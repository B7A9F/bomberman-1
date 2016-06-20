
public class Bombe extends Sprite {
	
	public int idJoueur;
	public boolean explosion;
	public int explosionDans;
	public int explosionTemps;
	public boolean superpose;
	
    public Bombe(int x, int y, int idJoueur)
    {
        this.x = x;
        this.y = y;
        this.idJoueur = idJoueur;
        explosion = false;
        explosionDans = BOMBE_EXPLOSION_DANS;
        superpose = true;
    }
    
    public void explose()
    {
    	explosion = true;
    	explosionTemps = EXPLOSION_TEMPS;
    	
    	x = x - TAILLE_EXPLOSION_BOMBE/2 + TAILLE_BOMBE/2;
		y = y - TAILLE_EXPLOSION_BOMBE/2 + TAILLE_BOMBE/2;
    }

}
