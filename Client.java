package ClientServerSocketProgramming;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
  private Socket socket;
  private BufferedReader reader;
  private BufferedWriter writer;
  private String username;
  static int port = 1111;
  public Client(Socket s, String user) {
    try {
      this.socket = s;
      this.writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
      this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
      this.username = user;
    } catch (IOException e) {
      closeconnection(socket, reader, writer);
    }
  }
  public void sendMessage() {
    try {
      writer.write(username);
      writer.newLine();
      writer.flush();
      Scanner msgScan = new Scanner(System.in);
      while (socket.isConnected()) {
        String messagetosend = msgScan.nextLine();
        writer.write(username + ": " + messagetosend);
        writer.newLine();
        writer.flush();
      }
    } catch (IOException e) {
      closeconnection(socket, reader, writer);
    }
  }

  public void listenMessages() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        String msgfromGC;
        while (socket.isConnected()) {
          try {
            msgfromGC = reader.readLine();
            System.out.println(msgfromGC);
          } catch (IOException e) {
            closeconnection(socket, reader, writer);
          }
        }
      }
    }).start();
  }

  public void closeconnection(Socket socket, BufferedReader reader, BufferedWriter writer) {
    try {
      socket.close();
      reader.close();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    Scanner nameScanner = new Scanner(System.in);
    System.out.println("Enter the name you want to display in group chat:");
    String userName = nameScanner.nextLine();
    Socket socket = new Socket("localhost", port);
    Client client = new Client(socket, userName);
    client.listenMessages();
    client.sendMessage();
  }
}