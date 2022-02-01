package ClientServerSocketProgramming;
import java.io.*;
import java.util.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {

	public static ArrayList<ClientHandler> clients = new ArrayList<>();
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private String clientName;
	
	public ClientHandler(Socket sock) {
		try {
			this.socket = sock;
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.clientName = reader.readLine();
			clients.add(this);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();  
			broadcastMessage("SERVER: "+ clientName +" has entered the chat on " + formatter.format(now));		
		} catch (IOException e) {
			closeSocketConnection(socket, reader, writer);
		}
	}

	@Override
	public void run() {
		String Msgfromclient;	
		while(socket.isConnected()) {
			try {
				Msgfromclient = reader.readLine();
				if(Msgfromclient.equals("quit"))
				{
					clients.remove(this);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
					LocalDateTime now = LocalDateTime.now();  
					broadcastMessage("SERVER: "+ clientName +" has left the chat at "+ formatter.format(now) );
					socket.close();
				}
				else {
					broadcastMessage(Msgfromclient);	
				}	
			}catch (IOException e) {
				closeSocketConnection(socket, reader, writer);
				break;
			}
		}
	}

	public void broadcastMessage(String message) {	
		for(ClientHandler c : clients) {
			try {
				if(!c.clientName.equals(clientName)) {
					c.writer.write(message);
					c.writer.newLine();
					c.writer.flush();
				}	
			}catch (IOException e) {
				closeSocketConnection(socket, reader, writer);
			}
		}
	}
	
	public void removeClient() {
		// removing that client from the clients array and broadcasting the message that he/she has left
		clients.remove(this);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		broadcastMessage("SERVER: "+ clientName +" has left the chat at "+ formatter.format(now));
	}
	
	public void closeSocketConnection(Socket socket, BufferedReader reader, BufferedWriter writer) {
		removeClient();
		try {
		// Closes Socket and the buffered readers and writers
		socket.close();
		reader.close();
		writer.close();
		} catch (IOException ioexceptions) {
			ioexceptions.printStackTrace();
		}
	}
}
