package com.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ChatbotApp extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton; // Declare the sendButton at the class level

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/chatbotdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ranaji123";

    public ChatbotApp() {
        setTitle("Chatbot App");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        JButton stylingButton = new JButton("Styling");
        stylingButton.addActionListener(e -> openStylingDialog());
        add(stylingButton, BorderLayout.NORTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void sendMessage() {
        String userMessage = messageField.getText();

        if (!userMessage.isEmpty()) {
            appendToChatArea("You: " + userMessage);
            displayMessageRecordForSender(userMessage);
            messageField.setText("");
        }
    }

    private void appendToChatArea(String message) {
        chatArea.append(message + "\n");
    }

    private void displayMessageRecordForSender(String sender) {
        try (Connection connection = getConnection()) {
            String query = "SELECT message FROM messages WHERE sender = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, sender);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    boolean found = false;
                    while (resultSet.next()) {
                        String message = resultSet.getString("message");
                        appendToChatArea("Chatbot: " + message);
                        found = true;
                        break;
                    }
                    if (!found) {
                        appendToChatArea("Chatbot: I don't know what you are saying.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = JDBC_URL + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
        return DriverManager.getConnection(url, DB_USER, DB_PASSWORD);
    }

    private void openStylingDialog() {
        Color chatAreaColor = JColorChooser.showDialog(this, "Choose Chat Area Color", chatArea.getBackground());
        Color messageFieldColor = JColorChooser.showDialog(this, "Choose Message Field Color", messageField.getBackground());
        Color sendButtonColor = JColorChooser.showDialog(this, "Choose Send Button Color", sendButton.getBackground());

        if (chatAreaColor != null) {
            chatArea.setBackground(chatAreaColor);
        }

        if (messageFieldColor != null) {
            messageField.setBackground(messageFieldColor);
        }

        if (sendButtonColor != null) {
            sendButton.setBackground(sendButtonColor);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            new ChatbotApp();
        });
    }
}
