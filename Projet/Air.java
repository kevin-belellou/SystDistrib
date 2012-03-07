import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import java.lang.Thread;

import java.util.Random;
import java.lang.Math;
import java.text.DecimalFormat;

/**
 * Programme qui simule l'évolution de la température de l'air d'une
 * pièce en fonction de la température extérieure et du niveau de
 * chauffage demandé. Chaque seconde la température courante est
 * envoyée via un message de type <a
 * href="MessageTemperature.html">MessageTemperature</a> sur le groupe
 * multicast du programme.<br /> <br /> 
 *
 * Les demandes de chauffage sont prises en compte une fois toutes les
 * 3 secondes (si plusieurs demandes sont arrivées dans un intervalle
 * de 3 secondes, on prend uniquement en compte la dernière). Une
 * demande de chauffage est traitée une seule fois. Si l'on veut
 * chauffer en continu l'air, il faut donc envoyer régulièrement des
 * demandes de chauffage. Une demande de chauffage est effectuée par
 * l'envoi d'un message de type <a
 * href="MessageTemperature.html">MessageTemperature</a> sur le groupe
 * multicast du programme.<br /> <br />
 *
 * La température extérieure évolue entre une température minimale (la
 * nuit) et une température maximale (le jour) de manière linéaire, en
 * augmentant d'abord la première demi-journée puis diminuant ensuite
 * lors de la seconde. Une journée complète dure 5 minutes. Les
 * températures minimales et maximales sont modifiées à la fin de
 * chaque journée.
 * 
 * <br /><br />Le programme se lance avec les paramètres suivants :<br />
 * <code>$ java Air groupeMulticast portMulticast nomPiece [seedRandom]</code><br/>
 * <ul>
 * <li><code>groupeMulticast</code> : adresse IP du groupe multicast à utiliser pour la pièce</li>
 * <li><code>port</code> : port du groupe multicast</li>
 * <li><code>nomPiece</code> : nom de la pièce</li>
 * <li><code>seedRandom</code> : paramètre optionnel initialisant le générateur de nombres aléatoires qui 
 * détermine la température extérieure. On lancera de préférence les programmes Air de 
 * toutes les pièces avec la même valeur.</li>
 * </ul> */
public class Air extends Thread {

    /**
     * Adresse du groupe multicast de la pièce
     */
    protected InetAddress groupMulticast;

    /**
     * Port du groupe multicast
     */ 
    protected int port;

    /**
     * Socket multicast
     */
    protected MulticastSocket socket;
    
    /**
     * Nom de la pièce
     */ 
    protected String nomPiece;

    /**
     * Température courante de la pièce
     */
    protected volatile float temperatureCourante;

    /**
     * Température extérieure de la maison
     */ 
    protected float temperatureExt;

    /**
     * Générateur de nombre aléatoire servant à initiliser puis
     * modifier la température extérieure 
     */
    protected Random generateur;

    /**
     * Format d'affichage des flottants
     */
    protected DecimalFormat format;

    /**
     * Fonction qui envoie sur le groupe multicast un message
     * précisant la température courante. Affiche sur la sortie
     * standard la valeur de la température courante et extérieure.
     * Affiche sur la sortie d'erreur un message en cas de problème. 
     */
    public void envoyerTemp()
    {
	try {
	    
	    System.out.println(this.toString());
	    MessageTemperature msg = new MessageTemperature(Math.round(temperatureCourante), 
							    MessageTemperature.MESURE, nomPiece);
	    byte tab[] = msg.toBytes();
	    socket.send(new DatagramPacket(tab, tab.length, groupMulticast, port));
	}
	catch(Exception e) {
	    System.err.println("[Erreur] envoi mesure temperature : "+e);
	}
    }
    
    /**
     * Fonction qui calcule et gère les variations de la température
     * courante en fonction de la température extérieure. Affiche sur
     * la sortie standard et envoie sur le groupe multicast la valeur
     * de la température courante 1 fois par seconde.
     */
    public void variations()
    {
	float tempNuit, tempJour;
	// durée complète d'une journée
	int intervalle = 30;

	// initialisation des températures du jour et de la nuit
	tempNuit = generateur.nextFloat() * 10 - 5;
	tempJour = generateur.nextFloat() * 10 + 10;
	temperatureExt = tempNuit;
	temperatureCourante = (tempJour - tempNuit) / 2;
		
	System.out.println(" *** valeurs initiales : nuit = "+ format.format(tempNuit) +" jour = "+format.format(tempJour));

	while (true)
	    {
		for (int j = 0; j < intervalle ; j++)
		    {	
			for (int i=0; i < 10 ; i++)
			    {
				try { Thread.sleep(1000); } catch (Exception e) { }
				// on modifie la température courante selon la température extérieure
				temperatureCourante += (temperatureExt - temperatureCourante) * 0.02;
				envoyerTemp();
			    }
			// le jour : on augmente la température extérieure
			if ( j < intervalle / 2)
			    temperatureExt += (tempJour - tempNuit) / ( intervalle / 2);	
			// la nuit : on diminue la température extérieure
			else 
			    temperatureExt -= (tempJour - tempNuit) / ( intervalle / 2);
		    }
		
		// on modifie à la fin complète de la journée les températures max et min
		tempNuit += generateur.nextFloat()* 6 - 3;
		tempJour += generateur.nextFloat()* 6 - 3;
		if (tempNuit > tempJour)
		    {
			float temp = tempNuit;
			tempNuit = tempJour;
			tempJour = temp;
		    }
	    }
    }
    
