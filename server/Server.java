import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class Server{

	private ServerSocket socket;
	private ConnectionThread connectionThread;
	private int port;
	
	private ArrayList<MessageThread> messageThreads = new ArrayList<MessageThread>();
	private ArrayList<OutgoingThread> outgoingThreads = new ArrayList<OutgoingThread>();
	private ArrayList<ArrayList<String>> outgoing = new ArrayList<ArrayList<String>>();
	
	
	public static void main(String[] args){
		if (args.length != 1){
			System.err.println("Please supply a port number");
		} else if (args[0].matches("[0-9]*")){
			Server server = new Server();
			server.addShutdownHook();
			server.port = Integer.parseInt(args[0]);
			try {
				server.runServer();
			} catch (Exception e){
				e.printStackTrace();
			}
		} else {
			System.err.println("The port must be a number");
		}
	}
	
	public void printUsers(int threadID){
		ArrayList<String> array = outgoing.get(threadID);
		System.out.println(messageThreads.size() + " users online - ");
		for (MessageThread thread : messageThreads){
			System.out.println(thread.getID() + " " + thread.getUsername());
			array.add(thread.getID() + " " + thread.getUsername());
		}
	}
	
	public void runServer() throws Exception{
		try {
			socket = new ServerSocket(port);
			System.out.println("Server running on port " + port);
			connectionThread = new ConnectionThread(messageThreads, outgoingThreads, socket, outgoing);
			connectionThread.start();
		} catch (IOException e) {
			System.err.println("There was a problem opening the server socket");
		}
	}
	
	public void addShutdownHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				System.out.println("Shutting down system");
				if (messageThreads.size() > 0){
					try {
						for (MessageThread m : messageThreads){
							m.terminate();
							System.out.println("Closed thread of user " + m.getUsername());
						}
						for (OutgoingThread o : outgoingThreads){
							o.terminate();
						}
						connectionThread.terminate();
						socket.close();
						System.out.println("Socket closed.");
					} catch (Exception e) {
						System.out.println("Closing the program failed");
						e.printStackTrace();
					}
				}
			}
		});
	}
	
}
