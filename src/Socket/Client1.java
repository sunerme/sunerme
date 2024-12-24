package Socket;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Client1 {

    private JFrame frmClient;
    private JTextField textField;
    private static JTextArea textArea;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Client1 window = new Client1();
                window.frmClient.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public Client1() {
        connectToServer();
        initialize();
    }

    /**
     * Connect to the server.
     */
    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Prompt for username
            String username = JOptionPane.showInputDialog("Enter your username:");
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username cannot be empty. Exiting.");
                System.exit(0);
            }
            out.println(username); // Send username to server

            // Create a thread to listen for messages from the server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        appendMessage(serverMessage);
                    }
                } catch (IOException e) {
                    appendMessage("Connection to server lost.");
                }
            }).start();

        } catch (IOException e) {
            appendMessage("Unable to connect to the server.");
        }
    }

    /*
      append a message to the text area.
    */
    private static void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmClient = new JFrame();
        frmClient.setTitle("Client1");
        frmClient.getContentPane().setBackground(Color.BLACK);
        frmClient.getContentPane().setLayout(null);

        textField = new JTextField();
        textField.setForeground(Color.WHITE);
        textField.setBackground(Color.DARK_GRAY);
        textField.setBounds(10, 607, 370, 28);
        frmClient.getContentPane().add(textField);
        textField.setColumns(10);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = textField.getText().trim();
                    if (!message.isEmpty()) {
                        out.println(message); // Send message to the server
                        appendMessage("You: " + message); // Append own message to chat area
                        textField.setText(""); // Clear input field
                    }
                }
            }
        });

        textArea = new JTextArea();
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(new Color(0, 0, 0));
        textArea.setEditable(false);
        textArea.setBounds(10, 10, 370, 580);
        frmClient.getContentPane().add(textArea);

        frmClient.setBounds(100, 100, 400, 680);
        frmClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
