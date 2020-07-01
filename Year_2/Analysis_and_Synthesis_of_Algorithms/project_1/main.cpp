/*************************************************/
/*              Projeto 1 ASA 2020               */
/*                                               */
/*          Pavle Arandjelovic - 193745          */
/*          Antonio Murteira   - 190706          */
/*                                               */
/*************************************************/

#include <iostream>
#include <cstdio>
#include <list>
#include <stack>

#define NIL -1

using namespace std;

class Graph{
    int V;                              
    list<int> *adj;
    public: int* grades;
    void TarjanVisit(int u, int disc[], int low[], stack<int> *st,stack<int> *SCC_st, bool stackMember[]);

public:
    Graph(int V);
    void addRelation(int v, int w);
    void addStudentGrade(int v, int grade);
    void Tarjan();
};

Graph::Graph(int V){
    this->V = V;
    grades = new int[V];
    adj = new list<int>[V];
}
void Graph::addStudentGrade(int v, int grade){
    grades[v-1] = grade;
}
void Graph::addRelation(int v, int w){
    adj[v-1].push_back(w-1);
}
void Graph::TarjanVisit(int u, int disc[], int low[], stack<int> *st, stack<int> *SCC_st, bool stackMember[]){
    static int time = -1;
    disc[u] = low[u] = ++time;
    st->push(u);
    stackMember[u] = true;

    list<int>::iterator i;
    // Visit all adjacent students
    for (i = adj[u].begin(); i != adj[u].end(); ++i){
        int v = *i;

        // If not visited, visit with Util
        if (disc[v] == NIL){
            TarjanVisit(v, disc, low, st, SCC_st, stackMember);
            grades[u] = max(grades[u], grades[v]);
            low[u]  = min(low[u], low[v]);
        }

        else if (stackMember[v])
            low[u]  = min(low[u], disc[v]);
        grades[u] = max(grades[u], grades[v]);
    }

    int w = 0;

    if (low[u] == disc[u]){
        int max_grade = -1;
        while (st->top() != u){
            w = (int) st->top();
            if(grades[w]>max_grade)
                max_grade = grades[w];
            SCC_st->push(w);
            stackMember[w] = false;
            st->pop();
        }

        w = (int) st->top();
        if(grades[w]>max_grade)
            max_grade = grades[w];
        SCC_st->push(w);
        while(!SCC_st->empty()){
            w = SCC_st->top();
            grades[w] = max_grade;
            SCC_st->pop();
        }

        stackMember[w] = false;
        st->pop();
    }
}
void Graph::Tarjan(){
    int *disc = new int[V];
    int *low = new int[V];
    bool *stackMember = new bool[V];
    stack<int> *st = new stack<int>();
    stack<int> *SCC_st = new stack<int>();

    for (int i = 0; i < V; i++){
        disc[i] = NIL;
        low[i] = NIL;
        stackMember[i] = false;
    }

    for (int i = 0; i < V; i++)
        if (disc[i] == NIL)
            TarjanVisit(i, disc, low, st, SCC_st, stackMember);
}


int main() {
    int student_count;
    int relation_count;
    int origin_node;
    int end_node;
    int grade;

    scanf("%d,%d", &student_count, &relation_count);
    Graph graph(student_count);

    for (int x = 1; x < student_count + 1; x++) {
        scanf("%d", &grade);
        graph.addStudentGrade(x, grade);
    }

    for(int x = 0; x < relation_count; x++){
        scanf("%d %d", &origin_node, &end_node);
        graph.addRelation(origin_node, end_node);
    }

    graph.Tarjan();

    for (int i = 0; i < student_count; i++)
        cout << graph.grades[i] << "\n";

}