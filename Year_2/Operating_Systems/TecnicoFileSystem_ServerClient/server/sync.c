/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "sync.h"
#include <stdio.h>
#include <stdlib.h>

void sync_init(syncMech* sync){
    int ret = syncMech_init(sync, NULL);
    if(ret != 0){
        perror("sync_init failed\n");
        exit(EXIT_FAILURE);
    }
}

void sync_destroy(syncMech* sync){
    int ret = syncMech_destroy(sync);
    if(ret != 0){
        perror("sync_destroy failed\n");
        exit(EXIT_FAILURE);
    }
}

void sync_wrlock(syncMech* sync){
    int ret = syncMech_wrlock(sync);
    if(ret != 0){
        perror("sync_wrlock failed");
        exit(EXIT_FAILURE);
    }
}

void sync_rdlock(syncMech* sync){
    int ret = syncMech_rdlock(sync);
    if(ret != 0){
        perror("sync_rdlock failed");
        exit(EXIT_FAILURE);
    }
}

void sync_unlock(syncMech* sync){
    int ret = syncMech_unlock(sync);
    if(ret != 0){
        perror("sync_unlock failed");
        exit(EXIT_FAILURE);
    }
}
