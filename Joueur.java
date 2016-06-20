
public class Joueur extends Sprite {

	public int id;
	public String nom;
    public int direction;
    public boolean connecte;
    public int vies;
    public int score;
    public boolean invincible;
    public int invincibleTemps;
    public EntreeClavier clavier;
    public int bombeTimer;
    public int nbBombesMax;
    public int nbBombes;
    public int renaissance;
    public long latence;

    public Joueur(int x, int y, int id, String nom)
    {
    	this.x = x;
        this.y = y;
    	this.id = id;
    	this.nom = nom;
        direction = DOWN;
        connecte = true;
        vies = 3;
        score = 0;
        invincible = true;
        invincibleTemps = DUREE_INVINCIBLE;
        clavier = new EntreeClavier(id, new boolean[50]);
        bombeTimer = 0;
        nbBombes = 0;
        nbBombesMax = 2;
        renaissance = 0;
        latence = 0;
    }
    
    public void blesse()
    {
    	vies--;
    	if(vies <= 0)
    		visible = false;
		invincible = true;
		invincibleTemps = DUREE_INVINCIBLE;
    }
    
    public void bonus(int type)
    {
    	if(type == CAISSE_BONUS_BOMBE)
    		nbBombesMax = Math.min(nbBombesMax + 1, MAX_BOMBES);
    	else if(type == CAISSE_BONUS_VIE)
    		vies = Math.min(vies + 1, MAX_VIES);
    }

}