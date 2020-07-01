#ifndef TECNICOFS_CLIENT_API_H
#define TECNICOFS_CLIENT_API_H


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <arpa/inet.h>

#include "tecnicofs-api-constants.h"
#include "constants.h"


int tfsCreate(char *filename, permission ownerPermissions, permission othersPermissions);
int tfsDelete(char *filename);
int tfsRename(char *filenameOld, char *filenameNew);
int tfsOpen(char *filename, int mode);
int tfsClose(int fd);
int tfsRead(int fd, char *buffer, int len);
int tfsWrite(int fd, char *buffer, int len);
int tfsMount(char * address);
int tfsUnmount();
int tfsCheckConnection();

#endif /* TECNICOFS_CLIENT_API_H */
