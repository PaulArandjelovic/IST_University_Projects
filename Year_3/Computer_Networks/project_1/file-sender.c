#include "packet-format.h"
#include <limits.h>
#include <netdb.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

// Program args
char *file_name;
char *host;
int port;
int window_size;
// Sequence number of last packet
uint max_seq;
// Server info
struct sockaddr_in srv_addr;
int sockfd;


void err(char *msg) {
    perror(msg);
    exit(EXIT_FAILURE);
}

void parse_args(int argc, char *argv[]) {
    if (argc != 5 || !atoi(argv[4]) || atoi(argv[4]) <= 0 || atoi(argv[4]) > MAX_WINDOW_SIZE) {
        err("Invalid arguments. Usage: ./file-sender.c file_name host port window_size\n");
    }

    file_name = argv[1];
    host = argv[2];
    port = atoi(argv[3]);
    window_size = atoi(argv[4]);
}

void send_pkt(data_pkt_t *data_pkt, int data_len) {
    printf("Sending segment %d, size %ld.\n", ntohl((*data_pkt).seq_num),
           offsetof(data_pkt_t, data) + data_len);
    size_t sent_len =
            sendto(sockfd, data_pkt, offsetof(data_pkt_t, data) + data_len, 0,
                   (struct sockaddr *) &srv_addr, sizeof(srv_addr));
    if (sent_len != offsetof(data_pkt_t, data) + data_len) {
        err("Truncated packet.\n");
    }
}

FILE* init() {
    FILE *file = fopen(file_name, "r");
    if (!file)
        err("fopen");

    // Prepare server host address.
    struct hostent *he;
    if (!(he = gethostbyname(host)))
        err("gethostbyname");

    srv_addr.sin_family = AF_INET;
    srv_addr.sin_port = htons(port);
    srv_addr.sin_addr = *((struct in_addr *)he->h_addr);

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd == -1)
        err("socket");

    struct timeval tv;
    int secs = TIMEOUT / 1000;
    tv.tv_sec = secs;    // 1 sec packet acknowledgement receivement timeout
    tv.tv_usec = 0;

    if (setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) == -1) {
        err("Setting socket time out");
    }

    return file;
}

void read_file(data_pkt_t *data_pkt, FILE** file, size_t* data_len) {
    fseek(*file, ntohl((*data_pkt).seq_num) * sizeof((*data_pkt).data), SEEK_SET);
    *data_len = fread((*data_pkt).data, 1, sizeof((*data_pkt).data), *file);

    if (*data_len >= 0 && *data_len < sizeof((*data_pkt).data)) {
        max_seq = ntohl((*data_pkt).seq_num);
    }
};

int recv_data(ack_pkt_t* ack_pkt, int* retries) {
    if (recvfrom(sockfd, ack_pkt, sizeof(ack_pkt), 0, (struct sockaddr *) &srv_addr, &(socklen_t){sizeof(srv_addr)}) < 0) {
        if (*retries >= MAX_RETRIES - 1) {
            err("Max packet retries reached!");
        } else {
            (*retries)++;
            return 1;  // Invalid response from receiver
        }
    }
    return 0;  // Valid response from receiver
}

int main(int argc, char *argv[]) {
    parse_args(argc, argv);
    FILE *file = init();

    uint32_t seq_num = 0;   // base of window
    data_pkt_t data_pkt;    // data packet sent to receiver
    size_t data_len;        // length of data read from file

    max_seq = UINT_MAX - 1; // sequence number of last packet in file
    int retries = 0;        // attempted retries for lost packets

    ack_pkt_t ack_pkt;
    ack_pkt.selective_acks = htonl(0);

    do {
        for (int i=0; (i < window_size) && (seq_num+i <= max_seq); i++) {

            if (i > 0) { // If i == 0 -> base of receiver window
                if ((ntohl(ack_pkt.selective_acks) & (1 << (i-1))) != 0) {
                    continue;  // Already ACK'd
                }
            }
            data_pkt.seq_num = htonl(seq_num + i);
            read_file(&data_pkt, &file, &data_len);
            send_pkt(&data_pkt, data_len);

            if (data_len != sizeof(data_pkt.data)) {
                break;
            }
        }

        // Receive ACK from receiver
        while (!recv_data(&ack_pkt, &retries)) {
            printf("Received ACK %d\n", ntohl(ack_pkt.seq_num));
            retries = 0;

            int seq_diff = (ntohl(ack_pkt.seq_num) - seq_num);
            if (!seq_diff) {
                continue;
            }

            // Clean up and exit.
            if (ntohl(ack_pkt.seq_num) == max_seq + 1) {
                close(sockfd);
                fclose(file);
                return 0;
            }

            // Send all new chunks reached by new window location
            while (seq_diff && (window_size + seq_num <= max_seq)) {
                seq_diff--;
                data_pkt.seq_num = htonl(window_size + seq_num++);
                read_file(&data_pkt, &file, &data_len);
                send_pkt(&data_pkt, data_len);
            }
            seq_num = ntohl(ack_pkt.seq_num);
        }
    } while (1);
}