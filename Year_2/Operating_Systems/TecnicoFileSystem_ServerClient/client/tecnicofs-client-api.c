#include "tecnicofs-client-api.h"


int sockfd;
struct sockaddr_un serv_addr;

void err(char *msg){
    perror(msg);
}

int tfsMount(char * address){
    if(tfsCheckConnection() == 0)
        return TECNICOFS_ERROR_OPEN_SESSION;

    int servlen;

    if ((sockfd = socket(AF_UNIX, SOCK_STREAM, 0)) < 0)
        err("client: can't open stream socket");

    bzero((char *)&serv_addr, sizeof(serv_addr));

    serv_addr.sun_family = AF_UNIX;
    strcpy(serv_addr.sun_path, address);
    servlen = strlen(serv_addr.sun_path) + sizeof(serv_addr.sun_family);

    if(connect(sockfd,(struct sockaddr*) &serv_addr, servlen) < 0)
        err("client: can't connect to server");

    return 0;
}

int tfsCreate(char *filename, permission ownerPermissions, permission othersPermissions){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char buffer[MAX_INPUT_SIZE];
    int returned_value;

    if(sprintf(buffer,"c %s %d%d", filename, ownerPermissions, othersPermissions) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer, sizeof(buffer)) < 0)
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsDelete(char *filename){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char buffer[MAX_INPUT_SIZE];
    int returned_value;

    if(sprintf(buffer, "d %s", filename) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer, sizeof(buffer)) < sizeof(buffer))
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsRename(char *filenameOld, char *filenameNew){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char buffer[MAX_INPUT_SIZE];
    int returned_value;

    if(sprintf(buffer, "r %s %s", filenameOld, filenameNew) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer, sizeof(buffer)) < 0)
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsOpen(char *filename, int mode){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char buffer[MAX_INPUT_SIZE];
    int returned_value;

    if(sprintf(buffer, "o %s %d", filename, mode) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer, sizeof(buffer)) < 0)
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsClose(int fd){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char buffer[MAX_INPUT_SIZE];
    int returned_value;

    if(sprintf(buffer, "x %d", fd) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer, sizeof(buffer)) < 0)
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsRead(int fd, char *buffer, int len){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char buffer2[MAX_INPUT_SIZE];
    int returned_value;

    if(sprintf(buffer2, "l %d %d", fd, len) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer2, sizeof(buffer2)) < 0)
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");
    if(read(sockfd, buffer, sizeof(buffer)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsWrite(int fd, char *buffer, int len){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    char content[len+1];
    char buffer2[MAX_INPUT_SIZE];
    int returned_value = 0;

    if(snprintf(content, len+1, "%s\n", buffer) < 0)
        err("Error: snprintf");
    if(sprintf(buffer2, "w %d %s", fd, content) < 0)
        err("Error: sprintf\n");
    if(write(sockfd, buffer2, sizeof(buffer2)) < 0)
        err("Error: write\n");
    if(read(sockfd, &returned_value, sizeof(returned_value)) < 0)
        err("Error: read\n");

    return ntohl(returned_value);
}

int tfsUnmount(){
    if(tfsCheckConnection() == -1)
        return TECNICOFS_ERROR_NO_OPEN_SESSION;

    if(write(sockfd, "q", sizeof(char)) < 0)
        err("Error: write\n");
    if(close(sockfd) < 0)
        err("Error: Close Socket");
    exit(0);
}

int tfsCheckConnection(){
    int error = 0;
    socklen_t len = sizeof (error);
    if (getsockopt (sockfd, SOL_SOCKET, SO_ERROR, &error, &len) != 0)
        return -1;
    return 0;
}
