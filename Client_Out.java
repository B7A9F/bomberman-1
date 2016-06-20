import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client_Out extends Thread implements Parametres {
	
	public Socket s;
	public ObjectOutputStream out;
	public EntreeClavier clavier;
	
	public Client_Out(Socket s, EntreeClavier clavier) throws IOException
	{
		this.s = s;
		this.clavier = clavier;
	}
	
	public void send() throws IOException
	{
		clavier.latence = System.currentTimeMillis();
    	out.reset();
    	out.writeObject(clavier);
    	out.flush();
    	clavier.message = null;
	}
	
	@Override
	public void run()
	{
		try {
			
			out = new ObjectOutputStream(s.getOutputStream());
			
	        long beforeTime, timeDiff, sleep;
	        beforeTime = System.currentTimeMillis();
	
	        while(true)
	        {
	        	send();
	        	
	            timeDiff = System.currentTimeMillis() - beforeTime;
	            sleep = DELAI - timeDiff;
	            if (sleep < 0) 
	                sleep = 2;
	            try {
	                Thread.sleep(sleep);
	            } catch (InterruptedException e) {
	                System.out.println("interrupted");
	            }
	            beforeTime = System.currentTimeMillis();
	        }
        
		} catch (IOException e1) {e1.printStackTrace();}
    }

}
