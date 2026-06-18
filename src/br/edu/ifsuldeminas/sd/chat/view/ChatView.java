package br.edu.ifsuldeminas.sd.chat.view;

import br.edu.ifsuldeminas.sd.chat.ChatException;
import br.edu.ifsuldeminas.sd.chat.ChatFactory;
import br.edu.ifsuldeminas.sd.chat.MessageContainer;
import br.edu.ifsuldeminas.sd.chat.Sender;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ChatView extends JFrame implements MessageContainer {

    private static final Color CHAT_BG      = new Color(0x11, 0x1B, 0x21);
    private static final Color HEADER_BG    = new Color(0x20, 0x2C, 0x33);
    private static final Color INPUT_BG     = new Color(0x20, 0x2C, 0x33);
    private static final Color OWN_BUBBLE   = new Color(0x00, 0x5C, 0x4B);
    private static final Color OTHER_BUBBLE = new Color(0x20, 0x2C, 0x33);
    private static final Color TEXT_COLOR   = new Color(0xE9, 0xEA, 0xDF);
    private static final Color OWN_NAME     = new Color(0x25, 0xD3, 0x66);
    private static final Color OTHER_NAME   = new Color(0x34, 0xB7, 0xF1);
    private static final Color SYSTEM_COLOR = new Color(0x86, 0x96, 0xA0);
    private static final Color SEND_BTN     = new Color(0x00, 0xA8, 0x84);
    private static final Color DISC_BTN     = new Color(0x8B, 0x00, 0x00);
    private static final Color PATTERN_CLR  = new Color(255, 255, 255, 10);

    private static final int MAX_BUBBLE_WIDTH = 340;

    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private JTextField txtMessage;
    private JButton btnSend;
    private JButton btnDisconnect;
    private Sender sender;
    private String userName;
    private boolean isMulticast;

    public ChatView(String protocol, int localPort, String remoteIp, int remotePort, String userName) {
        this.userName = userName;
        this.isMulticast = protocol.equalsIgnoreCase("MULTICAST");

        setTitle((isMulticast ? "Chat Multicast" : "UDP & TCP Chat") + " — " + userName);
        setSize(720, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        configureLayout();
        configureEvents();
        connect(protocol, localPort, remoteIp, remotePort);
        setVisible(true);
    }

    private void initializeComponents() {
        messagesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                drawPattern(g, getWidth(), getHeight());
            }
        };
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(true);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        Font inputFont = new Font("Arial", Font.PLAIN, 14);
        txtMessage = new JTextField();
        txtMessage.setFont(inputFont);
        txtMessage.setBackground(new Color(0x2A, 0x37, 0x42));
        txtMessage.setForeground(TEXT_COLOR);
        txtMessage.setCaretColor(TEXT_COLOR);
        txtMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3A, 0x47, 0x52)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        btnSend = new JButton("Enviar");
        styleButton(btnSend, SEND_BTN);

        btnDisconnect = new JButton("Sair");
        styleButton(btnDisconnect, DISC_BTN);
    }

    // Fundo com doodles sutis (estilo wallpaper do WhatsApp)
    private void drawPattern(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(CHAT_BG);
        g2.fillRect(0, 0, w, h);
        g2.setColor(PATTERN_CLR);
        g2.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int tile = 70;
        for (int col = 0; col * tile < w + tile; col++) {
            for (int row = 0; row * tile < h + tile; row++) {
                int ox = col * tile + (row % 2 == 0 ? 0 : 35);
                int oy = row * tile;
                drawPatternIcon(g2, ox + 10, oy + 10, (col * 3 + row * 2) % 7);
            }
        }
        g2.dispose();
    }

    private void drawPatternIcon(Graphics2D g, int x, int y, int v) {
        switch (v) {
            case 0: g.drawOval(x, y, 20, 15); g.drawLine(x+5, y+15, x+2, y+20); break;
            case 1:
                for (int i = 0; i < 5; i++) {
                    double a = Math.toRadians(i*72-90), a2 = Math.toRadians(i*72+36-90);
                    g.drawLine(x+10+(int)(8*Math.cos(a)), y+10+(int)(8*Math.sin(a)),
                               x+10+(int)(4*Math.cos(a2)), y+10+(int)(4*Math.sin(a2)));
                }
                break;
            case 2: g.drawArc(x+2,y,16,20,30,120); g.fillOval(x+2,y,5,5); g.fillOval(x+13,y+15,5,5); break;
            case 3: g.drawArc(x+2,y+2,8,8,0,180); g.drawArc(x+10,y+2,8,8,0,180);
                    g.drawLine(x+2,y+6,x+10,y+18); g.drawLine(x+18,y+6,x+10,y+18); break;
            case 4: g.drawLine(x+8,y+4,x+8,y+16); g.fillOval(x+4,y+13,7,6); g.drawLine(x+8,y+4,x+15,y+2); break;
            case 5: g.drawOval(x+2,y+2,16,16); g.fillOval(x+5,y+7,3,3); g.fillOval(x+12,y+7,3,3);
                    g.drawArc(x+5,y+10,10,6,0,-180); break;
            case 6: g.drawRoundRect(x+1,y+5,18,13,3,3); g.drawOval(x+6,y+7,8,8); g.fillOval(x+14,y+6,3,3); break;
        }
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 36));
    }

    private void configureLayout() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel headerTitle = new JLabel("● " + (isMulticast ? "Chat Multicast" : "Chat") + "  —  " + userName);
        headerTitle.setForeground(TEXT_COLOR);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 15));
        header.add(headerTitle, BorderLayout.WEST);

        JPanel inputArea = new JPanel(new BorderLayout(8, 0));
        inputArea.setBackground(INPUT_BG);
        inputArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        inputArea.add(txtMessage, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 6, 0));
        btnPanel.setBackground(INPUT_BG);
        btnPanel.add(btnDisconnect);
        btnPanel.add(btnSend);
        inputArea.add(btnPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(inputArea, BorderLayout.SOUTH);
    }

    private void configureEvents() {
        btnSend.addActionListener(e -> sendMessage());
        txtMessage.addActionListener(e -> sendMessage());
        btnDisconnect.addActionListener(e -> { dispose(); new ConnectionView(); });
    }

    private void connect(String protocol, int localPort, String remoteIp, int remotePort) {
        try {
            sender = ChatFactory.build(protocol, remoteIp, remotePort, localPort, this);
            addSystemMessage("Conectado como " + userName);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + e.getMessage());
        }
    }

    private void sendMessage() {
        if (sender == null) { JOptionPane.showMessageDialog(this, "Erro na conexão."); return; }
        String message = txtMessage.getText().trim();
        if (message.isEmpty()) return;

        // Multicast usa "[nickname] texto"; UDP/TCP usa "texto::de::nickname"
        String formatted = isMulticast
                ? "[" + userName + "] " + message
                : message + MessageContainer.FROM + userName;

        try {
            sender.send(formatted);
            addBubble(userName + " (Eu)", message, true);
            txtMessage.setText("");
        } catch (ChatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao enviar mensagem.");
        }
    }

    @Override
    public void newMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (isMulticast) {
                if (message.startsWith("[") && message.contains("] ")) {
                    int idx = message.indexOf("] ");
                    String from = message.substring(1, idx);
                    String text = message.substring(idx + 2);
                    if (!from.equals(userName)) addBubble(from, text, false);
                } else {
                    addSystemMessage(message);
                }
            } else if (message.contains(MessageContainer.FROM)) {
                String[] parts = message.split(MessageContainer.FROM);
                addBubble(parts[1], parts[0], false);
            } else {
                addSystemMessage(message);
            }
        });
    }

    private void addBubble(String senderName, String text, boolean isOwn) {
        Font nameFont = new Font("Arial", Font.BOLD, 11);
        Font msgFont  = new Font("Arial", Font.PLAIN, 13);

        JLabel probe = new JLabel(text);
        probe.setFont(msgFont);
        int naturalWidth = probe.getPreferredSize().width;

        // HTML só quando o texto precisar de quebra de linha
        JLabel msgLabel;
        if (naturalWidth <= MAX_BUBBLE_WIDTH) {
            msgLabel = new JLabel(text);
        } else {
            msgLabel = new JLabel("<html><body style='width:" + MAX_BUBBLE_WIDTH
                    + "px'>" + escapeHtml(text) + "</body></html>");
        }
        msgLabel.setFont(msgFont);
        msgLabel.setForeground(TEXT_COLOR);
        msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(senderName);
        nameLabel.setFont(nameFont);
        nameLabel.setForeground(isOwn ? OWN_NAME : OTHER_NAME);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color bubbleColor = isOwn ? OWN_BUBBLE : OTHER_BUBBLE;
        JPanel bubble = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bubbleColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(BorderFactory.createEmptyBorder(6, 12, 8, 12));
        bubble.setMaximumSize(new Dimension(
                Math.min(Math.max(nameLabel.getPreferredSize().width, naturalWidth) + 26, MAX_BUBBLE_WIDTH + 26),
                Integer.MAX_VALUE));

        bubble.add(nameLabel);
        bubble.add(Box.createVerticalStrut(3));
        bubble.add(msgLabel);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        row.add(bubble, isOwn ? BorderLayout.EAST : BorderLayout.WEST);

        messagesPanel.add(row);
        messagesPanel.add(Box.createVerticalStrut(4));
        messagesPanel.revalidate();
        messagesPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private void addSystemMessage(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 11));
        label.setForeground(SYSTEM_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setOpaque(true);
        label.setBackground(new Color(0x20, 0x2C, 0x33, 200));
        label.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.add(label);

        messagesPanel.add(row);
        messagesPanel.add(Box.createVerticalStrut(4));
        messagesPanel.revalidate();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
