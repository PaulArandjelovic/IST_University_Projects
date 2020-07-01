/*
 * File:        constants.h
 * Author:      Pavle Arandjelovic
 * IST Number:  TP193745
 * Description: Logistical System created in C.
 */
#ifndef CONSTANTS_H
#define CONSTANTS_H

/* Max amount of products allowed in our system */
#define MAX_PRODUCTS 10000
/* Max amount of orders allowed in our system */
#define MAX_ORDERS 500
/* Max amount of characters for any given string (including '\0' character) */
#define MAX_DESC 64
/* Max weight for a certain order */
#define MAX_WEIGHT 200
/* Max input that can be read from stdin by our system */
#define MAX_INPUT 80

/* Stores all product info */
typedef struct Product {
    char *desc;
    int id;
    int weight;
    int *price;
    int stock;
} Product;

/* Stores product references to quickly and easily extract info about a product */
typedef struct Product_ref {
    int id;
    int stock;                        /* Stock of certain product in an order */
} Product_ref;

/* Stores information about a certain order */
typedef struct Order {
    int ref_cnt;                      /* Reference counter */
    int weight;
    Product_ref p_refs[MAX_PRODUCTS]; /* Product references to a product in info array */
} Order;

#endif /* CONSTANTS_H */
