import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Monde implements Parametres, Serializable {
	
	public ArrayList<Joueur> joueurs;
	public ArrayList<Mur> murs;
	public ArrayList<Sol> sols;
	public ArrayList<Bombe> bombes;
	public ArrayList<Caisse> caisses;
	public ArrayList<Bonus> bonus;
	public MeilleurScore meilleurScore;
	public ArrayList<Message> messages;

	public String carte = "############\n#          #\n#          #\n#          #\n#          #\n#          #\n"
			+ "#          #\n#          #\n#          #\n#          #\n############";
	
	public Monde()
    {
    	joueurs = new ArrayList<Joueur>();
    	murs = new ArrayList<Mur>();
    	sols = new ArrayList<Sol>();
    	bombes = new ArrayList<Bombe>();
    	caisses = new ArrayList<Caisse>();
    	bonus = new ArrayList<Bonus>();
    	meilleurScore = new MeilleurScore();
    	messages = new ArrayList<Message>();
    	
    	chargerCarte();
    }
	
	public void chargerCarte()
	{
		int x = 0, y = 0;
		for(int i=0; i < carte.length(); i++)
		{
			switch(carte.charAt(i))
			{
			case '#':
				murs.add(new Mur(x, y));
				x += 50;
				break;
			case ' ':
				sols.add(new Sol(x, y));
				x += 50;
				break;
			case '\n':
				x = 0;
				y += 50;
				break;
			}
		}
	}
	
	public synchronized void maj()
    {
    	majJoueur();
    	majBombes();
    	
    	majSupprimer();
    	
    	majAjouterCaisse();
    }
	
	public void majJoueur()
    {
		for(Joueur joueur : joueurs)
		{
			if(joueur.bombeTimer > 0)
				joueur.bombeTimer--;
			
			if(joueur.renaissance > 0)
				joueur.renaissance--;
			
			if(joueur.invincible)
			{
				if(joueur.invincibleTemps > 0)
					joueur.invincibleTemps--;
				else
					joueur.invincible = false;
			}
			
			if(joueur.score > meilleurScore.score)
			{
				meilleurScore.score = joueur.score;
				meilleurScore.pseudo = joueur.nom;
			}
			
			gererEntreesClavier(joueur);
		}
    }
	
	public void gererEntreesClavier(Joueur joueur)
	{

		if(joueur.clavier.keys[BOMBE])
		{		
			if(joueur.bombeTimer == 0 && joueur.nbBombes < joueur.nbBombesMax)
			{
				bombes.add(new Bombe(joueur.x, joueur.y, joueur.id));
				
				joueur.bombeTimer = BOMBE_TIMER;
				joueur.nbBombes++;
			}
		}
		
		if (joueur.clavier.keys[LEFT] || joueur.clavier.keys[RIGHT] 
				|| joueur.clavier.keys[UP] || joueur.clavier.keys[DOWN])
        {
			int new_x = joueur.x;
			int new_y = joueur.y;
			
			if(joueur.clavier.keys[UP])
			{
				new_y -= DEPLACEMENT;
				joueur.direction = UP;
			}
			if(joueur.clavier.keys[DOWN])
			{
				new_y += DEPLACEMENT;
				joueur.direction = DOWN;
			}
			if(joueur.clavier.keys[LEFT])
			{
				new_x -= DEPLACEMENT;
				joueur.direction = LEFT;
			}
			if(joueur.clavier.keys[RIGHT])
			{
				new_x += DEPLACEMENT;
				joueur.direction = RIGHT;
			}
			
			Rectangle rectJoueur = new Rectangle(new_x, new_y, TAILLE_LARGEUR_PERSO, TAILLE_HAUTEUR_PERSO);
			
			if(!collisionMur(rectJoueur) && !collisionCaisse(rectJoueur) && 
					!joueurCollisionBombe(joueur.id, rectJoueur) && !joueurCollisionJoueur(joueur.id, rectJoueur))
			{
				joueur.x = new_x;
				joueur.y = new_y;
			}
			
			joueurCollisionsBonus(joueur.id, rectJoueur);
        }
		
		joueur.latence = joueur.clavier.latence;
	}
    
    public void majBombes()
    {
    	for(Bombe bombe : bombes)
    	{
    		if(bombe.superpose)
    		{
    			Joueur joueurBombe = getJoueur(bombe.idJoueur);
    			if(joueurBombe != null)
    			{
	    			Rectangle rectJoueur = new Rectangle(joueurBombe.x, joueurBombe.y, TAILLE_LARGEUR_PERSO, TAILLE_HAUTEUR_PERSO);
	    			Rectangle rectBombe = new Rectangle(bombe.x, bombe.y, TAILLE_BOMBE, TAILLE_BOMBE);
	    			if(!rectJoueur.intersects(rectBombe))
	    				bombe.superpose = false;
    			}
    		}
    		
    		if(!bombe.explosion && bombe.explosionDans > 0)
    			bombe.explosionDans--;
    		else if(!bombe.explosion && bombe.explosionDans == 0)
    			bombe.explose();
    		else if(bombe.explosion && bombe.explosionTemps == 0)
    		{
    			Joueur joueurBombe = getJoueur(bombe.idJoueur);
    			if(joueurBombe != null)
    				joueurBombe.nbBombes = Math.max(0, getJoueur(bombe.idJoueur).nbBombes - 1);
    			bombe.visible = false;
    		}
    		else if(bombe.explosion && bombe.explosionTemps > 0)
    			bombe.explosionTemps--;
    		
    		if(bombe.explosion)
    		{

    			Ellipse2D.Double rectBombe = new Ellipse2D.Double(bombe.x, bombe.y, TAILLE_EXPLOSION_BOMBE, TAILLE_EXPLOSION_BOMBE);
    			
    			bombeCollisionsJoueurs(bombe.idJoueur, rectBombe);
    			bombeCollisionsBombes(rectBombe);
    			bombeCollisionsCaisses(rectBombe);
    		}
    	}
    }
    
    public void majSupprimer()
    {
    	for(int i = joueurs.size()-1; i >= 0; i--)
		{
    		Joueur joueur = joueurs.get(i);
    		if(!joueur.connecte)
    			joueurs.remove(i);
    		else if(!joueur.visible)
    		{
    			int id = joueur.id;
				creerJoueur(joueur.id, joueur.nom);
				joueurs.remove(i);
				getJoueur(id).renaissance = DUREE_RENAISSANCE;
    		}
		}
    	
    	for(int i = bombes.size()-1; i >= 0; i--)
		{
    		Bombe bombe = bombes.get(i);
    		if(!bombe.visible)
    			bombes.remove(i);
		}
    	
    	for(int i = caisses.size()-1; i >= 0; i--)
		{
    		Caisse caisse = caisses.get(i);
    		if(!caisse.visible)
    			caisses.remove(i);
		}
    	
    	for(int i = bonus.size()-1; i >= 0; i--)
		{
    		Bonus bonu = bonus.get(i);
    		if(!bonu.visible)
    			bonus.remove(i);
		}
    }
    
    public void creerJoueur(int id, String pseudo)
	{
		boolean collision;
		do
		{
			collision = true;
			
			int x = TAILLE_MUR + (int)(Math.random() * ((LARGEUR_SCENE - 2*TAILLE_MUR - TAILLE_MUR - TAILLE_LARGEUR_PERSO) + 1));
			int y = TAILLE_MUR + (int)(Math.random() * ((HAUTEUR_SCENE - 2*TAILLE_MUR - TAILLE_MUR - TAILLE_HAUTEUR_PERSO) + 1));
			
			Rectangle rectJoueur = new Rectangle(x, y, TAILLE_LARGEUR_PERSO, TAILLE_HAUTEUR_PERSO);
			
			if(!collisionMur(rectJoueur) && !collisionCaisse(rectJoueur) && 
					!joueurCollisionBombe(id, rectJoueur) && !joueurCollisionJoueur(id, rectJoueur))
			{
				joueurs.add(new Joueur(x, y, id, pseudo));
				collision = false;
			}
			
		} while(collision);
	}
    
    public void majAjouterCaisse()
    {
    	if(caisses.size() < NB_MAX_CAISSES && joueurs.size() > 0)
    	{
			if(1 + (int)(Math.random() * ((PROBA_AJOUTER_CAISSE - 1) + 1)) == 1)
			{
				int type = CAISSE_VIDE;
				
				if(1 + (int)(Math.random() * ((PROBA_BONUS_VIE - 1) + 1)) == 1)
					type = CAISSE_BONUS_VIE;
				else if(1 + (int)(Math.random() * ((PROBA_BONUS_BOMBE - 1) + 1)) == 1)
					type = CAISSE_BONUS_BOMBE;
				
				boolean collision;
				do
				{
					collision = true;
					
					int x = TAILLE_MUR + (int)(Math.random() * ((LARGEUR_SCENE - 2*TAILLE_MUR - TAILLE_MUR - TAILLE_CAISSE) + 1));
					int y = TAILLE_MUR + (int)(Math.random() * ((HAUTEUR_SCENE - 2*TAILLE_MUR - TAILLE_MUR - TAILLE_CAISSE) + 1));
					
					Rectangle rectCaisse = new Rectangle(x, y, TAILLE_CAISSE, TAILLE_CAISSE);
					
					if(!collisionJoueur(rectCaisse) && ! collisionCaisse(rectCaisse))
					{
						caisses.add(new Caisse(x, y, type));
						collision = false;
					}
				} while(collision);
			}
    	}
    }
    
    public synchronized void addMessage(Message message)
    {
    	messages.add(message);
    	if(messages.size() > 8)
    		messages.remove(0);
    }
    
    public boolean joueurCollisionJoueur(int id, Rectangle rect)
	{
		for(Joueur joueur : joueurs)
		{
			if(joueur.id != id)
			{
				Rectangle rectJoueur = new Rectangle(joueur.x, joueur.y, TAILLE_LARGEUR_PERSO, TAILLE_HAUTEUR_PERSO);
				if(rect.intersects(rectJoueur))
					return true;
			}
		}
		return false;
	}
	
	public boolean collisionMur(Rectangle rect)
	{
		for(Mur mur : murs)
		{
			Rectangle rectMur = new Rectangle(mur.x, mur.y, TAILLE_MUR, TAILLE_MUR);
			if(rect.intersects(rectMur))
				return true;
		}
		return false;
	}
	
	public boolean collisionCaisse(Rectangle rect)
	{
		for(Caisse caisse : caisses)
		{
			Rectangle rectCaisse = new Rectangle(caisse.x, caisse.y, TAILLE_CAISSE, TAILLE_CAISSE);
			if(rect.intersects(rectCaisse))
				return true;
		}
		return false;
	}
	
	public boolean joueurCollisionBombe(int idJoueur, Rectangle rect)
	{
		for(Bombe bombe : bombes)
		{
			if(!(bombe.idJoueur == idJoueur && bombe.superpose))
			{
				Rectangle rectBombe = new Rectangle(bombe.x, bombe.y, TAILLE_BOMBE, TAILLE_BOMBE);
				if(rect.intersects(rectBombe))
					return true;
			}
		}
		return false;
	}
	
	public boolean collisionJoueur(Rectangle rect)
	{
		for(Joueur joueur : joueurs)
		{
			Rectangle rectJoueur = new Rectangle(joueur.x, joueur.y, TAILLE_LARGEUR_PERSO, TAILLE_HAUTEUR_PERSO);
			if(rect.intersects(rectJoueur))
				return true;
		}
		
		return false;
	}
	
	public void joueurCollisionsBonus(int idJoueur, Rectangle rect)
	{
		for(Bonus bonu : bonus)
		{
			Rectangle rectBonus = new Rectangle(bonu.x, bonu.y, TAILLE_CAISSE, TAILLE_CAISSE);

			if(rect.intersects(rectBonus))
			{
				bonu.visible = false;
				
				getJoueur(idJoueur).bonus(bonu.type);
			}
		}
	}
	
	public void bombeCollisionsJoueurs(int idJoueur, Ellipse2D.Double rect)
	{
		for(Joueur joueur : joueurs)
		{
			if(!joueur.invincible)
			{
				Rectangle rectJoueur = new Rectangle(joueur.x, joueur.y, TAILLE_LARGEUR_PERSO, TAILLE_HAUTEUR_PERSO);
				if(rect.intersects(rectJoueur))
				{
					joueur.blesse();
					
					Joueur joueurBombe = getJoueur(idJoueur);
					if(joueurBombe != null && idJoueur != joueur.id)
						joueurBombe.score++;
				}
			}
		}
	}

	public void bombeCollisionsBombes(Ellipse2D.Double rect)
	{
		for(Bombe bombe : bombes)
		{
			if(!bombe.explosion)
			{
				Rectangle rectBalle = new Rectangle(bombe.x, bombe.y, TAILLE_BOMBE, TAILLE_BOMBE);
	
				if(rect.intersects(rectBalle))
					bombe.explose();
			}
		}
	}
	
	public void bombeCollisionsCaisses(Ellipse2D.Double rect)
	{
		for(Caisse caisse : caisses)
		{
			Rectangle rectCaisse = new Rectangle(caisse.x, caisse.y, TAILLE_CAISSE, TAILLE_CAISSE);

			if(rect.intersects(rectCaisse))
			{
				caisse.visible = false;
				
				if(caisse.type != CAISSE_VIDE)
				{
					bonus.add(new Bonus(caisse.x, caisse.y, caisse.type));
				}
				
			}
		}
	}
	
	public Joueur getJoueur(int id)
	{
		for(Joueur joueur : joueurs)
			if(joueur.id == id)
				return joueur;
		return null;
	}
	
	public synchronized Monde getCopie() throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);
        out.flush();
        out.close();

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        Monde copie = (Monde)in.readObject();
        copie.murs = null;
        copie.sols = null;
        copie.carte = null;
        
        return copie;
	}
	
}
