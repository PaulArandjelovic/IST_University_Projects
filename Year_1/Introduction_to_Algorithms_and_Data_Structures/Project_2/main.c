/*
 * File:        main.c
 * Author:      Pavle Arandjelovic
 * IST Number:  TP193745
 * Description: Database to manage friendly football games created in C.
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>
#include "constants.h"


/* hashing function based on djb2 string hashing algorithm */
unsigned long hash(char *str) {
    unsigned long hash = 5381;
    int c;

    while ((c = *str++))
        hash = ((hash << 5u) + hash) + c;
    return hash % HASHTABLE_SIZE;
}

/* Locate a node with a matching name in the given hashtable */
void* locate_node(char* name, void** hashtab) {
    /* assigning to type G_Node to be able to traverse the linked list */
    G_Node *hash_head = hashtab[hash(name)];

    while(hash_head) {
        if(!strcmp(name, hash_head->data->name))
            return hash_head;   /* Node found in hashtable */
        hash_head = hash_head->next;
    }
    return NULL;    /* Node not found in hashtable */
}

/* Insert in a node in alphabetical order into the given linked list */
void insert_in_list_order(T_Node **team_head, T_Node *new_node) {
    if (!*team_head)
        *team_head = new_node;
    else if (strcmp(new_node->data->name, (*team_head)->data->name) < 0) {
        new_node->next = *team_head;
        new_node->next->prev = new_node;
        *team_head = new_node;
    } else {
        T_Node *temp = *team_head;

        /* Find position to insert node into */
        while (temp->next && strcmp(new_node->data->name, temp->next->data->name) > 0)
            temp = temp->next;
        new_node->next = temp->next;

        if (temp->next)
            new_node->next->prev = new_node;

        temp->next = new_node;
        new_node->prev = temp;
    }
}

/* Push given node into given hashtable and list */
void insert_node(void **node_list, G_Node *new_node, G_Node** game_tail, bool is_list) {
    new_node->next = *node_list;

    if (!*node_list && is_list)
        *game_tail = new_node;
    if (*node_list)
        ((G_Node *)(*node_list))->prev = new_node;
    *node_list = new_node;
}

/* Decrement wins of a given team */
void remove_wins (Game *game, T_Node **t_hashtab) {
    if (game->score[0] > game->score[1])
        ((T_Node *) locate_node(game->team_1, (void *) t_hashtab))->data->wins--;
    if (game->score[0] < game->score[1])
        ((T_Node *) locate_node(game->team_2, (void *) t_hashtab))->data->wins--;
}

/* Increment wins of a given team */
void add_wins(Game *game, int new_score_1, int new_score_2, T_Node **t_hashtab) {
    if (new_score_1 > new_score_2)
        ((T_Node *) locate_node(game->team_1, (void *) t_hashtab))->data->wins++;
    if (new_score_1 < new_score_2)
        ((T_Node *) locate_node(game->team_2, (void *) t_hashtab))->data->wins++;
}

/* Alter score of a given game and update team wins */
void alter_score(char* game_name, int score_1, int score_2,
        G_Node **g_hashtab, T_Node **t_hashtab, int cmd_cnt) {
    G_Node *game_node;

    if ((game_node = (G_Node *) locate_node(game_name, (void *) g_hashtab))) {
        /* Remove wins of winning team */
        remove_wins(game_node->data, t_hashtab);
        /* Re-evaluate game and add wins to new winning team */
        add_wins(game_node->data, score_1, score_2, t_hashtab);
        game_node->data->score[0] = score_1;
        game_node->data->score[1] = score_2;
    } else
        printf("%d Jogo inexistente.\n", cmd_cnt);
}

