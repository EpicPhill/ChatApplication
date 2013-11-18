import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
	
	private Socket socket;
	private String username;
	private String hostname = "localhost";
	private int port = 1117;
	private ReplyThread message;
	private MessageThread incoming;
	private boolean ok = false;
	
	public static void main(String[] args){
		Client client = new Client();
		if (args.length != 2){
			System.err.println("Please supply a hostname and a port number");
		} else if (args[1].matches("[0-9]*")){
			client.hostname = args[0];
			client.port = Integer.parseInt(args[1]);
			client.init();
		} else {
			System.err.println("The port must be a number");
		}
	}
	
	public void init(){
		try {
			socket = new Socket(hostname, port);
			addShutdownHook();
			runClient();
		} catch (ConnectException e){
			System.err.println("No server found running on that address or port. ");
		} catch (IOException e){ 
			System.err.println("There was a problem with the socket's streams.");
		} catch (Exception e){
			System.err.println(e);
		}
	}
	
	public void runClient() throws Exception{
		System.out.println("Client running on " + hostname + ":" + port);
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
		
		System.out.print("Enter your name: ");
		username = stdIn.readLine();
		System.out.println();
		while(username.equals("")){
			System.out.println("Name must not be blank.");
			System.out.print("Enter your name: ");
			username = stdIn.readLine();
		}
		out.println(username);
		System.out.println(in.readLine());
		System.out.println();
		
		message = new ReplyThread(out, this);
		incoming = new MessageThread(in, this);
		message.start();
		incoming.start();
	}
	
	public void addShutdownHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run(){
				System.out.println("Shutting down system");
				try {
					message.terminate();
					message = null;
					incoming.terminate();
					incoming = null;
					socket.close();
					System.out.println("Socket closed.");
				} catch (IOException e) {
					System.err.println("Closing the socket failed");
				} catch (Exception e){
					System.err.println("Halting the Threads failed - " + e);
				}
			}
		});
	}
	
	public boolean getOk(){
		return ok;
	}
	
	public void setOK(boolean ok){
		this.ok = ok;
	}
}
