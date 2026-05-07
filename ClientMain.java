import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientMain {
    private JFrame frame;
    private JTextArea chatSpace;
    private JTextField inputSpace;
    private PrintWriter out;
    public static void main(String[] args) {
        ClientMain clientMain = new ClientMain();
        clientMain.start();
    }
    public void start() {
        frame = new JFrame("ChatterBox");
        chatSpace = new JTextArea(50, 80);
        chatSpace.setEditable(false);
        chatSpace.setLineWrap(true); //for text thats too long; autowrap and make new line
        inputSpace = new JTextField();

        frame.add(new JScrollPane(chatSpace), BorderLayout.CENTER);
        frame.add(inputSpace, BorderLayout.SOUTH);
        frame.pack(); //window started very small without this
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        inputSpace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (out != null && !inputSpace.getText().isEmpty()) { //if out exists & user input isnt empty
                    out.println(inputSpace.getText());
                    inputSpace.setText(""); //clear after sending, to type new message
                }
            }
        });
        try {
            Socket socket = new Socket("localhost", 1234);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); //autoflush
            
            //lambda background thread to listen for server messages from server while main thread continues reading user input
            new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        chatSpace.append(response + "\n");
                        chatSpace.setCaretPosition(chatSpace.getDocument().getLength()); //move cursor to end of text so chat autoscrolls to show newest message
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame, "ERROR: Something went wrong.");
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "ERROR: Unable to connect to server." );
            e.printStackTrace();
        }
    }
}