import java.io.BufferedReader;

public class ChatThread extends Thread {

	private BufferedReader inChannel;
	private boolean running = true;
	private String output = null;
	
	private Client client;
	
	public ChatThread(BufferedReader inChannel, Client client){
		this.inChannel = inChannel;
		this.client = client;
	}
	
	public void run(){
		while(running){
			try{
				output = inChannel.readLine();
			} catch (Exception e){
				System.err.println("The message from the server was corrupted");
			}
			try{
				while (output != null){
					if (!output.equals("//ok//") && !output.equals("//closed//")){
						System.out.println(output);
					} else if (output.equals("//closed//")){
						System.err.println("The connection has closed.");
						System.exit(0);
					} else {
						client.setOK(true);
						Thread.sleep(50);
						client.setOK(false);
					}
					output = null;
				}
			} catch (InterruptedException e){
				System.err.println("There was an issue with putting the thread to sleep");
			}
		}
	}

	public void terminate(){
		running = false;
	}
	
}
