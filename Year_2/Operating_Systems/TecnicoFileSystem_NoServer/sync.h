/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#ifndef SYNC_H
#define SYNC_H

#include <pthread.h>
#include <unistd.h>
#include <semaphore.h>
#include "constants.h"

extern int numBuckets;

#ifdef RWLOCK
    #define syncMech                pthread_rwlock_t
    #define syncMech_alloc(a)       malloc(sizeof(pthread_rwlock_t)*a)
    #define syncMech_init(a,b,i)    pthread_rwlock_init(&a[i],b)
    #define syncMech_destroy(a,i)   pthread_rwlock_destroy(&a[i])
    #define syncMech_wrlock(a,i)    pthread_rwlock_wrlock(&a[i])
    #define syncMech_rdlock(a,i)    pthread_rwlock_rdlock(&a[i])
    #define syncMech_unlock(a,i)    pthread_rwlock_unlock(&a[i])
    #define syncMech_try_lock(a,i)  pthread_rwlock_trywrlock(&a[i])
#elif MUTEX
    #define syncMech                pthread_mutex_t
    #define syncMech_alloc(a)       malloc(sizeof(pthread_mutex_t)*a)
    #define syncMech_init(a,b,i)    pthread_mutex_init(&a[i],b)
    #define syncMech_destroy(a,i)   pthread_mutex_destroy(&a[i])
    #define syncMech_wrlock(a,i)    pthread_mutex_lock(&a[i])
    #define syncMech_rdlock(a,i)    pthread_mutex_lock(&a[i])
    #define syncMech_unlock(a,i)    pthread_mutex_unlock(&a[i])
    #define syncMech_try_lock(a,i)  pthread_mutex_trylock(&a[i])
#else //Abstract Sequential
    #define syncMech                void*
    #define syncMech_alloc(a)       malloc(sizeof(void)*a)
    #define syncMech_init(a,b,i)    do_nothing(a)
    #define syncMech_destroy(a,i)   do_nothing(a)
    #define syncMech_wrlock(a,i)    do_nothing(a)
    #define syncMech_rdlock(a,i)    do_nothing(a)
    #define syncMech_unlock(a,i)    do_nothing(a)
    #define syncMech_try_lock(a,i)  do_nothing(a)
#endif

void init_BstLock(syncMech** sync);
void sync_init(syncMech* sync);
void sem_mut_toggle(pthread_mutex_t* comLock, sem_t* sem, char f);
void sync_destroy(syncMech* sync);
int sync_try_lock(syncMech* sync, int hash);
void sync_wrlock(syncMech* sync, int hashValue);
void sync_rdlock(syncMech* sync, int hashValue);
void sync_unlock(syncMech* sync, int hashValue);
void mutex_init(pthread_mutex_t* mutex);
void mutex_lock(pthread_mutex_t* mutex);
void mutex_unlock(pthread_mutex_t* mutex);
void mutex_destroy(pthread_mutex_t* mutex);
int do_nothing(void* a);

#endif /* SYNC_H */
