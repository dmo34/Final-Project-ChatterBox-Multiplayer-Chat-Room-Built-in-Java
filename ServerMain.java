import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain {
    public static List<ClientHandler> clients = new ArrayList<>(); //shared list of clients, not using yet
    public static void main(String[] args) {
        try {
            int port = 1234;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server has started.");

            while (true) { 
                Socket socket = serverSocket.accept();
                System.out.println("Client has connected: " + socket.getInetAddress()); //get ip of conn client
                
                ClientHandler clientThread = new ClientHandler(socket);
                clients.add(clientThread);
                clientThread.start(); //creates new thread for each client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        //will handle client comms
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); //true is for autoflush, makes sure data is sent immediately across network
            out.println("Connected to server.");

            String message;
            //listen for client messages
            while ((message = in.readLine()) != null) { //read input and also check if client still conn
                System.out.println("[SERVER RECEIVED]: " + message);

                broadcast(message); //broadcast it to all connected clients
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress()); //show ip of disconn client, maybe change later since its not safe to show ip
        } finally {
            try {
                ServerMain.clients.remove(this); //remove this client from list when it disconns
                socket.close(); //makes so that socket closes no matter what
            } catch (Exception e) {
                e.printStackTrace(); //print report about exception
            } 
        }
    }
    private void broadcast(String message) { //priv because no other class should be allowed to use it
        for (ClientHandler clientThread : ServerMain.clients) {
            clientThread.out.println(message);
        }
    }
}