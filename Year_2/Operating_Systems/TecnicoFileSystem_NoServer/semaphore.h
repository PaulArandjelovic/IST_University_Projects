/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#ifndef SEMAPHORE_H
#define SEMAPHORE_H

#include "constants.h"
#include <semaphore.h>

sem_t producerFreeToAdd;
sem_t consumerFreeToExecute;

void semaphore_destroy(sem_t* sem);
void init_semaphore(sem_t* sem, int x);
void semaphoreAddSignal(sem_t* sem);
void semaphoreSubSignal(sem_t* sem);

#endif /* FS_H */
