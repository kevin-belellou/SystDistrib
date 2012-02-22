import java.net.*;
import java.io.*;


public class Client
{
     public static void main(String argv[])
     {	
          try
	     {
               // adresse IP du serveur
               InetAddress adr = InetAddress.getByName("localhost");

               // ouverture de connexion avec le serveur sur le port 7777
               Socket socket = new Socket(adr, 7777);


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
