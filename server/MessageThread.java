import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MessageThread extends Thread {

	private int id;

	private String username;
	private Socket client;
	private boolean running = true;
	private BufferedReader in;
	private PrintWriter out;
	private OutgoingThread outThread;
	private ArrayList<MessageThread> messageThreads;
	
	private ArrayList<ArrayList<String>> outgoing;
	
	public MessageThread(int id, Socket client, ArrayList<ArrayList<String>> outgoing, OutgoingThread outThread, PrintWriter out, ArrayList<MessageThread> messageThreads){
		this.id = id;
		this.client = client;
		this.outgoing = outgoing;
		this.outThread = outThread;
		this.out = out;
		this.messageThreads = messageThreads;
	}

	public void terminate(){
		running = false;
		this.interrupt();
	}
	
	public void run(){
			try {
				in = new BufferedReader( new InputStreamReader(client.getInputStream()));
			} catch (IOException e) {
				System.out.println("There was a problem with getting the input stream.");
			}
			try {
				username = in.readLine();
			} catch (IOException e) {
				System.out.println("There was a problem with reading from the client.");
			}
			if (!username.equals("null")){
				System.out.println(username + " has connected.");
				out.println("Connected. Welcome, " + username + "! ");
				outgoing.add(id, new ArrayList<String>());
				addMessagetoList("<" + username + " has joined>");
				outThread.start();
			} else {
				running = false;
			}
		while(running){
			try{
				Thread.sleep(100);
				String input = in.readLine();
				if (!input.equals("null") && (!input.equals("//disconnect//")) && (!input.equals(":users")) && (!input.equals(""))){
					System.out.println("<" + username + "> " + input);
					addMessagetoList("<" + username + "> " + input);
					outThread.sendMessage("//ok//");
				} else if (input.equals("//disconnect//")){
					running = false;
					System.out.println(username + " has disconnected");
					addMessagetoList(username + " has disconnected");
				} else if (input.equals(":users")){
					outThread.sendMessage("//ok//");
					printUsers(id);
				}
			} catch (Exception e){
				System.err.print("Damn, something broke! ");
				e.printStackTrace();
				running = false;
			}
		} 
		if (!username.equals("null")){
			outThread.terminate();
			
		}
	}
	
	public void printUsers(int threadID){
		ArrayList<String> array = outgoing.get(threadID);
		int running = 0;
		for (MessageThread thread : messageThreads){
			if (thread.getRunning())
				running++;
		}
		array.add(running + " Users online - ");
		for (MessageThread thread : messageThreads){
			if (thread.getRunning())
				array.add(thread.getUsername());
		}
		array.add("");
	}
	
	public boolean getRunning(){
		return running;
	}
	
	public String getUsername(){
		return username;
	}
	
	public int getID(){
		return id;
	}

	public void addMessagetoList(String message){
		for (int i = 0; i < outgoing.size(); i++){
			if (i != id){
				ArrayList<String> array = outgoing.get(i);
				array.add(message);
			}
			
		}
	}
}
