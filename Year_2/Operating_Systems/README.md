# Tecnico_File_Server

These two projects were part of an Operating Systems course at Instituto Superior Técnico (2019) to deepen our understanding of modern operating systems and filesystems.

There are two types of filesystems here:

* TecnicoFileSystem_NoServer
* TecnicoFileSystem_ServerClient

**NOTE - THE MAKEFILE NEEDS TO COMPILE ALL FILES BEFORE EXECUTING EXECUTABLES**

## TecnicoFileSystem_NoServer

This project was created as a way to show how various threads (through atomic operations using mutexes and semaphores) and entries in a hashtable can speed up a program considerably. One thread reads continuously reads input as a created threadpool processes the input read simultaneously. We compare the speed of three executables:
* nosync (fixed to one thread and no locks)
* mutex (variable number of threads and only mutex locks)
* rwlock (variable number of threads and read/write locks)

### Makefile
All commands run from terminal with "make" preceeding it

* all (first command, can be executed soley by entering command "make" into terminal) -> compiles all necessary files and creates 3 executables.
* clean -> clean/delete all objects (.o) files from compilation

### Run
The following arguments are passed through the command line to run a program:
* **_program_name_**  -- program to execute -> *tecnicofs-nosync* || *tecnicofs-mutex* || *tecnicofs-rwlock*
* **_inputfile_**     -- file containing commands and filenames (.txt)
* **_outputFile_**    -- output file containing visual representation of binary tree(s) storing all files (.txt)
* **_numberThreads_** -- positive integer depicting how many threads will be used to process commands read from the file (this                                     number is fixed to one when running the nosync variant).
* **_numBuckets_**    -- number of entries in our hashtable storing our files in various binary trees.

To run one of the three programs without error, the arguments need to be entered in the following format:

* **nosync** -> ./tecnicofs-nosync inputFile outputFile numberThreads(1) numBuckets
* **mutex** -> ./tecnicofs-mutex inputFile outputFile numberThreads numBuckets
* **rwlock** -> ./tecnicofs-rwlock inputFile outputFile numberThreads numBuckets

## TecnicoFileSystem_ServerClient

This project was created as a way to deepen our knowledge in working with signals and understanding communication between processes; in this case, a server and various clients. One thread runs the server-side of our application and a new thread is created whenever a new client connects to our server.

### Makefile
All commands run from terminal with "make" preceeding it

* all (first command, can be executed soley by entering command "make" into terminal) -> compiles all necessary files and creates an executable to run the server-side from.
* clean -> clean/delete all objects (.o) files from compilation

### Run
Required input for TecnicoFileSystem_ServerClient:

* **_program_name_**  -- program to execute -> *tecnicofs-nosync*
* **_scoket_name_**   -- socket name for the server-side socket
* **_outputFile_**    -- output file containing visual representation of binary tree storing all files (.txt)
* **_numBuckets_**    -- number of entries in our hashtable storing our files in various binary trees (currently set to one for simplicity).

./tecnicofs socket_name output_filepath numBuckets
