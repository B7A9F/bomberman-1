import java.io.Serializable;

public class EntreeClavier implements Serializable {
	
	public int id;
	public boolean keys[];
	public Message message;
	public long latence;
	
	public EntreeClavier(int id, boolean keys[])
	{
		this.id = id;
		this.keys = keys;
		message = null;
		latence = 0;
	}

}
