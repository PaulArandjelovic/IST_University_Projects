/*
 * File:        main.c
 * Author:      Pavle Arandjelovic
 * IST Number:  TP193745
 * Description: Logistical System created in C.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "constants.h"

int product_count = 0;           /* Product counter */
int order_count = 0;             /* Order counter */
Product p_info[MAX_PRODUCTS];    /* List of Products (product info) */
Order orders[MAX_ORDERS];        /* List of orders */
Product *products[MAX_PRODUCTS]; /* List of Product pointers to p_info, ordered by price */

/* Shift an array to the right from a start position to a stop position */
void shift_right_array(void** arr, int start_pos, int stop_pos) {
    int i;

    for (i=start_pos; i >= stop_pos; arr[i] = arr[i-1], i--);
}

/* Return the position of a product in the order if it exists, -1 if it doesn't */
int find_product_pos(int o_id, int p_id) {
    int i;

    for (i=0; i < orders[o_id].ref_cnt; i++) /* iterate through product refs of an order */
        if (orders[o_id].p_refs[i].id == p_id)
            return i;
    return -1;  /* Product ID not found in order with ID o_id */
}

/* Insert a new product into our system in order of price and ID */
void insert_product(Product *product) {
    int i, insert_pos = product_count;

    for(i=0; i < product_count; i++) /* iterate through all system products */
        if (*products[i]->price > *product->price ||
           (*product->price == *products[i]->price && product->id < products[i]->id)) {
            insert_pos = i;
            break;
        }
    shift_right_array((void*)products, product_count++, insert_pos);
    products[insert_pos] = product;
}

/* Insert a products reference into order reference array in alphabetical order */
void insert_into_order(Product_ref prod_arr[], int ref_cnt, Product_ref p_ref) {
    int pos, id = p_ref.id;

    /* Find position to insert product ref in order */
    for (pos=0; pos < ref_cnt && strcmp(p_info[prod_arr[pos].id].desc, p_info[id].desc) < 0; pos++);
    shift_right_array((void*)prod_arr, ref_cnt, pos);
    prod_arr[pos] = p_ref;
}

/* Add stock to a product, alter value in global p_info array */
void add_stock(int p_id, int quantity) {
    if (p_id >= product_count)
        printf("Impossivel adicionar produto %d ao stock. Produto inexistente.\n", p_id);
    else
        p_info[p_id].stock += quantity;
}

/* Remove stock from a product, alter value in global p_info array */
void remove_stock(int p_id, int quantity) {
    if (p_id >= product_count)
        printf("Impossivel remover stock do produto %d. Produto inexistente.\n", p_id);
    else if (p_info[p_id].stock < quantity)
        printf("Impossivel remover %d unidades do produto %d do stock. Quantidade insuficiente.\n", quantity, p_id);
    else
        p_info[p_id].stock -= quantity;
}

/* Add product to system - Add product to p_info and pointer to it in products */
void add_product(char* desc, int price, int weight, int stock) {
    Product product;

    product.desc = malloc(strlen(desc) * sizeof(char) + 1);
    product.price = malloc(sizeof(int));

    strcpy(product.desc, desc);
    product.id = product_count;
    product.weight = weight;
    *product.price = price;
    product.stock = stock;

    p_info[product.id] = product;
    insert_product(&p_info[product.id]);
    printf("Novo produto %d.\n", product.id);
}

/* Remove product from order - set stock of product in order to 0 */
void remove_product(int o_id, int p_id) {
    if (o_id >= order_count)
        printf("Impossivel remover produto %d a encomenda %d. Encomenda inexistente.\n", p_id, o_id);
    else if (p_id >= product_count)
        printf("Impossivel remover produto %d a encomenda %d. Produto inexistente.\n", p_id, o_id);
    else {
        int pos;

        if ((pos = find_product_pos(o_id, p_id)) > -1) {
            int id = orders[o_id].p_refs[pos].id;
            add_stock(orders[o_id].p_refs[pos].id, orders[o_id].p_refs[pos].stock);
            orders[o_id].weight -= p_info[id].weight * orders[o_id].p_refs[pos].stock;
            orders[o_id].p_refs[pos].stock = 0;
        }
    }
}

/* Alter price of product in system - change price of product in p_info */
void alter_price(int p_id, int new_price) {
    if (p_id >= product_count)
        printf("Impossivel alterar preco do produto %d. Produto inexistente.\n", p_id);
    else {
        Product *prod;
        int i;

        *p_info[p_id].price = new_price; /* Alter product price globally */
        /* Remove product from products array */
        for (i=0; products[i]->id != p_id; i++);
        prod = products[i];
        for (; i < product_count; products[i] = products[i+1], i++);

        product_count--;
        /* Re-insert product into products array */
        insert_product(prod);
    }
}

/* Add a stock of product to an order and remove available stock from system */
void add_to_order(int o_id, int p_id, int stock) {
    if (o_id >= order_count)
        printf("Impossivel adicionar produto %d a encomenda %d. Encomenda inexistente.\n", p_id, o_id);
    else if (p_id >= product_count)
        printf("Impossivel adicionar produto %d a encomenda %d. Produto inexistente.\n", p_id, o_id);
    else if (p_info[p_id].stock < stock)
        printf("Impossivel adicionar produto %d a encomenda %d. Quantidade em stock insuficiente.\n", p_id, o_id);
    else if (p_info[p_id].weight * stock + orders[o_id].weight > MAX_WEIGHT)
        printf("Impossivel adicionar produto %d a encomenda %d. Peso da encomenda excede o maximo de %d.\n", p_id, o_id, MAX_WEIGHT);
    else {
        Product_ref p_ref;
        int pos;

        p_info[p_id].stock -= stock;
        orders[o_id].weight += p_info[p_id].weight * stock;

        /* If product already exists in order, add stock to it */
        if ((pos = find_product_pos(o_id, p_id)) > -1) {
            orders[o_id].p_refs[pos].stock += stock;
            return;
        }
        /* Product does not already exist in order, insert new entry */
        p_ref.id = p_id;
        p_ref.stock = stock;
        insert_into_order(orders[o_id].p_refs, orders[o_id].ref_cnt, p_ref);
        orders[o_id].ref_cnt++;
    }
}

