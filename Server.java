package ClientServerSocketProgramming;
import java.net.*;
import java.io.*;

public class Server {
	
	private ServerSocket serversocket;
    static  int port = 1111;
	public Server(ServerSocket serversocket) {
		this.serversocket= serversocket;
	}
	public void initializeServer() {
		try {
			while(!serversocket.isClosed()) {
				Socket s = serversocket.accept();
				System.out.println("New Connection has been established");
				ClientHandler clients = new ClientHandler(s);		
				Thread thread = new Thread(clients);
				thread.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void closeServerSocket() {
		try {
			if(serversocket != null) {
				serversocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		ServerSocket serversocket = new ServerSocket(port);
		Server server = new Server(serversocket);
		System.out.println("SERVER: Waiting for clients.....");
		server.initializeServer();
	}
}