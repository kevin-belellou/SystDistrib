import java.net.*;

public class Client
{
     public static void main(String argv[])
     {
          if (argv.length != 2)
          {
               System.err.println("Il faut 2 arguments exactement : @IP et port du serveur");
               System.exit(-1);
          }

          String adresse = argv[0];
          int port = 0;
          try
          {
               port = Integer.parseInt(argv[1]);
          }
          catch (Exception e)
          {
               System.err.println("Le 2eme argument doit etre un nombre (port)");
               System.exit(-1);
          }

          try
          {
               InetAddress adr;
               DatagramPacket packet;
               DatagramSocket socket;

               // adr contient l'@IP de la partie serveur
               adr = InetAddress.getByName(adresse);

               // données à envoyer : chaîne de caractères
               byte[] data = (new String("youpi")).getBytes();

               // création du paquet avec les données et en précisant l'adresse du serveur
               packet = new DatagramPacket(data, data.length, adr, port);

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
