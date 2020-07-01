/*************************************************/
/*              Projeto 2 ASA 2020               */
/*                                               */
/*          Pavle Arandjelovic - 193745          */
/*          Antonio Murteira   - 190706          */
/*                                               */
/*************************************************/

#include <iostream>
#include <vector>
#include <queue>
#include <cstring>

using namespace std;

struct Edge {
    int v ;    // Vertex v (or "to" vertex)
    int flow ; // flow of data in edge
};

class Graph {
    int V;

public:
    explicit Graph(int V);
    void addRelation(int u, int v) const;
    void createIntersectionNodes(int columns) const;
    vector<Edge> *adj;
};

Graph::Graph(int V){
    this->V = V;
    this->adj = new vector<Edge>[V + 2];
}

void Graph::createIntersectionNodes(int columns) const {
     for (int i = 2; i <= V; i += 2) {
//          Input -> Output Node
         addRelation(i - 1, i);

         // Bottom-side verification
         if (i - columns > 0)
             addRelation(i, i - columns - 1);

         // Top-side verification
         if (i + columns <= V)
             addRelation(i, i + columns - 1);

         // Left-side verification
         if ((i - 2) % columns)
             addRelation(i, i - 3);

         // Right-side verification
         if (i % columns)
             addRelation(i, i + 1);
     }
}

// v -> w
void Graph::addRelation(int u, int v) const {
    // Edge -> {destination_edge, flow}
    Edge e1 {v, 1};             // Normal Edge
    Edge e2 {u, 0};             // Reverse Edge

    adj[u].push_back(e1);
    adj[v].push_back(e2);
}

bool bfs(Graph *&rGraph, int s, int t, int parent[], int vertex_cnt) {
    vector<bool> visited(vertex_cnt, false);

    queue <int> q;
    q.push(s);
    visited[s] = true;
    parent[s] = -1;

    while (!q.empty()) {
        int u = q.front();
        q.pop();

        for (Edge e : rGraph->adj[u]) {
            if (!visited[e.v] && e.flow == 1) {
                if (e.v == t) { // return once target reached
                    parent[e.v] = u;
                    return true;
                }
                q.push(e.v);
                parent[e.v] = u;
                visited[e.v] = true;
            }
        }
    }
    return false;
}

// Returns the maximum flow from s to t in the given graph
int edmondsKarp(Graph *&rGraph, int vertex_cnt) {
    int u, v;
    int target = vertex_cnt + 1;
    int source = 0;
    int parent[vertex_cnt];  // This array is filled by BFS and to store path
    int max_flow = 0;        // There is no flow initially

    while (bfs(rGraph, source, target, parent, vertex_cnt)) {
        // update values in residual graph
        for (v = target; v != source; v = parent[v]) {
            u = parent[v];

            for (auto & i : rGraph->adj[u])
                if (i.v == v)
                    i.flow = 0;             // Set flow of normal edge to 0
            for (auto & i : rGraph->adj[v])
                if (i.v == u)
                    i.flow = 1;             // Set flow of reverse edge to 1
        }
        max_flow++;     // Max possible flow through a path == 1
    }
    return max_flow;
}

int main() {
    int avenue_cnt, street_cnt; // Columns, Rows
    int shop_cnt, citizen_cnt;
    int vertex_cnt;

    scanf("%d %d", &avenue_cnt, &street_cnt);
    vertex_cnt = 2 * avenue_cnt * street_cnt;

    Graph *graph = new Graph(vertex_cnt);
    graph->createIntersectionNodes(avenue_cnt * 2);

    // Flow: shops output nodes -> super target
    scanf("%d %d", &shop_cnt, &citizen_cnt);
    for(int av_no, street_no, x = 0; x < shop_cnt; x++) {
        scanf("%d %d", &av_no, &street_no);
        graph->addRelation((street_no - 1) * (avenue_cnt * 2) + (av_no * 2), vertex_cnt + 1);
    }

    // Flow: super source -> people input nodes
    for(int av_no, street_no, x = 0; x < citizen_cnt; x++){
        scanf("%d %d", &av_no, &street_no);
        graph->addRelation(0, ((street_no - 1) * (avenue_cnt * 2)) + (av_no * 2) - 1);
    }

    cout << edmondsKarp(graph, vertex_cnt) << endl;

    delete(graph);
    return 0;
}
