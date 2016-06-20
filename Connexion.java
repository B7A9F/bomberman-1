import java.io.Serializable;

public class Connexion implements Serializable {
	
	public double version;
	public int idJoueur;
	public String pseudo;
	public boolean ok;
	public String message;
	
	public Connexion(double version, String pseudo)
	{
		this.version = version;
		this.pseudo = pseudo;
	}
	
	public Connexion(boolean ok, int idJoueur, String message)
	{
		this.ok = ok;
		this.idJoueur = idJoueur;
		this.message = message;
	}
	
}
