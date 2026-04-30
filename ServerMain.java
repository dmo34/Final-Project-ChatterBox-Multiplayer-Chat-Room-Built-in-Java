import java.io.*;
import java.net.*;
import java.util.*;

public class ServerMain {
    public static Map<String, ClientHandler> clients = new java.util.concurrent.ConcurrentHashMap<>(); //shared map of clients, prevent crash if a user joins or leaves while broadcast forloop running
    public static void main(String[] args) {
        try {
            int port = 1234;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server has started.");

            while (true) { 
                Socket socket = serverSocket.accept();
                System.out.println("Client has connected: " + socket.getInetAddress()); //get ip of conn client
                
                ClientHandler clientThread = new ClientHandler(socket); //wrap socket in handler
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
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        //handle client comms
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); //autoflush, forces data to be sent right away across network
            
            while (true) { 
                out.println("Enter username: ");
                String input = in.readLine();
                if (input == null) { //if client closes window during this
                    return;
                }
                input = input.trim();
                if (input.isEmpty()) {
                    out.println("[SERVER]: Enter a valid username.");
                } else if (ServerMain.clients.containsKey(input.toLowerCase())) {
                    out.println("[SERVER]: Username is not available.");
                } else {
                    username = input; //if not empty and not taken, user gets username
                    ServerMain.clients.put(username.toLowerCase(), this); //key = lowercase name, value = this thread
                    break; //exit loop to start chatting
                } 
            }
            out.println("Welcome, " + username + "!");
            System.out.println(username + " connected from: " + socket.getInetAddress());
            broadcast("[SERVER]: " + username + " has joined the chat.");
            broadcastUserList();

            String message;
            //listen for client messages
            while ((message = in.readLine()) != null) { //read input and also check if client still conn
                System.out.println("[SERVER RECEIVED]: " + message);
                if (message.equalsIgnoreCase("/users")) { //use command to see updated list
                    getUserList();
                } else {
                    broadcast(username + ": " + message); //broadcast it to all connected clients
                }                
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress()); //show ip of disconn client, maybe change later since its not safe to show ip
        } finally {
            try {
                if (username != null) {
                    ServerMain.clients.remove(username.toLowerCase()); //remove this client from list when it disconns
                    broadcast("[SERVER]: " + username + " has left the chat.");
                    broadcastUserList();
                    socket.close(); //socket closes no matter what
                }
            } catch (Exception e) {
                e.printStackTrace(); //print report about exception
            } 
        }
    }
    private void broadcast(String message) {
        for (ClientHandler clientThread : ServerMain.clients.values()) { //get values
            clientThread.out.println(message);
        }
    }
    private String userListString() {
        StringBuilder userList = new StringBuilder(); //modify same string, dynamically build string of active users
        for (ClientHandler clientThread : ServerMain.clients.values()) {
            if (clientThread.username != null) {
                userList.append(clientThread.username).append(", ");
            }
        }
        return userList.toString();
    }
    private void getUserList() {
        out.println("USERS ONLINE: " + userListString());
    }
    private void broadcastUserList() {
        String currentList = userListString();
        broadcast("[SERVER LIVE LIST]: " + currentList);
    }
}