import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Scene extends JPanel implements Parametres {
	
	private class TAdapter extends KeyAdapter {

        public void keyPressed(KeyEvent e)
        {
        	if(e.getKeyCode() == KeyEvent.VK_UP)
        		clavier.keys[0] = true;
        	else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        		clavier.keys[1] = true;
        	else if(e.getKeyCode() == KeyEvent.VK_DOWN)
        		clavier.keys[2] = true;
        	else if(e.getKeyCode() == KeyEvent.VK_LEFT)
        		clavier.keys[3] = true;
        	if(e.getKeyCode() == KeyEvent.VK_SPACE)
        		clavier.keys[4] = true;
        }
        
        public void keyReleased(KeyEvent e)
        {
        	if(e.getKeyCode() == KeyEvent.VK_UP)
        		clavier.keys[0] = false;
        	else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
        		clavier.keys[1] = false;
        	else if(e.getKeyCode() == KeyEvent.VK_DOWN)
        		clavier.keys[2] = false;
        	else if(e.getKeyCode() == KeyEvent.VK_LEFT)
        		clavier.keys[3] = false;
        	if(e.getKeyCode() == KeyEvent.VK_SPACE)
        		clavier.keys[4] = false;
        	if(e.getKeyCode() == KeyEvent.VK_ENTER)
        		envoyerMessage();
        }
    }

	public Monde monde;
	public int idJoueur;
	public String pseudoJoueur;
	public EntreeClavier clavier;
	public boolean fenetreMessage;
	
	public Image imageJoueurFace;
	public Image imageJoueurDos;
	public Image imageJoueurGauche;
	public Image imageJoueurDroite;
	public Image imageBombe;
	public Image imageBombeExplose;
	public Image imageMur;
	public Image imageSol;
	public Image imageVie;
	public Image imageViePerdue;
	public Image imageCaisse;
	public Image imageBonus;
	public Image imageBonusUsee;
	public Image imageMort;
	public Image imageCouronne;
	
    public Scene(int idJoueur, String pseudoJoueur, Monde monde, EntreeClavier clavier) throws IOException, InterruptedException, ClassNotFoundException
    {
    	this.idJoueur = idJoueur;
    	this.pseudoJoueur = pseudoJoueur;
    	this.monde = monde;
    	this.clavier = clavier;
    	fenetreMessage = false;
    	
    	addKeyListener(new TAdapter());
        setFocusable(true);
        setDoubleBuffered(true);
        setBackground(Color.white);
        
        chargerImages();
    }

    public void chargerImages()
    {
    	imageJoueurFace = new ImageIcon(this.getClass().getResource("joueur_face.png")).getImage();
    	imageJoueurDos = new ImageIcon(this.getClass().getResource("joueur_dos.png")).getImage();
    	imageJoueurGauche = new ImageIcon(this.getClass().getResource("joueur_gauche.png")).getImage();
    	imageJoueurDroite = new ImageIcon(this.getClass().getResource("joueur_droite.png")).getImage();
    	imageBombe = new ImageIcon(this.getClass().getResource("bombe.png")).getImage();
    	imageBombeExplose = new ImageIcon(this.getClass().getResource("bombe_explose.png")).getImage();
    	imageMur = new ImageIcon(this.getClass().getResource("mur.png")).getImage();
    	imageSol = new ImageIcon(this.getClass().getResource("sol.png")).getImage();
    	imageVie = new ImageIcon(this.getClass().getResource("vie.png")).getImage();
    	imageViePerdue = new ImageIcon(this.getClass().getResource("vie_perdue.png")).getImage();
    	imageCaisse = new ImageIcon(this.getClass().getResource("caisse.png")).getImage();
    	imageBonus = new ImageIcon(this.getClass().getResource("bonus.png")).getImage();
    	imageBonusUsee = new ImageIcon(this.getClass().getResource("bonus_usee.png")).getImage();
    	imageMort = new ImageIcon(this.getClass().getResource("mort.png")).getImage();
    	imageCouronne = new ImageIcon(this.getClass().getResource("couronne.png")).getImage();
    }
    
    public void paint(Graphics g)
    {
    	super.paint(g);

    	dessinerSols(g);
    	dessinerMurs(g);
    	dessinerCaisses(g);
    	dessinerBonus(g);
    	dessinerBombes(g);
    	dessinerJoueur(g);
    	
    	afficherInfosJoueur(g);
    	afficherScores(g);
      
    	Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    
    public void dessinerJoueur(Graphics g)
    {
    	ArrayList<Joueur> joueurs = monde.joueurs;
    	for(Joueur joueur : joueurs)
    	{
    		Image im;
    		if(joueur.direction == RIGHT)
				im = imageJoueurDroite;
			else if(joueur.direction == UP)
				im = imageJoueurDos;
			else if(joueur.direction == LEFT)
				im = imageJoueurGauche;
			else
				im = imageJoueurFace;
    		
    		g.drawImage(im, joueur.x, joueur.y, this);
    		
    		if(joueur.id != idJoueur)
				g.drawString(joueur.nom, joueur.x - 10, joueur.y - 5);
    	}
    }
    
    public void dessinerCaisses(Graphics g)
    {
    	ArrayList<Caisse> caisses = monde.caisses;
    	for(Caisse caisse : caisses)
    		g.drawImage(imageCaisse, caisse.x, caisse.y, this);
    }
    
    public void dessinerBonus(Graphics g)
    {
    	ArrayList<Bonus> bonus = monde.bonus;
    	for(Bonus bonu : bonus)
    	{
    		if(bonu.type == CAISSE_BONUS_BOMBE)
    			g.drawImage(imageBonus, bonu.x, bonu.y, this);
    		else if(bonu.type == CAISSE_BONUS_VIE)
    			g.drawImage(imageVie, bonu.x, bonu.y, this);
    	}
    }
    
    public void dessinerBombes(Graphics g)
    {
    	ArrayList<Bombe> bombes = monde.bombes;
    	for(Bombe bombe : bombes)
    	{
    		if(bombe.explosion)
    			g.drawImage(imageBombeExplose, bombe.x, bombe.y, this);
    		else
    			g.drawImage(imageBombe, bombe.x, bombe.y, this);
    	}
    }
    
    public void dessinerMurs(Graphics g)
    {
    	ArrayList<Mur> murs = monde.murs;
    	for(Mur mur : murs)
    		g.drawImage(imageMur, mur.x, mur.y, this);
    }
    
    public void dessinerSols(Graphics g)
    {
    	ArrayList<Sol> sols = monde.sols;
    	for(Sol sol : sols)
    		g.drawImage(imageSol, sol.x, sol.y, this);
    }
    
    public void envoyerMessage()
    {
    	if(!fenetreMessage)
		{
    		String message = JOptionPane.showInputDialog("Message : ", "");
    		
    		if(message != null && message.length() <= LONGUEUR_MAX_MESSAGE)
    			clavier.message = new Message(pseudoJoueur, message);
    		
    		fenetreMessage = true;
    		requestFocus();
		}
		else
			fenetreMessage = false;
    }
    
    public void afficherInfosJoueur(Graphics g)
    {
    	Joueur joueur = monde.getJoueur(idJoueur);
    	
    	if(joueur.renaissance > 0)
    		g.drawImage(imageMort, LARGEUR_SCENE/2 - 150, HAUTEUR_SCENE/2 - 200, this);

    	g.setFont(new Font("Arial", 0, 20));
    	g.setColor(Color.white);
    	
		int x = 10;
		int y = HAUTEUR_SCENE - TAILLE_MUR - 20;

    	g.drawString("score : " + joueur.score, x, y);
    	
    	g.setFont(new Font("Arial", 0, 10));
    	long latence = System.currentTimeMillis() - joueur.latence;
    	if(latence < 200)
    		g.setColor(Color.green);
    	else if(latence < 400)
    		g.setColor(Color.orange);
    	else
    		g.setColor(Color.red);
    		
    	g.drawString("latence : " + latence, x, y+15);
    	
    	x += 120;
    	y -= 20;
    	for(int i=0; i < joueur.vies; i++)
    	{
    		g.drawImage(imageVie, x, y, this);
    		x+= 30;
    	}
    	
    	for(int i= joueur.vies; i < MAX_VIES; i++)
    	{
    		g.drawImage(imageViePerdue, x, y, this);
    		x+= 30;
    	}
    	
    	x += 20;
    	for(int i=0; i < joueur.nbBombesMax - joueur.nbBombes; i++)
    	{
    		g.drawImage(imageBonus, x, y, this);
    		x+= 30;
    	}
    	for(int i = joueur.nbBombesMax - joueur.nbBombes; i < joueur.nbBombesMax; i++)
    	{
    		g.drawImage(imageBonusUsee, x, y, this);
    		x+= 30;
    	}
    }
    
    public void afficherScores(Graphics g)
    {	
    	g.setFont(new Font("Arial", 0, 20));

    	int x = LARGEUR_SCENE + 5;
    	int y = 20;
    	
    	if(!monde.meilleurScore.pseudo.equals(""))
    	{
    		g.drawImage(imageCouronne, x, y, this);
	    	g.setColor(Color.orange);
	    	g.drawString(monde.meilleurScore.pseudo + " : " + monde.meilleurScore.score, x+40, y+25);
	    	y += 80;
    	}

    	x += 5;
    	g.setColor(Color.black);
    	for(Joueur joueur : monde.joueurs)
    	{
    		g.drawString(joueur.nom + " : " + joueur.score, x, y);
    		y += 20;
    		g.drawString(String.valueOf(joueur.vies), x, y+10);
    		x+= 30;
    		g.drawImage(imageVie, x - TAILLE_CAISSE/2, y - TAILLE_CAISSE/2, this);
    		x += 30;
    		g.drawString(String.valueOf(joueur.nbBombesMax), x, y+10);
    		x+= 30;
    		g.drawImage(imageBonus, x - TAILLE_CAISSE/2, y - TAILLE_CAISSE/2, this);
    		
    		x -= 90;
    		y += 60;	
    	}
    }
    

}
