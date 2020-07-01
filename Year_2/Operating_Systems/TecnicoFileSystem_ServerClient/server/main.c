/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <arpa/inet.h>

#include "fs.h"
#include "constants.h"
#include "lib/timer.h"
#include "sync.h"
#include "lib/inodes.h"
#include "file-manager.h"


tecnicofs* fs;
int termination_flag = 0;


void error(char *msg){
    perror(msg);
    exit(EXIT_FAILURE);
}

static void displayUsage (const char* appName){
    printf("Usage: %s socket_name output_filepath numbuckets\n", appName);
    exit(EXIT_FAILURE);
}

static void parseArgs (long argc, char* const argv[]){
    if (argc != 4) {
        fprintf(stderr, "Invalid format:\n");
        displayUsage(argv[0]);
    }

    if(atoi(argv[3]) != 1){
        fprintf(stderr, "Invalid number of buckets\n");
        displayUsage(argv[0]);
    }
}

void* applyCommands(void* fd){
    int newsockfd = *(int*) fd;
    char token;
    sigset_t   signal_mask;

    if(sigemptyset (&signal_mask) != 0)
        error("Error: sigemptyset\n");
    if(sigaddset (&signal_mask, SIGINT) != 0)
        error("Error: sigaddset\n");
    if(pthread_sigmask(SIG_BLOCK, &signal_mask, NULL) != 0)
        error("Error: sigmask\n");

    client_* client = client_init(newsockfd);

    while(1){
        char buffer[MAX_INPUT_SIZE];
        char string[2][MAX_INPUT_SIZE];

        int n = read(newsockfd, buffer, sizeof(buffer));
        if(n == 0)
            continue;
        if(n < 0)
            error("Error: Read from Buffer\n");

        sscanf(buffer, "%c %s %s", &token, string[0], string[1]);
        int return_value;
        int converted_return;
        char* return_string;

        switch (token) {
            case 'c':;
                return_value = create(fs, string[0], string[1], client->client_id);
                break;
            case 'd':;
                return_value = delete(fs, string[0], client->client_id);
                break;
            case 'r':;
                return_value = renameFile(fs, string[0], string[1], client->client_id);
                break;
            case 'o':
                return_value = openFile(fs, string[0], atoi(string[1]), client);
                break;
            case 'x':
                return_value = closeFile(fs, atoi(string[0]), client);
                break;
            case 'l':
                return_string = malloc(sizeof(char)*(atoi(string[1])+2));
                return_value = readFile(fs, atoi(string[0]), atoi(string[1]), client, return_string);
                break;
            case 'w':;
                return_value = writeFile(fs, atoi(string[0]), string[1], client);
                break;
            case 'q': //prepare client for unmount
                if(close(newsockfd) < 0)
                    error("Error: Close Client Socket");
                free(client->files);
                free(client);
                return NULL;
            default: { /* error */
                fprintf(stderr, "Error: commands to apply\n");
                exit(EXIT_FAILURE);
            }
        }
        converted_return = htonl(return_value);
        if(write(newsockfd, &converted_return, sizeof(converted_return)) < 0)
            error("Error: write\n");
        if (token == 'l'){
            if(write(newsockfd,return_string, sizeof(return_string)) < 0)
                error("Error: write\n");
            free(return_string);
        }

    }
    return NULL;
}

void terminationSig(int s) {
    termination_flag = 1;
}

void run_tecnico_server(char* socketName){
    int sockfd, clilen, servlen;
    struct sockaddr_un cli_addr, serv_addr;

    if ((sockfd = socket(AF_UNIX,SOCK_STREAM,0)) < 0)
        error("Error: Can't open socket stream\n");

    unlink(socketName);
    bzero((char *)&serv_addr, sizeof(serv_addr));

    serv_addr.sun_family = AF_UNIX;
    strcpy(serv_addr.sun_path, socketName);
    servlen = strlen(serv_addr.sun_path) + sizeof(serv_addr.sun_family);
    if (bind(sockfd, (struct sockaddr*) &serv_addr, servlen) < 0)
        error("Error: Bind Address\n");

    if(listen(sockfd, 5) != 0)
        error("Error: Listen\n");

    int clientCount = 0;
    int newsockfd;
    pthread_t* clients = malloc(sizeof(pthread_t) * 10);

    for(;;){
        if((clientCount % 10) == 0){
            clients = realloc(clients, sizeof(pthread_t) * (clientCount + 10));
            if(!clients)
                error("Error: Reallocation of clients\n");
        }

        clilen = sizeof(cli_addr);
        newsockfd = accept(sockfd, (struct sockaddr*) &cli_addr, (socklen_t*) &clilen);
        //termination signal called (SIGINT) -- flag == 1 -> terminate
        if(termination_flag)
            break;
        if(newsockfd < 0)
            error("Error: Accept\n");

        if(pthread_create(&clients[clientCount], NULL, applyCommands, (void*)&newsockfd) != 0)
            error("Error: Client Thread Creation Failed\n");

        clientCount++;
    }

    for (int i = 0; i < clientCount; i++)
        if(pthread_join(clients[i],NULL) != 0)
            error("Server: cannot join client thread\n");

    free(clients);
    if(close(sockfd) <0)
        error("Error: close socket\n");
    printf("\n");
}

int main(int argc, char* argv[]) {
    TIMER_T startTime, stopTime;
    struct sigaction act;

    parseArgs(argc, argv);

    memset (&act,0, sizeof(act));
    act.sa_handler = terminationSig;
    sigaction (SIGINT, &act , NULL);

    FILE * outputFp = fopen(argv[2],"w");
    if (!outputFp)
        error("Can't open output file\n");
    fs = new_tecnicofs();
    inode_table_init();

    TIMER_READ(startTime);
    run_tecnico_server(argv[1]);
    TIMER_READ(stopTime);
    printf("TecnicoFS completed in %.4f seconds.\n", TIMER_DIFF_SECONDS(startTime, stopTime));

    print_tecnicofs_tree(outputFp, fs);

    inode_table_destroy();
    free_tecnicofs(fs);
    fflush(outputFp);
    fclose(outputFp);

    exit(EXIT_SUCCESS);
}
