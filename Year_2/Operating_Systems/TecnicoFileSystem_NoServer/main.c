/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include "fs.h"
#include "constants.h"
#include "lib/timer.h"
#include "sync.h"


char* global_inputFile = NULL;
char* global_outputFile = NULL;
int numberThreads = 0;
int numBuckets = 0;
pthread_mutex_t commandsLock;
tecnicofs* fs;

char inputCommands[MAX_COMMANDS][MAX_INPUT_SIZE];
int numberCommands = 0;
int headQueue = 0;


static void displayUsage (const char* appName){
    printf("Usage: %s input_filepath output_filepath threads_number numbuckets\n", appName);
    exit(EXIT_FAILURE);
}

static void parseArgs (long argc, char* const argv[]){
    if (argc != 5) {
        fprintf(stderr, "Invalid format:\n");
        displayUsage(argv[0]);
    }

    global_inputFile = argv[1];
    global_outputFile = argv[2];
    numberThreads = atoi(argv[3]);
    numBuckets = atoi(argv[4]);
    if (numberThreads <= 0) {
        fprintf(stderr, "Invalid number of threads\n");
        displayUsage(argv[0]);
    }
    if(numBuckets <= 0){
        fprintf(stderr, "Invalid number of buckets\n");
        displayUsage(argv[0]);
    }
}

int insertCommand(char* data) {
    strcpy(inputCommands[numberCommands], data);
    numberCommands = (numberCommands + 1) % MAX_COMMANDS;
    return 1;
}

char* removeCommand() {
    char* c = inputCommands[headQueue];
    if(strcmp(c, "q") != 0)
        headQueue = (headQueue + 1) % MAX_COMMANDS;
    else
        semaphoreAddSignal(&consumerFreeToExecute); // last command to apply 'q'
    return c;
}

void errorParse(int lineNumber){
    fprintf(stderr, "Error: line %d invalid\n", lineNumber);
    exit(EXIT_FAILURE);
}

void* processInput(){
    FILE* inputFile;
    inputFile = fopen(global_inputFile, "r");
    if(!inputFile){
        fprintf(stderr, "Error: Could not read %s\n", global_inputFile);
        exit(EXIT_FAILURE);
    }

    char line[MAX_INPUT_SIZE];
    int lineNumber = 0;

    while (fgets(line, sizeof(line)/sizeof(char), inputFile)) {
        char token;
        char name[MAX_INPUT_SIZE];
        char new_name[MAX_INPUT_SIZE];
        lineNumber++;

        int numTokens = sscanf(line, "%c %s %s", &token, name, new_name);

        /* perform minimal validation */
        if (numTokens < 1) {
            continue;
        }
        switch (token) {
            case 'c':
            case 'l':
            case 'd':
            case 'r':
                if((token == 'r' && numTokens != 3)||(token != 'r' && numTokens != 2))
                    errorParse(lineNumber);
                sem_mut_toggle(&commandsLock, &producerFreeToAdd, 'l');
                insertCommand(line);
                sem_mut_toggle(&commandsLock, &consumerFreeToExecute, 'u');
                break;
            case '#':
                break;
            default: { /* error */
                errorParse(lineNumber);
            }
        }
    }
    sem_mut_toggle(&commandsLock, &producerFreeToAdd, 'l');
    insertCommand("q"); //file read terminated
    sem_mut_toggle(&commandsLock, &consumerFreeToExecute, 'u');

    fclose(inputFile);
    return NULL;
}

FILE * openOutputFile() {
    FILE *fp;
    fp = fopen(global_outputFile, "w");
    if (fp == NULL) {
        perror("Error opening output file");
        exit(EXIT_FAILURE);
    }
    return fp;
}

void* applyCommands(){
    while(1){
        sem_mut_toggle(&commandsLock, &consumerFreeToExecute, 'l');

        const char* command = removeCommand();
        if (command == NULL){
            mutex_unlock(&commandsLock);
            continue;
        }
        char token;
        char name[MAX_INPUT_SIZE];
        char new_name[MAX_INPUT_SIZE];
        sscanf(command, "%c %s %s", &token, name, new_name);

        int iNumber;
        if('c' == token)
            iNumber = obtainNewInumber(fs);

        sem_mut_toggle(&commandsLock, &producerFreeToAdd, 'u');

        switch (token) {
            case 'c':
                create(fs, name, iNumber);
                break;
            case 'l': ;//this is an empty statement
                int searchResult = lookup(fs, name);
                if(!searchResult)
                    printf("%s not found\n", name);
                else
                    printf("%s found with inumber %d\n", name, searchResult);
                break;
            case 'd':
                delete(fs, name);
                break;
            case 'r':
                renameFile(fs, name, new_name);
                break;
            case 'q': //read termination command
                return NULL;
            default: { /* error */
                fprintf(stderr, "Error: commands to apply\n");
                exit(EXIT_FAILURE);
            }
        }
    }
    return NULL;
}

void runThreads(FILE* timeFp){
    pthread_t process_thread;
    TIMER_T startTime, stopTime;

    TIMER_READ(startTime);
    if(pthread_create(&process_thread, NULL, processInput, NULL)){
        perror("Can't create process_thread");
        exit(EXIT_FAILURE);
    }

    #if defined (RWLOCK) || defined (MUTEX)
        pthread_t* workers = (pthread_t*) malloc(numberThreads * sizeof(pthread_t));

        for(int i = 0; i < numberThreads; i++){
            int err = pthread_create(&workers[i], NULL, applyCommands, NULL);
            if (err != 0){
                perror("Can't create thread");
                exit(EXIT_FAILURE);
            }
        }
        for(int i = 0; i < numberThreads; i++) {
            if(pthread_join(workers[i], NULL)) {
                perror("Can't join thread");
            }
        }
    #else
        applyCommands();
    #endif

    if(pthread_join(process_thread, NULL)) {
        perror("Can't join thread");
    }

    TIMER_READ(stopTime);
    fprintf(timeFp, "TecnicoFS completed in %.4f seconds.\n", TIMER_DIFF_SECONDS(startTime, stopTime));
    #if defined (RWLOCK) || defined (MUTEX)
        free(workers);
    #endif
}


int main(int argc, char* argv[]) {
    parseArgs(argc, argv);

    init_semaphore(&producerFreeToAdd, MAX_COMMANDS);
    init_semaphore(&consumerFreeToExecute, 0);
    mutex_init(&commandsLock);

    FILE * outputFp = openOutputFile();
    fs = new_tecnicofs();

    runThreads(stdout);
    print_tecnicofs_tree(outputFp, fs);

    free_tecnicofs(fs);
    fflush(outputFp);
    fclose(outputFp);

    mutex_destroy(&commandsLock);
    semaphore_destroy(&consumerFreeToExecute);
    semaphore_destroy(&producerFreeToAdd);
    
    exit(EXIT_SUCCESS);
}
