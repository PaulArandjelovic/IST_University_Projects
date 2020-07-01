/* Sistemas Operativos, DEI/IST/ULisboa 2019-20 */

#include "lib/inodes.h"
#include "fs.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>

tecnicofs* new_tecnicofs(){
	tecnicofs*fs = malloc(sizeof(tecnicofs));
	if (!fs) {
		perror("failed to allocate tecnicofs");
		exit(EXIT_FAILURE);
	}
	fs->bstTree = NULL;
	sync_init(&(fs->bstLock));
	return fs;
}

void free_tecnicofs(tecnicofs* fs){
	free_tree(fs->bstTree);
	sync_destroy(&(fs->bstLock));
	free(fs);
}

int create(tecnicofs* fs, char *name, char *permissions, uid_t client){
	sync_wrlock(&(fs->bstLock));

	node* search_iNumber = search(fs->bstTree, name);
	if (search_iNumber){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_FILE_ALREADY_EXISTS;
	}

	enum permission owner_permission = atoi(permissions) / 10;
	enum permission others_permission = atoi(permissions) % 10;
	int iNumber = inode_create(client, owner_permission, others_permission);
	if(iNumber == -1){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_OTHER;
	}

	fs->bstTree = insert(fs->bstTree, name, iNumber);

	sync_unlock(&(fs->bstLock));
	return 0;
}

int delete(tecnicofs* fs, char *name, uid_t client){
	sync_wrlock(&(fs->bstLock));

	node* search_iNumber = search(fs->bstTree, name);
	if (!search_iNumber){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_FILE_NOT_FOUND;
	}

	int iNumber = search_iNumber->inumber;
	uid_t old_client;
	if(inode_get(iNumber, &old_client,NULL,NULL,NULL,0) == -1){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_OTHER;
	}
	if(client != old_client){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_PERMISSION_DENIED;
	}

	//counter == 0 to alter file (no clients have file opened), deletes
	//counter > 0 -> file open by x clients, do nothing
	int counter = inode_clientCounter(iNumber,'r');
	if(inode_clientCounter(iNumber,'r') == -1){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_OTHER;
	}else if (counter != 0){
		sync_unlock(&(fs->bstLock));
		return 0;
	}

	fs->bstTree = remove_item(fs->bstTree, name);
	if(inode_delete(iNumber) == -1){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_OTHER;
	}

	sync_unlock(&(fs->bstLock));
	return 0;
}

int renameFile(tecnicofs* fs, char *name, char *new_name, uid_t client){
	sync_wrlock(&(fs->bstLock));

	node* search_iNumber = search(fs->bstTree, name);
	if (!search_iNumber){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_FILE_NOT_FOUND;
	}
	if(search(fs->bstTree, new_name)){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_FILE_ALREADY_EXISTS;
	}

	int iNumber = search_iNumber->inumber;
	uid_t old_client;
	if(inode_get(iNumber,&old_client,NULL,NULL,NULL,0) == -1){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_OTHER;
	}
	if(client != old_client){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_PERMISSION_DENIED;
	}

	//counter == 0 to alter file (no clients have file opened), renames
	//counter > 0 -> file open by x clients, do nothing
	int counter = inode_clientCounter(iNumber,'r');
	if(inode_clientCounter(iNumber,'r') == -1){
		sync_unlock(&(fs->bstLock));
		return TECNICOFS_ERROR_OTHER;
	}else if (counter != 0){
		sync_unlock(&(fs->bstLock));
		return 0;
	}

	fs->bstTree = remove_item(fs->bstTree, name);
	fs->bstTree = insert(fs->bstTree, new_name, iNumber);

	sync_unlock(&(fs->bstLock));
	return 0;
}

int lookup(tecnicofs* fs, char *name){
	int inumber = -1;
	sync_rdlock(&(fs->bstLock));
	node* searchNode = search(fs->bstTree, name);
	if ( searchNode ) {
		inumber = searchNode->inumber;
	}
	sync_unlock(&(fs->bstLock));
	return inumber;
}

void print_tecnicofs_tree(FILE * fp, tecnicofs *fs){
	print_tree(fp, fs->bstTree);
}
