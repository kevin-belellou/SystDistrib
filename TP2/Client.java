import java.net.*;
import java.io.*;

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
               // adresse IP du serveur
               InetAddress adr = InetAddress.getByName(adresse);

               // ouverture de connexion avec le serveur sur le port 7777
               Socket socket = new Socket(adr, port);

               // construction de flux objets à partir des flux de la socket
               ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
               ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

               // écriture d'une chaîne dans le flux de sortie : c'est-à-dire envoi de
               // données au serveur
               output.writeObject(new String("youpi"));

               // attente de réception de données venant du serveur (avec le readObject)
               // on sait qu'on attend une chaîne, on peut donc faire un cast directement
               String chaine = (String)input.readObject();
               System.out.println(" recu du serveur : "+chaine);
          }
          catch (Exception e)
          {
               System.err.println(e);
	     }
     }
}
