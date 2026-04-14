import java.io.*;
import java.net.*;

public class ClientMain {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            //lambda thread to listen for server messages
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start(); //creates thread to listen for messages from server while main thread handles user input. allows one client to multitask

            //sent user input to server
            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput);
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}