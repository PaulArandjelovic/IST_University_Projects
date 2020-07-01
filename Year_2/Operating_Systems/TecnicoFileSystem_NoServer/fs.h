/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#ifndef FS_H
#define FS_H
#include "lib/bst.h"
#include "sync.h"
#include "lib/hash.h"
#include "semaphore.h"

extern int numBuckets;

typedef struct tecnicofs {
    node** hashtable;
    int nextINumber;
    syncMech* bstLock;
}tecnicofs;

void initHashtable(node*** hashtable);
int obtainNewInumber(tecnicofs* fs);
tecnicofs* new_tecnicofs();
void free_tecnicofs(tecnicofs* fs);
void create(tecnicofs* fs, char *name, int inumber);
void delete(tecnicofs* fs, char *name);
void renameFile(tecnicofs* fs, char *name, char *new_name);
int lookup(tecnicofs* fs, char *name);
void print_tecnicofs_tree(FILE * fp, tecnicofs *fs);

#endif /* FS_H */