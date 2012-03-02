#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <errno.h>

int creerSocketUDP(int port)
{
     int sock;
     static struct sockaddr_in addr;

     // création d'une socket UDP
     sock = socket(AF_INET, SOCK_DGRAM, 0);
     if (sock == -1)
     {
          //perror("erreur création socket");
          return -1;
     }

     // liaison de la socket sur le port local passé en paramètre
     bzero(&addr, sizeof(struct sockaddr_in));
     addr.sin_family = AF_INET;
     addr.sin_port = htons(port);
     addr.sin_addr.s_addr = htonl(INADDR_ANY);

     if (bind(sock, (struct sockaddr*)&addr, sizeof(addr)) == -1)
     {
          //perror("erreur bind");
          return -1;
     }

     return sock;
}

int creerSocketTCP(int port)
{
     int sock;
     static struct sockaddr_in addr;

     // création d'une socket UDP
     sock = socket(AF_INET, SOCK_STREAM, 0);
     if (sock == -1)
     {
          //perror("erreur création socket");
          return -1;
     }

     // liaison de la socket sur le port local passé en paramètre
     bzero(&addr, sizeof(struct sockaddr_in));
     addr.sin_family = AF_INET;
     addr.sin_port = htons(port);
     addr.sin_addr.s_addr = htonl(INADDR_ANY);

     if (bind(sock, (struct sockaddr*)&addr, sizeof(addr)) == -1)
     {
          //perror("erreur bind");
          return -1;
     }

     return sock;
}
