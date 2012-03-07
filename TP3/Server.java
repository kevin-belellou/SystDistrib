import java.net.*;

public class Server
{
     public static void main(String argv[])
     {
          if (argv.length != 1)
          {
               System.err.println("Il faut 1 argument exactement : port d'ecoute");
               System.exit(-1);
          }

          int port = 0;
          try
          {
               port = Integer.parseInt(argv[0]);
          }
          catch (Exception e)
          {
               System.err.println("L'argument doit etre un nombre (port)");
               System.exit(-1);
          }

          try
	     {
               DatagramSocket socket;
               DatagramPacket packet;

               // création d'une socket liée au port 7777
               socket = new DatagramSocket(port);

               // tableau de 15 octets qui contiendra les données reçues
               byte[] data = new byte[50];

               // création d'un paquet en utilisant le tableau d'octets
               packet = new DatagramPacket(data, data.length);

               // attente de la réception d'un paquet. Le paquet reçu est placé dans
               // packet et ses données dans data.
               socket.receive(packet);

               // récupération et affichage des données
               String chaine = new String(packet.getData(), 0, packet.getLength());
               InetAddress adrClient = packet.getAddress();
               Integer portClient = new Integer(packet.getPort());

               System.out.println("recu : " + chaine);
               System.out.println("depuis : " + adrClient.getHostAddress() + ":" + portClient);

               data = (new String("Ah ouais toi aussi ?")).getBytes();
               packet = new DatagramPacket(data, data.length, adrClient, portClient);
               socket.send(packet);
          }
          catch (Exception e)
          {
               System.err.println(e);
	     }
     }
}
