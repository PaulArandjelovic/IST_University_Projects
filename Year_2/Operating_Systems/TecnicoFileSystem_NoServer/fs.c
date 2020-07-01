/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "fs.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>

int obtainNewInumber(tecnicofs* fs) {
	int newInumber = ++(fs->nextINumber);
	return newInumber;
}

void initHashtable(node*** hashtable){
	*hashtable = calloc(numBuckets, sizeof(node*));
	if(!hashtable){
		perror("Error allocating hashtable");
		exit(EXIT_FAILURE);
	}
}

tecnicofs* new_tecnicofs(){
	tecnicofs*fs = malloc(sizeof(tecnicofs));
	if (!fs) {
		perror("failed to allocate tecnicofs");
		exit(EXIT_FAILURE);
	}

	initHashtable(&fs->hashtable);
	init_BstLock(&fs->bstLock);
	sync_init(fs->bstLock);
	fs->nextINumber = 0;
	return fs;
}


void free_tecnicofs(tecnicofs* fs){
	for(int i = 0; i < numBuckets; i++)
		free_tree(fs->hashtable[i]);

	sync_destroy((fs->bstLock));
	free(fs->bstLock);
	free(fs->hashtable);
	free(fs);
}

void create(tecnicofs* fs, char *name, int inumber){
	int hashValue = hash(name, numBuckets);
	sync_wrlock((fs->bstLock), hashValue);
	fs->hashtable[hashValue] = insert(fs->hashtable[hashValue], name, inumber);
	sync_unlock((fs->bstLock), hashValue);
}

void delete(tecnicofs* fs, char *name){
	int hashValue = hash(name, numBuckets);
	sync_wrlock((fs->bstLock), hashValue);
	fs->hashtable[hashValue] = remove_item(fs->hashtable[hashValue], name);
	sync_unlock((fs->bstLock), hashValue);
}

void renameFile(tecnicofs* fs, char *name, char *new_name){
	int oldHash = hash(name, numBuckets);
	int newHash = hash(new_name, numBuckets);
	int randCnst = 1, keep_looping = 1;
	int iNumber = 0; int r;

	while(keep_looping){
		r = rand() % (2 * (randCnst)); // r between 0 and 2*randCnst
		sleep(1E-6*r); // thread sleeps for a random amount of time in case two threads are trying to lock the same tree at the same time
		if(!sync_try_lock(fs->bstLock, oldHash)){
			if(oldHash==newHash || !sync_try_lock(fs->bstLock, newHash))
				keep_looping = 0;
			else
				syncMech_unlock(fs->bstLock, oldHash);
		}
		randCnst++; // max value for randomizer
	}

	node* search_iNumber = search(fs->hashtable[oldHash], name);
	if (search_iNumber && !search(fs->hashtable[newHash], new_name)){
		fs->hashtable[oldHash] = remove_item(fs->hashtable[oldHash], name);
		fs->hashtable[newHash] = insert(fs->hashtable[newHash], new_name, iNumber);
	}

	syncMech_unlock(fs->bstLock, newHash);
	if(oldHash!=newHash)
		syncMech_unlock(fs->bstLock, oldHash);
	return;
}

int lookup(tecnicofs* fs, char *name){
	int hashValue = hash(name, numBuckets);
	int inumber = 0;

	sync_rdlock((fs->bstLock), hashValue);
	node* searchNode = search(fs->hashtable[hashValue], name);
	if ( searchNode ) {
		inumber = searchNode->inumber;
	}
	sync_unlock((fs->bstLock), hashValue);
	return inumber;
}

void print_tecnicofs_tree(FILE * fp, tecnicofs *fs){
	for(int i=0; i < numBuckets; i++)
		if(fs->hashtable[i])
			print_tree(fp, fs->hashtable[i]);
}
