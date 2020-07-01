#ifndef FILE_MANAGER_H
#define _GNU_SOURCE

#include "lib/inodes.h"
#include "fs.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>


struct opened_files{
    int file;
    int mode;
};

typedef struct client_files{
    struct opened_files* files;
    uid_t client_id;
}client_;


client_* client_init(int sockfd);
int openFile(tecnicofs* fs, char* filename, int mode, client_* client);
int closeFile(tecnicofs* fs, int pos, client_* client);
int readFile(tecnicofs* fs, int pos, int len, client_* client, char* content);
int writeFile(tecnicofs* fs, int pos, char* content, client_* client);

#endif
