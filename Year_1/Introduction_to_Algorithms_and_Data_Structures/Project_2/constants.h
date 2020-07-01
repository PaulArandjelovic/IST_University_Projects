/*
 * File:        constants.h
 * Author:      Pavle Arandjelovic
 * IST Number:  TP193745
 * Description: Database to manage friendly football games created in C.
 */
#ifndef CONSTANTS_H
#define CONSTANTS_H

/* Max length of a name -> game/team */
#define MAX_NAME 1024
/* Max amount of string inputs our system can take */
#define INPUT_SPLITS 3
/* Size of  hashtables -> game/team */
#define HASHTABLE_SIZE 9973
/* Max input size that can be read from stdin by our system */
#define MAX_INPUT MAX_NAME * 4

/* Stores all Game info */
typedef struct Game {
    char* name;
    char* team_1;
    char* team_2;
    int score[2];
} Game;

/* Game node in associated linked lists */
typedef struct GameNode {
    Game *data;
    struct GameNode *prev;
    struct GameNode *next;
} G_Node;

/* Stores all Team info */
typedef struct Team {
    char* name;
    int wins;
} Team;

/* Team Node in associated linked lists */
typedef struct TeamNode {
    Team *data;
    struct TeamNode *prev;
    struct TeamNode *next;
} T_Node;

#endif /* CONSTANTS_H */