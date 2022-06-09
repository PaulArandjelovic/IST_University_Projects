/******************************************************************************\
* Link state routing protocol.                                                 *
\******************************************************************************/

#include <stdlib.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "routing-simulator.h"

#define SAME_NODE(a, b) \
    ((int)(a) == (int)(b)) ? 0 : COST_INFINITY

typedef struct {
  cost_t link_cost[MAX_NODES];
  int version;
} link_state_t;

// Message format to send between nodes.
typedef struct {
  link_state_t ls[MAX_NODES];
} message_t;

// State format.
typedef struct {
    link_state_t ls[MAX_NODES];
} state_t;

void send_msg() {
    state_t *curr_state = (state_t *) get_state();
    node_t curr_node = get_current_node();

    for (node_t neighbor = get_first_node(); neighbor <= get_last_node(); neighbor++) {
        if (get_link_cost(neighbor) < COST_INFINITY && neighbor != curr_node) {
            message_t *msg = (message_t *) malloc(sizeof(message_t));

            for (node_t v = 0; v < MAX_NODES; v++) {
                for (node_t i = 0; i < MAX_NODES; i++)
                    msg->ls[v].link_cost[i] = curr_state->ls[v].link_cost[i];
                msg->ls[v].version = curr_state->ls[v].version;
            }
            send_message(neighbor, msg);
        }
    }
}

int minDistance(cost_t dist[], char sptSet[]) {
    // Initialize min value
    cost_t min = COST_INFINITY;
    node_t min_index;

    for (node_t v = get_first_node(); v <= get_last_node(); v++)
        if (sptSet[v] == 0 && dist[v] <= min)
            min = dist[v], min_index = v;

    return min_index;
}

node_t get_hop(node_t path[], node_t dest_node) {
    int prev;
    int j = dest_node;

    do {
        prev = j;
        j = path[j];
        if (j == -1)
            break;
    } while (j != get_current_node());
    return prev;
}

void dijkstra() {
    state_t *curr_state = (state_t *) get_state();

    cost_t cost[MAX_NODES];  // Cost from current node to every other node
    char sptSet[MAX_NODES];  // 1 if vertex is included in shortest path
    node_t path[MAX_NODES];  // Path to destination

    for (node_t i = get_first_node(); i <= get_last_node(); i++) {
        path[i] = -1;
        cost[i] = get_link_cost(i);
        if (cost[i]!= COST_INFINITY) {
            path[i]= get_current_node();
        }
        sptSet[i] = 0;
    }

    cost[get_current_node()] = 0;
    sptSet[get_current_node()] = 1;
    for (int count = get_first_node(); count < get_last_node(); count++) {
        cost_t u = minDistance(cost, sptSet);

        sptSet[u] = 1;

        for (node_t v = get_first_node(); v <= get_last_node(); v++)
            if (!sptSet[v] && curr_state->ls[u].link_cost[v] != COST_INFINITY && COST_ADD(cost[u], curr_state->ls[u].link_cost[v]) < cost[v]) {
                path[v] = u;
                cost[v] = COST_ADD(cost[u], curr_state->ls[u].link_cost[v]);
            }
    }

    for (node_t node = get_first_node(); node <= get_last_node(); node++) {
        if (node == get_current_node())
            continue;
        set_route(node, get_hop(path, node), cost[node]);
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
                curr_state->ls[src_node].link_cost[dest_node] = SAME_NODE(src_node, dest_node);
                curr_state->ls[src_node].version = 0;
            }
        set_state(curr_state);
    }

    if (new_cost != curr_state->ls[curr_node].link_cost[neighbor]) {
        curr_state->ls[curr_node].link_cost[neighbor] = new_cost;
        curr_state->ls[curr_node].version++;
        dijkstra();
        send_msg();
    }
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void *message) {
    state_t *curr_state = (state_t *) get_state();
    message_t *msg = (message_t *)message;
    char alter_flag = 0;
    for (node_t node = get_first_node(); node <= get_last_node(); node++) {
        if (msg->ls[node].version > curr_state->ls[node].version) {
            memcpy(curr_state->ls[node].link_cost, msg->ls[node].link_cost, sizeof(cost_t) * MAX_NODES);
            curr_state->ls[node].version = msg->ls[node].version;
            alter_flag = 1;
        }
    }

    dijkstra();
    if (alter_flag)
        send_msg();
}
