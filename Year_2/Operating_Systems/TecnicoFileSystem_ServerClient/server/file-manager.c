#include "file-manager.h"

void err(char *msg){
    perror(msg);
    exit(EXIT_FAILURE);
}

//initialize client array structure
client_* client_init(int sockfd){
    struct ucred ucred;
    unsigned int len = sizeof(struct ucred);

    client_* client = malloc(sizeof(client_));
    if(!client)
        err("Error: memory allocation for client\n");

    if (getsockopt(sockfd, SOL_SOCKET, SO_PEERCRED, &ucred, &len) < 0)
        err("Error: getsockopt\n");

    client->files = malloc(sizeof(struct opened_files)*MAX_OPENED_FILES);
    if(!client->files)
        err("Error: memory allocation for client file");

    for(int i = 0; i < MAX_OPENED_FILES; i++)
        client->files[i].file = -1;

    client->client_id = ucred.uid;
    return client;
}

int openFile(tecnicofs* fs, char* filename, int mode, client_* client){
    int inumber = lookup(fs,filename);
    int freespot = -1;
    uid_t client_id;
    enum permission owner, others, permission;

    if(inumber == -1)
        return TECNICOFS_ERROR_FILE_NOT_FOUND;

    if(inode_get(inumber,&client_id,&owner,&others,NULL,0) == -1)
        return TECNICOFS_ERROR_OTHER;

    if (client_id != client->client_id)
        permission = others;
    else
        permission = owner;

    if(permission != mode && permission != 3)
        return TECNICOFS_ERROR_PERMISSION_DENIED;

    for(int i = MAX_OPENED_FILES-1; i > -1; i--){
        if(client->files[i].file == -1)
            freespot = i;
        if(client->files[i].file == inumber)
            return TECNICOFS_ERROR_FILE_IS_OPEN;
        if(i == 0 && freespot == -1)
        //reached end of loop && no free position -> MAX_OPENED_FILES reached
            return TECNICOFS_ERROR_MAXED_OPEN_FILES;
    }

    //increment number of clients who opened file
    if(inode_clientCounter(inumber, 'i') == -1)
        return TECNICOFS_ERROR_OTHER;

    client->files[freespot].file = inumber;
    client->files[freespot].mode = mode;

    return freespot;
}

int closeFile(tecnicofs* fs, int pos, client_* client){
    if(client->files[pos].file == -1)
        return TECNICOFS_ERROR_FILE_NOT_OPEN;

    //decrement number of clients who opened file
    if(inode_clientCounter(client->files[pos].file, 'd') == -1)
        return TECNICOFS_ERROR_OTHER;

    client->files[pos].file = -1;

    return 0;
}

int readFile(tecnicofs* fs, int pos, int len, client_* client, char* content){
    if(client->files[pos].file == -1)
        return TECNICOFS_ERROR_FILE_NOT_OPEN;

    if(client->files[pos].mode < 2)
        return TECNICOFS_ERROR_INVALID_MODE;

    if(inode_get(client->files[pos].file,NULL,NULL,NULL,content,len-1) == -1)
        return TECNICOFS_ERROR_OTHER;

    return strlen(content);
}

int writeFile(tecnicofs* fs, int pos, char* content, client_* client){
    if(client->files[pos].file == -1)
        return TECNICOFS_ERROR_FILE_NOT_OPEN;

    if(client->files[pos].mode != 1 && client->files[pos].mode != 3)
        return TECNICOFS_ERROR_INVALID_MODE;
        
    if(inode_set(client->files[pos].file,content,strlen(content)) == -1)
        return TECNICOFS_ERROR_OTHER;

    return 0;
}