/* Insert game into system (hashtable + list) */
void add_game(char* name, char* team_1, char* team_2, int score_1, int score_2,
        G_Node **g_hashtab, T_Node **t_hashtab, G_Node **g_head, G_Node **g_tail, int cmd_cnt) {
    T_Node *team_node_1, *team_node_2;

    if ((G_Node *) locate_node(name, (void *) g_hashtab))
        printf("%d Jogo existente.\n", cmd_cnt);
    else if (!(team_node_1 = (T_Node *) locate_node(team_1, (void *) t_hashtab))
          || !(team_node_2 = (T_Node *) locate_node(team_2, (void *) t_hashtab)))
        printf("%d Equipa inexistente.\n", cmd_cnt);
    else {
        Game *new_game = malloc(sizeof(Game));
        G_Node *hash_node = malloc(sizeof(G_Node));
        G_Node *list_node = malloc(sizeof(G_Node));

        new_game->name = malloc(sizeof(char) * strlen(name) + 1);

        strcpy(new_game->name, name);
        new_game->team_1 = team_node_1->data->name;
        new_game->team_2 = team_node_2->data->name;
        new_game->score[0] = score_1;
        new_game->score[1] = score_2;

        /* Node in hashtable references same game data as node in game list */
        hash_node->data = list_node->data = new_game;
        hash_node->next = list_node->next = NULL;
        hash_node->prev = list_node->prev = NULL;

        /* Evaluate game and add wins to winning team */
        add_wins(new_game, new_game->score[0], new_game->score[1], t_hashtab);
        insert_node((void **) &g_hashtab[hash(name)], (void *) hash_node, NULL, false);
        insert_node((void **) g_head, (void *) list_node, g_tail, true);
    }
}

/* Print given game to stdout */
void show_game(Game *game, int cmd_cnt) {
    printf("%d %s %s %s %d %d\n",
           cmd_cnt,
           game->name,
           game->team_1,
           game->team_2,
           game->score[0],
           game->score[1]
    );
}

/* Show all existing games, print to stdout */
void show_games(G_Node **g_tail, int cmd_cnt) {
    G_Node *game_node;

    for (game_node = *g_tail; game_node; game_node = game_node->prev)
        show_game(game_node->data, cmd_cnt);
}

/* Given the name of a game, find it and print to stdout */
void find_game(char* name, G_Node **g_hashtab, int cmd_cnt) {
    G_Node *game_node;

    if ((game_node = (G_Node *) locate_node(name, (void *) g_hashtab)))
        show_game(game_node->data, cmd_cnt);
    else
        printf("%d Jogo inexistente.\n", cmd_cnt);
}

/* Remove node from given linked list */
void remove_node(G_Node* game, void** head_ref, G_Node** tail_ref, bool is_lst) {
    if (is_lst && game == *tail_ref)
        *tail_ref = game->prev;
    if (*head_ref == game)
        *head_ref = game->next;
    if (game->next)
        game->next->prev = game->prev;
    if (game->prev)
        game->prev->next = game->next;
    free(game);
}

/* Remove game from database that matches with given name  (hashtable + list) */
void remove_game(char *name, G_Node **g_hashtab, T_Node **t_hashtab,
        G_Node **g_head, G_Node **g_tail, int cmd_cnt) {
    G_Node* game_hash_node;

    if ((game_hash_node = (G_Node *) locate_node(name, (void *) g_hashtab))) {
        G_Node* game_list_node = *g_head;

        /* Find node to remove in game list */
        while(game_list_node && strcmp(game_list_node->data->name, name))
            game_list_node=game_list_node->next;
        remove_wins(game_hash_node->data, t_hashtab);

        free(game_hash_node->data->name);
        free(game_hash_node->data);
        remove_node(game_hash_node, (void **) &g_hashtab[hash(name)], NULL, false);
        remove_node(game_list_node, (void **) g_head, g_tail, true);
    } else
        printf("%d Jogo inexistente.\n", cmd_cnt);
}

/* Add team to database (hashtable + list) */
void add_team(char* team_name, T_Node **t_hashtab, T_Node **t_head, int cmd_cnt) {
    if (!locate_node(team_name, (void *) t_hashtab)) {
        T_Node* team_hash_node = malloc(sizeof(T_Node));
        T_Node* team_list_node = malloc(sizeof(T_Node));
        Team *team = malloc(sizeof(Team));

        team->name = malloc(sizeof(char) * strlen(team_name) + 1);
        strcpy(team->name, team_name);
        team->wins = 0;

        team_hash_node->data = team_list_node->data = team;
        team_hash_node->next = team_list_node->next = NULL;
        team_hash_node->prev = team_list_node->prev = NULL;

        insert_node(
                (void **) &t_hashtab[hash(team_name)],
                (void *) team_hash_node, NULL, false
                );
        insert_in_list_order(t_head, team_list_node);
    } else
        printf("%d Equipa existente.\n", cmd_cnt);
}

