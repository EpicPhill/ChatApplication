import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class ConnectionThread extends Thread {

	private ServerSocket socket;
	private ArrayList<MessageThread> messageThreads;
	private ArrayList<OutgoingThread> outgoingThreads;
	private ArrayList<ArrayList<String>> outgoing;
	private PrintWriter out;
	
	private boolean running = true;
	
	public ConnectionThread(ArrayList<MessageThread> messageThreads, ArrayList<OutgoingThread> outgoingThreads, ServerSocket socket, ArrayList<ArrayList<String>> outgoing){
		this.socket = socket;
		this.messageThreads = messageThreads;
		this.outgoingThreads = outgoingThreads;
		this.outgoing = outgoing;
	}
	
	public void run(){
		Socket client = null;
		while (running){
			try {
				client = socket.accept();
			} catch (Exception e){
				System.out.println("Socket listening was interrupted");
			}
				int id = 0;
			try {
				out = new PrintWriter(client.getOutputStream(),true);
			} catch (Exception e){
				System.err.println("output stream could not be opened");
			}
			
			//remove all the terminated threads and add the new ones to the first empty spot
			for (int i = messageThreads.size()-1;  i >= 0; i--){
				if (messageThreads.get(i).getRunning() == false){
					messageThreads.remove(i);
					outgoingThreads.remove(i);
					outgoing.get(i).clear();
					id = i;
				} else id = messageThreads.size();
			}
			OutgoingThread outThread = new OutgoingThread(id, outgoing, out);
			MessageThread newThread = new MessageThread(id, client, outgoing, outThread, out, messageThreads);
			outgoingThreads.add(id, outThread);
			messageThreads.add(id, newThread);
			newThread.start();
			
		} 
		try {
			client.close();
		} catch (Exception e) {
			System.err.println("Closing the port failed");
		}
	}
	
	public void terminate(){
		running = false;
		this.interrupt();
	}
}
