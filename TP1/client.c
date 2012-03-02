#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <errno.h>

#include "../functions.h"

#define TAILLEBUF 20

int main(int argc, char** argv)
{
	if(argc != 3)
     {
		printf("Wrong number of argument\n");
		return -1;
	}

     // identifiant de la machine serveur
     struct hostent *serveur_host;

     // adresse de la socket coté serveur
     static struct sockaddr_in addr_serveur;

     // taille de l'addresse socket
     socklen_t lg;

     // descripteur de la socket locale
     int sock;

     // Création de la socket et bind via functions.h
     sock = creerSocketUDP(atoi(argv[2]));
     if (sock == -1)
     {
          printf("fuck\n");
          exit(-1);
     }

     // chaine à envoyer
     char *msg = "bonjour";

     // buffer de réception
     char buffer[TAILLEBUF];

     // chaine reçue en réponse
     char *reponse;

     // nombre d'octets lus ou envoyés
     int nb_octets;

//     // création d'une socket UDP
//     sock = socket(AF_INET, SOCK_DGRAM, 0);
//     if (sock == -1)
//     {
//          perror("erreur création socket");
//          exit(1);
//     }

     // récupération identifiant du serveur
     serveur_host = gethostbyname(argv[1]);
     if (serveur_host == NULL)
     {
          perror("erreur adresse serveur");
          exit(1);
     }

//     // création adresse socket destinatrice
//     bzero(&addr_serveur, sizeof(struct sockaddr_in));
//     addr_serveur.sin_family = AF_INET;
//     addr_serveur.sin_port = htons(atoi(argv[2]));
//     memcpy(&addr_serveur.sin_addr.s_addr, serveur_host->h_addr, serveur_host->h_length);

     // on envoie le message "bonjour" au serveur
     lg = sizeof(struct sockaddr_in);
     nb_octets = sendto(sock, msg, strlen(msg)+1, 0, (struct sockaddr*)&addr_serveur, lg);
     if (nb_octets == -1)
     {
          perror("erreur envoi message");
          exit(1);
     }

     printf("paquet envoyé, nb_octets = %d\n",nb_octets);

     // on attend la réponse du serveur
     nb_octets = recvfrom(sock, buffer, TAILLEBUF, 0, (struct sockaddr*)&addr_serveur, &lg);
     if (nb_octets == -1)
     {
          perror("erreur réponse serveur");
          exit(1);
     }

     reponse = (char *)malloc(nb_octets * sizeof(char));
     memcpy(reponse, buffer, nb_octets);
     printf("reponse recue du serveur : %s\n",reponse);

     // on ferme la socket
     close(sock);

	return 0;
}
