/******************************************************************************\
* Path vector routing protocol.                                                *
\******************************************************************************/

#include <stdlib.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "routing-simulator.h"

#define SAME_NODE(a, b) \
    ((int)(a) == (int)(b)) ? 0 : COST_INFINITY

// Message format to send between nodes.
typedef struct {
    cost_t arr[MAX_NODES];
    node_t path[MAX_NODES][MAX_NODES];
} message_t;

// State format.
typedef struct {
    cost_t table[MAX_NODES][MAX_NODES];
    node_t path[MAX_NODES][MAX_NODES][MAX_NODES];
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

void set_path(node_t dest_node, node_t hop_select) {
    state_t *curr_state = (state_t *) get_state();
    node_t curr_node = get_current_node();

    if (hop_select == -1) {
        for (node_t i = get_first_node(); i <= get_first_node(); i++) {
            curr_state->path[curr_node][dest_node][i] = -1;
        }
    } else {
        curr_state->path[curr_node][dest_node][0] = hop_select;

        for (node_t i = get_first_node(); i != dest_node && i <= get_last_node(); i++) {
            node_t next_node = curr_state->path[hop_select][dest_node][i];
            curr_state->path[curr_node][dest_node][i + 1] = next_node;
        }
    }
}

char no_loop(node_t dest_node, node_t hop_select) {
    state_t *curr_state = (state_t *) get_state();
    node_t curr_node = get_current_node();

    for (node_t i = get_first_node(); i <= get_last_node(); i++) {
        int cnt = 0;
        for (node_t j = get_first_node(); j <= get_last_node(); j++) {
            node_t node = curr_state->path[hop_select][dest_node][j];
            if ((node == i && ++cnt > 1) || (node == hop_select) || (node == curr_node)) {
                return 0;
            }
        }
    }
    return 1;
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

            if (new_cost < min_cost && no_loop(dest_node, hop_node)) {
                hop_select = hop_node;
                min_cost = new_cost;
            }
        }

        if (curr_state->table[curr_node][dest_node] != min_cost) {
            curr_state->table[curr_node][dest_node] = min_cost;

            set_path(dest_node, hop_select);
            set_route(dest_node, hop_select, min_cost);
            alter_flag = 1;
        }
        if (curr_state->table[curr_node][dest_node] == min_cost) {
            set_path(dest_node, hop_select);
            set_route(dest_node, hop_select, min_cost);
        }
    }

    if (alter_flag) {
        for (node_t neighbor = get_first_node(); neighbor <= get_last_node(); neighbor++) {
            if (get_link_cost(neighbor) < COST_INFINITY && neighbor != curr_node) {
                message_t *msg = (message_t *)malloc(sizeof(message_t));
                memcpy(msg->arr, curr_state->table[curr_node], sizeof(curr_state->table[curr_node]));
                memcpy(msg->path, curr_state->path[curr_node], sizeof(curr_state->path[curr_node]));
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
            for (node_t dest_node = get_first_node(); dest_node <= get_last_node(); dest_node++) {
                curr_state->table[src_node][dest_node] = SAME_NODE(src_node, dest_node);
                for (node_t i = get_first_node(); i <= get_last_node(); i++)
                    curr_state->path[src_node][dest_node][i] = -1;
            }
        set_state(curr_state);
    }
    bellman_ford(curr_node, curr_state);
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void *message) {
    state_t *curr_state = (state_t *) get_state();
    message_t *msg = (message_t *)message;

    memcpy(curr_state->table[sender], msg->arr, sizeof(cost_t) * MAX_NODES);
    memcpy(curr_state->path[sender], msg->path, sizeof(node_t) * MAX_NODES * MAX_NODES);
    bellman_ford(get_current_node(), curr_state);
}
