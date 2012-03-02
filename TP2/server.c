#include "../functions.h"

#define TAILLEBUF 20

int main(int argc, char* argv[])
{
     if(argc != 2)
     {
		printf("Wrong number of argument\n");
		return -1;
	}

     // adresse socket coté client
     static struct sockaddr_in addr_client;

     // adresse socket locale
     static struct sockaddr_in addr_serveur;

     // longueur adresse
     int lg_addr;

     // socket d'écoute et de service
     int socket_ecoute, socket_service;

     // Création de la socket et bind via functions.h
     socket_ecoute = creerSocketTCP(atoi(argv[1]));
     if (socket_ecoute == -1)
     {
          printf("fuck\n");
          exit(-1);
     }

     // buffer qui contiendra le message reçu
     char message[TAILLEBUF];

     // chaîne reçue du client
     char *chaine_recue;

     // chaîne renvoyée au client
     char *reponse = "bien recu";

     // nombre d'octets reçus ou envoyés
     int nb_octets;

//     // création socket TCP d'écoute
//     socket_ecoute = socket(AF_INET, SOCK_STREAM, 0);
//     if (socket_ecoute == -1)
//     {
//          perror("creation socket");
//          exit(1);
//     }

//     // liaison de la socket d'écoute sur le port 4000
//     bzero((char *) &addr_serveur, sizeof(addr_serveur));
//     addr_serveur.sin_family = AF_INET;
//     addr_serveur.sin_port = htons(4000);
//     addr_serveur.sin_addr.s_addr=htonl(INADDR_ANY);
//     if (bind(socket_ecoute, (struct sockaddr*)&addr_serveur, sizeof(addr_serveur)) == -1 )
//     {
//          perror("erreur bind socket écoute");
//          exit(1);
//     }

     // configuration socket écoute : 5 connexions max en attente
     if (listen(socket_ecoute, 5) == -1)
     {
          perror("erreur listen");
          exit(1);
     }

     // on attend la connexion du client
     lg_addr = sizeof(struct sockaddr_in);
     socket_service = accept(socket_ecoute, (struct sockaddr *)&addr_client, &lg_addr);
     if (socket_service == -1)
     {
        perror("erreur accept");
        exit(1);
     }

     // la connexion est établie, on attend les données envoyées par le client
     nb_octets = read(socket_service, message, TAILLEBUF);

     // affichage du message reçu
     chaine_recue = (char *)malloc(nb_octets * sizeof(char));
     memcpy(chaine_recue, message, nb_octets);
     printf("recu message %s\n", chaine_recue);

     // on envoie la réponse au client
     write(socket_service, reponse, strlen(reponse) + 1);

     // on ferme les sockets
     close(socket_service);
     close(socket_ecoute);
}