/* Create a new empty order and add to global orders array */
void new_order() {
    Order order;
    order.weight = 0;
    order.ref_cnt = 0;
    orders[order_count] = order;
    printf("Nova encomenda %d.\n", order_count++);
}

/* Find the order where a product occurs most, print result to stdout */
void find_most_product(int p_id) {
    if (p_id >= product_count)
        printf("Impossivel listar maximo do produto %d. Produto inexistente.\n", p_id);
    else {
        int x, pos;
        int order = -1, stock = 0;

        for (x=0; x < order_count; x++)
            if ((pos = find_product_pos(x, p_id)) > -1 && orders[x].p_refs[pos].stock > stock) {
                order = x;
                stock = orders[x].p_refs[pos].stock;
            }
        if (stock)
            printf("Maximo produto %d %d %d.\n", p_id, order, stock);
    }
}

/* Calculate total cost of an order, print to stdout */
void calc_order_cost(int o_id) {
    if (o_id >= order_count)
        printf("Impossivel calcular custo da encomenda %d. Encomenda inexistente.\n", o_id);
    else {
        int i, total = 0;

        for (i=0; i < orders[o_id].ref_cnt; i++)
            total += orders[o_id].p_refs[i].stock * *p_info[orders[o_id].p_refs[i].id].price;
        printf("Custo da encomenda %d %d.\n", o_id, total);
    }
}

/* Show product info in a given order (if exists), print to stdout */
void show_product(int o_id, int p_id) {
    if (o_id >= order_count)
        printf("Impossivel listar encomenda %d. Encomenda inexistente.\n", o_id);
    else if (p_id >= product_count)
        printf("Impossivel listar produto %d. Produto inexistente.\n", p_id);
    else {
        int pos;

        if ((pos = find_product_pos(o_id, p_id)) > -1) {
            int id = orders[o_id].p_refs[pos].id;
            printf("%s %d.\n", p_info[id].desc, orders[o_id].p_refs[pos].stock);
            return;
        }
        printf("%s 0.\n", p_info[p_id].desc); /* Product stock in order = 0 */
    }
}

/* Show all products of an order, print to stdout */
void show_order(int o_id) {
    if (o_id >= order_count)
        printf("Impossivel listar encomenda %d. Encomenda inexistente.\n", o_id);
    else {
        int i;

        printf("Encomenda %d\n", o_id);
        for (i=0; i < orders[o_id].ref_cnt; i++)
            if (orders[o_id].p_refs[i].stock) {
                int id = orders[o_id].p_refs[i].id;
                printf("* %s %d %d\n", p_info[id].desc, *p_info[id].price, orders[o_id].p_refs[i].stock);
            }
    }
}

/* Show all existing products in system, print to stdout */
void show_products() {
    int i;

    printf("Produtos\n");
    for (i=0; i < product_count; i++)
        printf("* %s %d %d\n", products[i]->desc, *products[i]->price, products[i]->stock);
}

/* Free previously allocated memory in global var info before program termination */
void free_memory() {
    int i;

    for (i=0; i < product_count; i++) {
        free(p_info[i].desc);
        free(p_info[i].price);
    }
}

/* Reads from stdin -> saves to input -> split input and store split info to buffer */
int main() {
    char buff[MAX_DESC], input[MAX_INPUT], command;
    int nums_in[3]; /* Numbers gathered from stdin, stored in buffer */
    /* Array of function pointers instead of large switch statement reduces
     * code clutter and scales easier than large switch statement */
    void (*funcs[256])();
    funcs['N'] = new_order;   funcs['p'] = alter_price;  funcs['l'] = show_products;
    funcs['q'] = add_stock;   funcs['r'] = remove_stock; funcs['R'] = remove_product;
    funcs['L'] = show_order;  funcs['A'] = add_to_order; funcs['C'] = calc_order_cost;
    funcs['a'] = add_product; funcs['E'] = show_product; funcs['m'] = find_most_product;

    for (; ;) {
        fgets(input, MAX_INPUT, stdin);
        sscanf(input, "%1c %[^:]:%d:%d:%d",
                &command, buff, &nums_in[0], &nums_in[1], &nums_in[2]);
        if (command == 'x')               /* Exit command */
            break;
        if (strchr("N l", command))       /* Functions with 0 arguments */
            funcs[(int)command]();
        if (strchr("L C m", command))     /* Functions with 1 argument */
            funcs[(int)command](atoi(buff));
        if (strchr("q E R r p", command)) /* Functions with 2 arguments */
            funcs[(int)command](atoi(buff), nums_in[0]);
        if (strchr("A", command))         /* Functions with 3 arguments */
            funcs[(int)command](atoi(buff), nums_in[0], nums_in[1]);
        if (strchr("a", command))         /* Functions with 4 arguments */
            funcs[(int)command](buff, nums_in[0], nums_in[1], nums_in[2]);
    }
    free_memory();
    return 0;
}