    /**
     * Thread qui toutes les 3 secondes récupère la valeur de la
     * dernière demande de chauffage et modifie le cas échéant la
     * température courante en fonction de la puissance de
     * chauffage. En cas d'erreur, se termine.
     */
    public void run() 
    {
	// lance le thread qui attend les messages sur la socket
	AttentePaquet attente = new AttentePaquet(socket);
	attente.start();
	
	int valeur;
	try {
	    while(true)
		{
		    Thread.sleep(3000);
		    valeur = attente.getDernier();
		    if (valeur >= 0)
			{
			    System.out.println(" == demande de chauffage de niveau "+valeur);
			    temperatureCourante += valeur / 4.0;
			}
		}
	}
	catch (Exception e) {
	    System.err.println("[Erreur] réception données chauffage : "+e);
	}	
    }
 
    /**
     * Initialise la socket multicast. En cas d'erreur, le programme
     * se termine.  
     */
    protected void initMulticast(String nomMachine, int port)
    {
	try  {
	    groupMulticast = InetAddress.getByName(nomMachine);
	    socket = new MulticastSocket(port);
       	    socket.joinGroup(groupMulticast);
 	}
	catch(Exception e) {
	    System.err.println("[Erreur] Impossible de créer la socket multicast : "+e);
	    
	    System.exit(1);
	}
    }
    
    public String toString()
    {
	return "Piece = "+nomPiece+" | temp = "+format.format(temperatureCourante)+ 
	    " | ext = "+format.format(temperatureExt);
    }

    public Air(String adrMulti, int port, String piece, int initRandom)
    {
	initMulticast(adrMulti, port);
	this.port = port;
	nomPiece = piece;

	generateur = new Random(initRandom);
	
	format = new DecimalFormat("00.00");
    }

    public Air(String adrMulti, int port, String piece)
    {
	initMulticast(adrMulti, port);
	this.port = port;
	nomPiece = piece;

	generateur = new Random(0);
	
	format = new DecimalFormat("00,00");
    }
 
   
    /**
     * Lance le programme Air. Les paramètres sont les suivants :<br />
     * <code>$ java Air groupeMulticast portMulticast nomPiece [seedRandom]</code><br/>
     * <ul>
     * <li><code>groupeMulticast</code> : adresse IP du groupe multicast à utiliser pour la pièce</li>
     * <li><code>port</code> : port du groupe multicast</li>
     * <li><code>nomPiece</code> : nom de la pièce</li>
     * <li><code>seedRandom</code> : paramètre optionnel initialisant le générateur de nombres aléatoires qui 
     * détermine la température extérieure. On lancera de préférence les programmes Air de 
     * toutes les pièces avec la même valeur.</li>
     * </ul>
     */
    public static void main(String argv[])
    {

	if (argv.length < 3) {
	    	System.err.println("Erreur dans les arguments !");
		System.err.println("Usage : $ java Air groupeMulticast portMulticast nomPiece [seedRandom]");
		System.exit(1);
	}
	String group = argv[0];
	int port = (new Integer(argv[1])).intValue();
	String piece = argv[2];
	int seed;
	if (argv.length >= 4) 
	    seed = (new Integer(argv[3])).intValue();
	else seed = 0;

	Air air = new Air(group, port, piece, seed);
	air.start();
	air.variations();
    }

    /**
     * Thread qui attend les paquets sur la socket
     */
    protected class AttentePaquet extends Thread 
    {
	/**
	 * Dernière demande de chauffage à prendre en compte. Une
	 * valeur de -1 signifie qu'aucune demande n'a eu lieu depuis
	 * la dernière lecture de la valeur.  
	 */
	protected int dernier = -1;
	
	/**
	 * La socket multicast sur laquelle on attend les messages.
	 * */
	protected MulticastSocket socket;
	
	/**
	 * Retourne la dernière demande de chauffage et la remet à -1.
	 */
	public synchronized int getDernier() 
	{
	    int temp = dernier;
	    dernier = -1;
	    return temp;
	}
	
	protected synchronized void setDernier(int val)
	{
	    dernier = val;
	}
	
	/**
	 * Attend en permanence des paquets sur la socket. S'il s'agit
	 * d'une demande de chauffage, modifie la valeur de l'attribut
	 * dernier. En cas d'erreur, se termine (plus aucune lecture
	 * n'est alors faite sur la socket).
	 */
	public void run()
	{
	    try {
		byte tab[] = new byte[100];
		DatagramPacket dp = new DatagramPacket(tab, tab.length);
		MessageTemperature msg;
		
		while (true)
		    {
			socket.receive(dp);
			msg = MessageTemperature.fromBytes(dp.getData(),dp.getLength());
			if (msg.getType() == MessageTemperature.CHAUFFER)
			    {
				if (msg.getValeur() >= 5) 
				    setDernier(5);
				else setDernier(msg.getValeur());
			    }
		    }
	    }
	    catch(Exception e) {
		System.err.println("[Erreur] Lecture socket : "+e);
	    } 
	}

	public AttentePaquet(MulticastSocket socket)
	{
	    this.socket = socket;
	}
    }
} 











