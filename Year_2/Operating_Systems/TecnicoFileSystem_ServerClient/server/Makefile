# Makefile, versao 1
# Sistemas Operativos, DEI/IST/ULisboa 2019-20

SOURCES = main.c fs.c sync.c file-manager.c
SOURCES+= lib/bst.c lib/inodes.c
OBJS_RWLOCK = $(SOURCES:%.c=%-rwlock.o)
OBJS = $(OBJS_RWLOCK)
CC   = gcc
LD   = gcc
CFLAGS =-Wall -std=gnu99 -I../ -g
LDFLAGS=-lm -pthread
TARGETS = tecnicofs

.PHONY: all clean

all: $(TARGETS)

$(TARGETS):
	$(LD) $(CFLAGS) $^ -o $@ $(LDFLAGS)

lib/bst-rwlock.o: lib/bst.c lib/bst.h
lib/inodes-rwlock.o: lib/inodes.c lib/inodes.h
fs-rwlock.o: fs.c fs.h lib/bst.h
file-manager-rwlock.o: file-manager.c file-manager.h
sync-rwlock.o: sync.c sync.h constants.h
main-rwlock.o: main.c fs.h constants.h lib/timer.h sync.h

tecnicofs: lib/bst-rwlock.o lib/inodes-rwlock.o fs-rwlock.o file-manager-rwlock.o sync-rwlock.o main-rwlock.o

%.o:
	$(CC) $(CFLAGS) -c -o $@ $<

clean:
	@echo Cleaning...
	rm -f $(OBJS) $(TARGETS)
