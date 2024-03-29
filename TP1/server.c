#include "../functions.h"

#define TAILLEBUF 20

int main(int argc, char* argv[])
{
     if(argc != 2)
     {
          printf("Wrong number of argument\n");
          return -1;
     }

     // adresse de la socket locale
     static struct sockaddr_in addr_local;

     // adresse de la socket coté serveur
     static struct sockaddr_in addr_client;

     // identifiant du client
     struct hostent *host_client;

     // taille de l'addresse socket
     socklen_t lg;

     // descripteur de la socket locale
     int sock;

     // Création de la socket et bind via functions.h
     sock = creerSocketUDP(atoi(argv[1]));
     if (sock == -1)
     {
          printf("fuck\n");
          exit(-1);
     }

     // chaine à envoyer en réponse
     char *reponse = "bien recu";

     // buffer de réception
     char buffer[TAILLEBUF];

     // chaine reçue
     char *chaine;

     // nombre d'octets lus ou envoyés
     int nb_octets;

     // attente de données venant d'un client
     lg = sizeof(struct sockaddr_in);
     nb_octets = recvfrom(sock, buffer, TAILLEBUF, 0, (struct sockaddr *)&addr_client, &lg);
     if (nb_octets == -1)
     {
          perror("erreur réception paquet");
          exit(1);
     }

     // récupère nom de la machine émettrice des données
     host_client = gethostbyaddr(&(addr_client.sin_addr), sizeof(long), AF_INET);
     if (host_client == NULL)
     {
          perror("erreur gethostbyaddr");
          exit(1);
     }

     // affichage message reçu et coordonnées émetteur
     chaine = (char *)malloc(nb_octets * sizeof(char));
     memcpy(chaine, buffer, nb_octets);
     printf("recu message %s de la part de %s depuis le port %d\n", chaine, host_client->h_name, ntohs(addr_client.sin_port));

     // envoi de la réponse à l'émetteur
     nb_octets = sendto(sock, reponse, strlen(reponse)+1, 0, (struct sockaddr*)&addr_client, lg);
     if (nb_octets == -1)
     {
          perror("erreur envoi réponse");
          exit(1);
     }

     // fermeture la socket
     close(sock);

     return 0;
}
