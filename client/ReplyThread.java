import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ReplyThread extends Thread {

	private PrintWriter outChannel;
	private boolean running = true;
	public boolean ok = false;
	
	private Client client;
	
	public ReplyThread (PrintWriter outChannel, Client client){
		this.outChannel = outChannel;
		this.client = client;
	}
	
	public void run(){
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		while(running){
			try{
				input = stdIn.readLine();
			} catch (Exception e){
				System.err.println("There was a problem in reading the input");
				
			}
			while ((input != null) && (!input.equals(""))){
				outChannel.println(input);
				input = null;
				try{
					Thread.sleep(10);
					if (!client.getOk()){
						throw new Exception("Message not recieved by server!");
					}
				} catch (Exception e){
					System.err.println(e);
					System.exit(0);
				}
			}
		}
	}
	
	public void terminate(){
		running = false;
		outChannel.println("//disconnect//");
	}
	
}
