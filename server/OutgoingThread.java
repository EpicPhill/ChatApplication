import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class OutgoingThread extends Thread {

	private int id;
	
	private boolean running = true;
	
	private ArrayList<ArrayList<String>> outgoing;
	private PrintWriter out;
	
	public OutgoingThread(int id, ArrayList<ArrayList<String>> outgoing, PrintWriter out){
		this.id = id;
		this.outgoing = outgoing;
		this.out = out;
	}
	
	public void run(){
		while(running){
			try{
				Thread.sleep(100);
				while(outgoing.get(id).size() > 0){
					Iterator<String> it = outgoing.get(id).iterator();
					while(it.hasNext()){
						String message = it.next();
						out.println(message);
						it.remove();
					}
				}
			} catch (Exception e){
				e.printStackTrace();
				running = false;
			}
		}
	}
	
	public void terminate(){
		out.println("//closed//");
		running = false;
	}
	
	public void sendMessage(String message){
		out.println(message);
	}
	
}
