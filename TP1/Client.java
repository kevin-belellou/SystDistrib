import java.net.*;

public class Client
{
     public static void main(String argv[])
     {	
          try
	     {
               InetAddress adr;
               DatagramPacket packet;
               DatagramSocket socket;

               // adr contient l'@IP de la partie serveur
               adr = InetAddress.getByName("localhost");

               // données à envoyer : chaîne de caractères
               byte[] data = (new String("youpi")).getBytes();

               // création du paquet avec les données et en précisant l'adresse du serveur
               // (@IP et port sur lequel il écoute : 7777)
               packet = new DatagramPacket(data, data.length, adr, 7777);

               // création d'une socket, sans la lier à un port particulier
               socket = new DatagramSocket();

               // envoi du paquet via la socket
               socket.send(packet);
          }
          catch (Exception e)
          {
               System.err.println(e);
	     }
     }
}
