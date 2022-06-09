#include "packet-format.h"
#include <arpa/inet.h>
#include <limits.h>
#include <netinet/in.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>


char *file_name;
int port;
int window_size;

int sockfd;
struct sockaddr_in srv_addr;
struct sockaddr_in src_addr;

void err(char *msg) {
    perror(msg);
    exit(EXIT_FAILURE);
}

void parse_args(int argc, char* argv[]) {
    if (argc != 4 || !atoi(argv[3]) || atoi(argv[3]) <= 0 || atoi(argv[3]) > MAX_WINDOW_SIZE) {
        err("Invalid arguments. Usage: ./file-sender.c file_name host port window_size\n");
    }

    file_name = argv[1];
    port = atoi(argv[2]);
    window_size = atoi(argv[3]);
}

FILE* init() {
    FILE *file = fopen(file_name, "w");
    if (!file)
        err("fopen");

    // Prepare server socket.
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd == -1)
        err("socket");

    // Allow address reuse so we can rebind to the same port,
    // after restarting the server.
    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) < 0)
        err("setsockopt");

    srv_addr.sin_family = AF_INET;
    srv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    srv_addr.sin_port = htons(port);

    if (bind(sockfd, (struct sockaddr *)&srv_addr, sizeof(srv_addr)))
        err("bind");
    fprintf(stderr, "Receiving on port: %d\n", port);

    return file;
}

int in_window(int seq_num, int data_seq_num) {
    return (data_seq_num < (seq_num + window_size) && data_seq_num >= seq_num) ? data_seq_num - seq_num : -1;
}

void send_ack(ack_pkt_t *ack_pkt, uint32_t *seq_num, int data_seq_num) {
    int seq_diff = in_window(*seq_num, data_seq_num);

    if (seq_diff == 0) {
        (*seq_num)++;
        while (ntohl((*ack_pkt).selective_acks) & 1) {
            (*seq_num)++;
            (*ack_pkt).selective_acks = htonl(ntohl((*ack_pkt).selective_acks) >> 1);
        }
        (*ack_pkt).selective_acks = htonl(ntohl((*ack_pkt).selective_acks) >> 1);
    } else if (seq_diff > 0) {
        (*ack_pkt).selective_acks = htonl(ntohl((*ack_pkt).selective_acks) | 1<<(seq_diff-1));
    }
    (*ack_pkt).seq_num = htonl(*seq_num);

    printf("Sending ACK %u\n", *seq_num);
    sendto(sockfd, ack_pkt, sizeof(ack_pkt), 0,
           (struct sockaddr *)&src_addr, sizeof(src_addr));
}

int main(int argc, char *argv[]) {
    parse_args(argc, argv);
    FILE* file = init();

    ssize_t len;
    uint32_t seq_num = 0;

    uint last_pkt = UINT_MAX;

    ack_pkt_t ack_pkt;
    ack_pkt.selective_acks = htonl(0);

    do {
        data_pkt_t data_pkt;

        len =
                recvfrom(sockfd, &data_pkt, sizeof(data_pkt), 0,
                         (struct sockaddr *)&src_addr, &(socklen_t){sizeof(src_addr)});
        printf("Received segment %d, size %ld.\n", ntohl(data_pkt.seq_num), len);

        if (len != sizeof(data_pkt_t))
            last_pkt = ntohl(data_pkt.seq_num);

        fseek(file, ntohl(data_pkt.seq_num) * sizeof(data_pkt.data), SEEK_SET);
        fwrite(data_pkt.data, 1, len - offsetof(data_pkt_t, data), file);
        send_ack(&ack_pkt, &seq_num, ntohl(data_pkt.seq_num));

    } while (last_pkt >= seq_num);

    // Clean up and exit.
    close(sockfd);
    fclose(file);

    return 0;
}