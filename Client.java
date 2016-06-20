import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Client extends JFrame implements Runnable, Parametres {

	public Socket s;
	
	public Scene scene;
	public Monde monde;
	public Thread animator;

	public Client_In client_in;
	public Client_Out client_out;
	
	public int portServeur;
	public String ipServeur;
	
	public int idJoueur;
	public String pseudoJoueur;
	public EntreeClavier clavier;
	
	public JTextArea jContenu;

	public Client() throws ClassNotFoundException, IOException, InterruptedException {

        clavier = new EntreeClavier(idJoueur, new boolean[5]);

        connexionDlg();
        connexion();
        
        scene = new Scene(idJoueur, pseudoJoueur, monde, clavier);
        creerFenetre();

    	animator = new Thread(this);
    	animator.start();
	}
	
	public void creerFenetre() throws ClassNotFoundException, IOException, InterruptedException
	{
		setLayout(new BorderLayout());
        add(scene, BorderLayout.CENTER);
        JPanel jMessages = new JPanel();
        jMessages.setLayout(new BorderLayout());
        jContenu = new JTextArea();
        jContenu.setFocusable(false);
        jMessages.add(jContenu, BorderLayout.CENTER);
        add(jMessages, BorderLayout.SOUTH);
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGEUR_SCENE + 200, HAUTEUR_SCENE + 150);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
	}
	
	public void connexionDlg()
    {
    	JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
    	ConnexionDlg diag = new ConnexionDlg(parent, true);
    	diag.setVisible(true);
    	
    	if(diag.ok)
    	{
    		this.pseudoJoueur = diag.pseudo;
    		this.ipServeur = diag.ipServeur;
    		this.portServeur = Integer.valueOf(diag.portServeur);
        	diag.dispose();
    	}
    	else
    		System.exit(0);
    }
    
    public void connexion() throws UnknownHostException, IOException, ClassNotFoundException
    {
    	s = new Socket(ipServeur, portServeur);
    	ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
    	ObjectInputStream in = new ObjectInputStream(s.getInputStream());
    	
    	out.writeObject(new Connexion(VERSION, pseudoJoueur));
    	out.flush();
    	
    	Connexion conReponse = (Connexion)in.readObject();
    	if(!conReponse.ok)
    	{
    		JOptionPane.showMessageDialog(null, conReponse.message, "Erreur", JOptionPane.ERROR_MESSAGE);   
    		System.exit(0);
    	}
    	else
    	{
    		idJoueur = conReponse.idJoueur;
    		monde = (Monde)in.readObject();
    		
	        client_in = new Client_In(s, monde);
	        client_in.start();
	        client_out = new Client_Out(s, clavier);
	        client_out.start();
    	}
    }

	public void afficherMessages()
	{
		String s = "";
		for(Message message : monde.messages)
			s += message.pseudo + " dit : " + message.texte + "\n";
		jContenu.setText(s);
	}
	
	public void run()
	{
        long beforeTime, timeDiff, sleep;
        beforeTime = System.currentTimeMillis();

        while(true)
        {
        	monde.joueurs = client_in.monde.joueurs;
        	monde.bombes = client_in.monde.bombes;
        	monde.bonus = client_in.monde.bonus;
        	monde.caisses = client_in.monde.caisses;
        	monde.meilleurScore = client_in.monde.meilleurScore;
        	monde.messages = client_in.monde.messages;
        	
            scene.repaint();
            afficherMessages();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAI - timeDiff;
            if (sleep < 0) 
                sleep = 2;
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {e.printStackTrace();}
            beforeTime = System.currentTimeMillis();
        }
    }
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