/* Find the team with the given team name, print it to stdout */
void find_team(char* name, T_Node **t_hashtab, int cmd_cnt) {
    T_Node *team_node;

    if ((team_node = (T_Node *) locate_node(name, (void *) t_hashtab)))
        printf("%d %s %d\n",
               cmd_cnt,
               team_node->data->name,
               team_node->data->wins
        );
    else
        printf("%d Equipa inexistente.\n", cmd_cnt);
}

/* Find the team that has the most wins, print result to stdout */
void find_most_wins(T_Node **t_head, int cmd_cnt) {
    T_Node *head = *t_head;
    int max_wins = -1;

    while (head) {
        if (head->data->wins > max_wins)
            max_wins = head->data->wins;
        head = head->next;
    }
    head = *t_head;

    if (head)
        printf("%d Melhores %d\n", cmd_cnt, max_wins);
    while (head) {
        if (head->data->wins == max_wins)
            printf("%d * %s\n", cmd_cnt, head->data->name);
        head = head->next;
    }
}

/* free_memory helper function, free all memory allocated within given list and hashtable */
void free_memory_aux(void* lst_head, void** hashtab) {
    G_Node *node_hsh, *next, *node_lst = lst_head;

    while (node_lst) {
        next = node_lst->next;
        node_hsh = locate_node(node_lst->data->name, hashtab);
        if (hashtab[hash(node_lst->data->name)])
            remove_node(node_hsh, &hashtab[hash(node_lst->data->name)], NULL, false);
        free(node_lst->data->name);
        free(node_lst->data);
        free(node_lst);
        node_lst = next;
    }
}

/* Free all memory allocated in program */
void free_memory(G_Node** g_hashtab, T_Node** t_hashtab, G_Node* g_head, T_Node* g_tail) {
    free_memory_aux(g_head, (void **) g_hashtab);
    free_memory_aux(g_tail, (void **) t_hashtab);

    free(g_hashtab);
    free(t_hashtab);
}

/* Reads from stdin -> saves to input -> split input and store split info to buffer */
int main() {
    /* g_head head of game list in order of introduction to our database */
    G_Node **g_hashtab, *g_head, *g_tail;
    /* t_head -> head of team list in alphabetical order */
    T_Node **t_hashtab, *t_head = NULL;

    char command, input[MAX_INPUT], buff[INPUT_SPLITS][MAX_NAME];
    int cmd_cnt = 0; /* Command count */
    int scores[2];   /* Scores gathered from stdin, stored in buffer */

    g_hashtab = calloc(HASHTABLE_SIZE, sizeof(G_Node *));
    t_hashtab = calloc(HASHTABLE_SIZE, sizeof(T_Node *));
    g_head = g_tail = NULL;

    for(; ;) {
        fgets(input, MAX_INPUT, stdin);
        sscanf(input, "%1c %1023[^:\n]:%1023[^:\n]:%1023[^:\n]:%d:%d",
               &command, buff[0], buff[1], buff[2], &scores[0], &scores[1]);

        if (command == 'x')  /* Exit command */
            break;
        switch (command) {
            case 'g':
                find_most_wins(&t_head, ++cmd_cnt);
                break;
            case 'P':
                find_team(buff[0], t_hashtab, ++cmd_cnt);
                break;
            case 'A':
                add_team(buff[0], t_hashtab, &t_head, ++cmd_cnt);
                break;
            case 'r':
                remove_game(buff[0], g_hashtab, t_hashtab, &g_head, &g_tail, ++cmd_cnt);
                break;
            case 'p':
                find_game(buff[0], g_hashtab, ++cmd_cnt);
                break;
            case 'l':
                show_games(&g_tail, ++cmd_cnt);
                break;
            case 'a':
                add_game(buff[0], buff[1], buff[2], scores[0], scores[1],
                         g_hashtab, t_hashtab, &g_head, &g_tail, ++cmd_cnt);
                break;
            case 's':
                alter_score(buff[0], atoi(buff[1]), atoi(buff[2]),
                            g_hashtab, t_hashtab, ++cmd_cnt);
                break;
        }
    }
    free_memory(g_hashtab, t_hashtab, g_head, t_head);
    return 0;
}
