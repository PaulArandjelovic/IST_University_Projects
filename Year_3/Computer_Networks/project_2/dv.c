/******************************************************************************\
* Distance vector routing protocol without reverse path poisoning.             *
\******************************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "routing-simulator.h"

#define SAME_NODE(a, b) \
    ((int)(a) == (int)(b)) ? 0 : COST_INFINITY

// Message format to send between nodes.
typedef struct {
    cost_t arr[MAX_NODES];
} message_t;

// State format.
typedef struct {
    cost_t table[MAX_NODES][MAX_NODES];
} state_t;


void print_table(node_t self, state_t *state) {
    printf("<----- Node %d ------>\n", self);
    for (node_t i = get_first_node(); i <= get_last_node(); i++) {
        for (node_t j = get_first_node(); j <= get_last_node(); j++) {
            printf(" %3d ", state->table[i][j]);
        }
        printf("\n");
    }
}

void bellman_ford(node_t curr_node, state_t* curr_state) {
    int alter_flag = 0;

    for (node_t dest_node = get_first_node(); dest_node <= get_last_node(); dest_node++) {
        if (curr_node == dest_node)
            continue;
        node_t hop_select = -1;
        cost_t min_cost = COST_INFINITY;

        for (node_t hop_node = get_first_node(); hop_node <= get_last_node(); hop_node++) {
            if (curr_node == hop_node)
                continue;

            cost_t new_cost = COST_ADD(get_link_cost(hop_node), curr_state->table[hop_node][dest_node]);

            if (new_cost < min_cost) {
                hop_select = hop_node;
                min_cost = new_cost;
            }
        }

        if (curr_state->table[curr_node][dest_node] != min_cost) {
            curr_state->table[curr_node][dest_node] = min_cost;
            set_route(dest_node, hop_select, min_cost);
            alter_flag = 1;
        }
        if (curr_state->table[curr_node][dest_node] == min_cost)
            set_route(dest_node, hop_select, min_cost);
    }

    if (alter_flag) {
        for (node_t neighbor = get_first_node(); neighbor <= get_last_node(); neighbor++) {
            if (get_link_cost(neighbor) < COST_INFINITY && neighbor != curr_node) {
                message_t *msg = (message_t *)malloc(sizeof(message_t));
                memcpy(msg->arr, curr_state->table[curr_node], sizeof(curr_state->table[curr_node]));
                send_message(neighbor, msg);
            }
        }
    }
}

// Notify a node that a neighboring link has changed cost.
void notify_link_change(node_t neighbor, cost_t new_cost) {
    state_t *curr_state = (state_t *) get_state();
    node_t curr_node = get_current_node();

    if (!curr_state) {
        curr_state = (state_t *) malloc(sizeof(state_t));

        for (node_t src_node = get_first_node(); src_node <= get_last_node(); src_node++)
            for (node_t dest_node = get_first_node(); dest_node <= get_last_node(); dest_node++)
                curr_state->table[src_node][dest_node] = SAME_NODE(src_node, dest_node);
        set_state(curr_state);
    }
    bellman_ford(curr_node, curr_state);
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void *message) {
    state_t *curr_state = (state_t *) get_state();

    memcpy(curr_state->table[sender], ((message_t *)message)->arr, sizeof(cost_t) * MAX_NODES);
    bellman_ford(get_current_node(), curr_state);
}
