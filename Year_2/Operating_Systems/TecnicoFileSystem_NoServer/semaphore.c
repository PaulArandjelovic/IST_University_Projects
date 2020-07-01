/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "semaphore.h"
#include <stdio.h>
#include <stdlib.h>


void semaphore_destroy(sem_t* sem){
  if(sem_destroy(sem) != 0){
    perror("Failed to destroy semaphore");
    exit(EXIT_FAILURE);
  }
}

void init_semaphore(sem_t* sem, int x){
  if((sem_init(sem, 0, x) != 0)){
    perror("Failed to initialize semaphore");
    exit(EXIT_FAILURE);
  }
}

void semaphoreAddSignal(sem_t* sem) {
  if(sem_post(sem) != 0){
    perror("sem_post failed");
    exit(EXIT_FAILURE);
  }
}

void semaphoreSubSignal(sem_t* sem) {
  if(sem_wait(sem) != 0){
    perror("sem_wait failed");
    exit(EXIT_FAILURE);
  }
}