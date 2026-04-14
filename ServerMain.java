import java.io.*;
import java.net.*;

public class ServerMain {
    public static void main(String[] args) {
        try {
            int port = 1234;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server has started.");

            while (true) { 
                Socket socket = serverSocket.accept();
                System.out.println("Client has connected.");
                ClientHandler clientThread = new ClientHandler(socket);
                clientThread.start(); //plan to handle multiple clients in separate threads even though run() method is empty for now.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        //will handle client comms
    }
}