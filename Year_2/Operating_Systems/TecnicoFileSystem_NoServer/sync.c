/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "semaphore.h"
#include "sync.h"
#include <stdio.h>
#include <errno.h>
#include <stdlib.h>

void init_BstLock(syncMech** sync){
    *sync = syncMech_alloc(numBuckets);
    if(!sync){
        perror("Error allocating lock");
        exit(EXIT_FAILURE);
    }
}

void sync_init(syncMech* sync){
    int ret;
    for(int i=0; i < numBuckets; i++){
        ret = syncMech_init(sync, NULL, i);
        if(ret != 0){
            perror("sync_init failed\n");
            exit(EXIT_FAILURE);
        }
    }
}

void sem_mut_toggle(pthread_mutex_t* comLock, sem_t* sem, char f){
    if(f == 'l'){//lock
        semaphoreSubSignal(sem);
        mutex_lock(comLock);
    }
    if(f == 'u'){//unlock
        mutex_unlock(comLock);
        semaphoreAddSignal(sem);
    }
}

void sync_destroy(syncMech* sync){
    int ret;
    for(int i=0; i < numBuckets; i++){
        ret = syncMech_destroy(sync, i);
        if(ret != 0){
            perror("sync_destroy failed\n");
            exit(EXIT_FAILURE);
        }
    }
}

int sync_try_lock(syncMech* sync, int hash) {
    int ret = syncMech_try_lock(sync, hash);
    if(ret == EBUSY)    //return 1 if mutex already locked
        return 1;
    if(ret == 0)    //return 0 if successfully locked
        return 0;
    perror("sync try_lock failed"); //exit program if lock has failed and not busy
    exit(EXIT_FAILURE);
}

void sync_wrlock(syncMech* sync, int hashValue){
    int ret = syncMech_wrlock(sync, hashValue);
    if(ret != 0){
        perror("sync_wrlock failed");
        exit(EXIT_FAILURE);
    }
}

void sync_rdlock(syncMech* sync, int hashValue){
    int ret = syncMech_rdlock(sync, hashValue);
    if(ret != 0){
        perror("sync_rdlock failed");
        exit(EXIT_FAILURE);
    }
}

void sync_unlock(syncMech* sync, int hashValue){
    int ret = syncMech_unlock(sync, hashValue);
    if(ret != 0){
        perror("sync_unlock failed");
        exit(EXIT_FAILURE);
    }
}

void mutex_init(pthread_mutex_t* mutex){
    #if defined (RWLOCK) || defined (MUTEX)
        int ret = pthread_mutex_init(mutex, NULL);
        if(ret != 0){
            perror("mutex_init failed\n");
            exit(EXIT_FAILURE);
        }
    #endif
}

void mutex_destroy(pthread_mutex_t* mutex){
    #if defined (RWLOCK) || defined (MUTEX)
        int ret = pthread_mutex_destroy(mutex);
        if(ret != 0){
            perror("mutex_destroy failed\n");
            exit(EXIT_FAILURE);
        }
    #endif
}

void mutex_lock(pthread_mutex_t* mutex){
    #if defined (RWLOCK) || defined (MUTEX)
        int ret = pthread_mutex_lock(mutex);
        if(ret != 0){
            perror("mutex_lock failed");
            exit(EXIT_FAILURE);
        }
    #endif
}

void mutex_unlock(pthread_mutex_t* mutex){
    #if defined (RWLOCK) || defined (MUTEX)
        int ret = pthread_mutex_unlock(mutex);
        if(ret != 0){
            perror("mutex_unlock failed");
            exit(EXIT_FAILURE);
        }
     #endif
}

int do_nothing(void* a){
    (void)a;
    return 0;
}
