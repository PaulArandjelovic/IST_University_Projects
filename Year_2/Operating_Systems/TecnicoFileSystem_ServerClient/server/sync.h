/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#ifndef SYNC_H
#define SYNC_H

#include <pthread.h>
#include <unistd.h>
#include "constants.h"


#define syncMech              pthread_rwlock_t
#define syncMech_init(a,b)    pthread_rwlock_init(a,b)
#define syncMech_destroy(a)   pthread_rwlock_destroy(a)
#define syncMech_wrlock(a)    pthread_rwlock_wrlock(a)
#define syncMech_rdlock(a)    pthread_rwlock_rdlock(a)
#define syncMech_unlock(a)    pthread_rwlock_unlock(a)


void sync_init(syncMech* sync);
void sync_destroy(syncMech* sync);
void sync_wrlock(syncMech* sync);
void sync_rdlock(syncMech* sync);
void sync_unlock(syncMech* sync);

#endif /* SYNC_H */
