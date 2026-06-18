import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;


public class ChatView extends JFrame implements MessageContainer {

    private JTextPane txtMessages;
    private JTextField txtMessage;
    private JButton btnSend;
    private JButton btnDisconnect;
    private Sender sender;
    private String userName;
    private boolean isMulticast;

    public ChatView(
    		String protocol,
            int localPort,
            String remoteIp,
            int remotePort,
            String userName
    ) {

        this.userName = userName;
        this.isMulticast = protocol.equalsIgnoreCase("MULTICAST");

        setTitle(
                (isMulticast ? "Multicast Chat" : "UDP & TCP Chat") + " - " + userName
        );

        setSize(700,450);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(
                new Color(30,30,30)
        );

        initializeComponents();

        configureLayout();

        configureEvents();

        connect(protocol, localPort, remotePort);

        setVisible(true);
    }

    private void initializeComponents() {

        Font font =
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                );

        txtMessages = new JTextPane();
        txtMessages.setEditable(false);
        txtMessages.setBackground(
                new Color(40,40,40)
        );
        txtMessages.setForeground(Color.WHITE);
        txtMessages.setFont(font);
        txtMessage = new JTextField();
        txtMessage.setFont(font);
        txtMessage.setBackground(
                new Color(50,50,50)
        );
        txtMessage.setForeground(Color.WHITE);
        txtMessage.setCaretColor(Color.WHITE);

        btnSend = new JButton("Enviar");
        styleButton(btnSend);
        
        btnDisconnect = new JButton("Desconectar");
        styleDangerButton(btnDisconnect);
    }

    private void styleDangerButton(JButton button) {

        button.setBackground(
                new Color(200,60,60)
        );

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
    } 
    
    private void styleButton(JButton button) {

        button.setBackground(
                new Color(70,130,180)
        );

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );
    }

    private void configureLayout() {

        JScrollPane scrollPane =
                new JScrollPane(txtMessages);

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout()
                );
        

        bottomPanel.setBackground(
                new Color(30,30,30)
        );

        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,10,10,10
                )
        );

        bottomPanel.add(
                txtMessage,
                BorderLayout.CENTER
        );

        bottomPanel.add(
                btnSend,
                BorderLayout.EAST
        );
        
        bottomPanel.add(btnSend, BorderLayout.EAST);
        
        JPanel buttonsPanel = new JPanel(
                new GridLayout(1,2,5,0)
        );

        buttonsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0,10,0,0
                )
        );

        buttonsPanel.setBackground(
                new Color(30,30,30)
        );

        buttonsPanel.add(btnDisconnect);

        buttonsPanel.add(btnSend);

        bottomPanel.add(
                buttonsPanel,
                BorderLayout.EAST
        );

        setLayout(new BorderLayout());

        add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void configureEvents() {

        btnSend.addActionListener(
                e -> sendMessage()
        );

        txtMessage.addActionListener(
                e -> sendMessage()
        );
        btnDisconnect.addActionListener(
                e -> disconnect()
        );
    }

    private void connect(
            String protocol,
    		int localPort,
            int remotePort
    ) {

        try {

            sender = ChatFactory.build(
                    protocol,
            		"localhost",
                    remotePort,
                    localPort,
                    this
            );

            appendColoredMessage(
                    "Sistema",
                    "Conectado ao chat.",
                    Color.ORANGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao conectar."
            );
        }
    }
    
    private void disconnect() {

        dispose();

        new ConnectionView();
    }

    private void sendMessage() {

        if (sender == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro na conexão."
            );

            return;
        }

        String message =
                txtMessage.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        // Formato da mensagem depende do protocolo
        // Multicast: "[nickname] texto" (conforme atividade)
        // UDP/TCP: "texto::de::nickname" (formato original)
        String formattedMessage;
        if (isMulticast) {
            formattedMessage = "[" + userName + "] " + message;
        } else {
            formattedMessage = String.format("%s%s%s", message, MessageContainer.FROM, userName);
        }

        try {

            sender.send(formattedMessage);

            appendColoredMessage(
                    "EU",
                    message,
                    Color.GREEN
            );

            txtMessage.setText("");

        } catch (ChatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao enviar mensagem."
            );
        }
    }

    @Override
    public void newMessage(String message) {

        SwingUtilities.invokeLater(() -> {

            if (isMulticast) {
                // Mensagens multicast chegam no formato "[nickname] texto"
                // Exemplo: "[Alice] Oi pessoal!"
                if (message.startsWith("[") && message.contains("] ")) {
                    int fechaBracket = message.indexOf("] ");
                    String from = message.substring(1, fechaBracket);
                    String text = message.substring(fechaBracket + 2);
                    appendColoredMessage(from, text, Color.CYAN);
                } else {
                    appendColoredMessage("Sistema", message, Color.ORANGE);
                }

            } else if (message.contains(MessageContainer.FROM)) {
                // Mensagens UDP/TCP chegam no formato "texto::de::nickname"
                String[] parts = message.split(MessageContainer.FROM);
                String text = parts[0];
                String from = parts[1];
                appendColoredMessage(from, text, Color.CYAN);

            } else {
                appendColoredMessage("Sistema", message, Color.ORANGE);
            }
        });
    }

    private void appendColoredMessage(
            String senderName,
            String message,
            Color senderColor
    ) {

        StyledDocument doc =
                txtMessages.getStyledDocument();

        Style style =
                txtMessages.addStyle(
                        "Style",
                        null
                );

        try {

            StyleConstants.setForeground(
                    style,
                    senderColor
            );

            StyleConstants.setBold(
                    style,
                    true
            );

            doc.insertString(
                    doc.getLength(),
                    senderName + ": ",
                    style
            );

            StyleConstants.setForeground(
                    style,
                    Color.WHITE
            );

            StyleConstants.setBold(
                    style,
                    false
            );

            doc.insertString(
                    doc.getLength(),
                    message + "\n",
                    style
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}