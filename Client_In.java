import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client_In extends Thread implements Parametres {
	
	public Socket s;
	public ObjectInputStream in;
	public Monde monde;
	
	public Client_In(Socket s, Monde monde) throws IOException
	{
		this.s = s;
		this.monde = monde;
	}
	
	public void receive() throws ClassNotFoundException, IOException
	{
		monde = (Monde)in.readObject();
	}
	
	@Override
	public void run() {

		try {
			
			in = new ObjectInputStream(s.getInputStream());

			while(true)
			{
				receive();
			}
		
		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		
	}

}
