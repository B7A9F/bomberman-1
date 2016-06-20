import java.io.Serializable;

public class Message implements Serializable {
	
	public String pseudo;
	public String texte;
	
	public Message(String pseudo, String texte)
	{
		this.pseudo = pseudo;
		this.texte = texte;
	}

}
