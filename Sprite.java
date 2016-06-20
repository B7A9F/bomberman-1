import java.io.Serializable;

public class Sprite implements Parametres, Serializable {
	
	public int x;
	public int y;
	public boolean visible;

    public Sprite()
    {
        visible = true;
    }

}
