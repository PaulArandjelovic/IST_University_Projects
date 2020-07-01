/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#ifndef FS_H
#define FS_H
#include "lib/bst.h"
#include "sync.h"
#include "lib/inodes.h"

typedef struct tecnicofs {
    node*   bstTree;
    syncMech bstLock;
}tecnicofs;

tecnicofs* new_tecnicofs();
void free_tecnicofs(tecnicofs* fs);
int create(tecnicofs* fs, char *names, char *permissions, uid_t client);
int delete(tecnicofs* fs, char *name, uid_t client);
int renameFile(tecnicofs* fs, char *name, char *new_name, uid_t client);
int lookup(tecnicofs* fs, char *name);
void print_tecnicofs_tree(FILE * fp, tecnicofs *fs);

#endif /* FS_H */
