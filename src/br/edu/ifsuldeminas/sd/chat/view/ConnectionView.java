package br.edu.ifsuldeminas.sd.chat.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ConnectionView extends JFrame {

    private static final Color BG           = new Color(0x11, 0x1B, 0x21);
    private static final Color PANEL_BG     = new Color(0x20, 0x2C, 0x33);
    private static final Color FIELD_BG     = new Color(0x2A, 0x37, 0x42);
    private static final Color FIELD_BORDER = new Color(0x3A, 0x47, 0x52);
    private static final Color TEXT_COLOR   = new Color(0xE9, 0xEA, 0xDF);
    private static final Color LABEL_COLOR  = new Color(0x86, 0x96, 0xA0);
    private static final Color WA_GREEN     = new Color(0x25, 0xD3, 0x66);
    private static final Color WA_TEAL      = new Color(0x12, 0x8C, 0x7E);
    private static final Color WA_DARK      = new Color(0x07, 0x5E, 0x54);

    private JTextField txtLocalPort;
    private JTextField txtRemotePort;
    private JTextField txtRemoteIp;
    private JTextField txtName;
    private JButton btnConnect;
    private JRadioButton udpButton;
    private JRadioButton tcpButton;
    private JRadioButton multicastButton;
    private JPanel localPortPanel;
    private JLabel lblRemoteIp;
    private JLabel lblRemotePort;

    public ConnectionView() {
        setTitle("Chat Multicast");
        setSize(400, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG);
        initializeComponents();
        setVisible(true);
    }

    // Carrega logo de src/images/logo.png; se não encontrar, desenha com Graphics2D
    private JPanel createWALogo(int size) {
        java.net.URL imgUrl = getClass().getClassLoader().getResource("images/logo.png");

        if (imgUrl != null) {
            ImageIcon original = new ImageIcon(imgUrl);
            Image scaled = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled));
            logoLabel.setPreferredSize(new Dimension(size, size));

            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrapper.setBackground(PANEL_BG);
            wrapper.setOpaque(false);
            wrapper.add(logoLabel);
            return wrapper;
        }

        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int m = 2;
                int s = size - m * 2;

                g2.setColor(WA_GREEN);
                g2.fillOval(m, m, s, s);

                g2.setColor(Color.WHITE);
                int bs = (int)(s * 0.80);
                int bx = m + (s - bs) / 2;
                int by = m + (int)(s * 0.06);
                g2.fillOval(bx, by, bs, bs);

                int tipX = m + (int)(s * 0.22);
                int tipY = m + s - (int)(s * 0.04);
                int[] px = { bx + bs/5, tipX, bx + bs/2 };
                int[] py = { by + bs - (int)(bs * 0.12), tipY, by + bs - (int)(bs * 0.08) };
                g2.fillPolygon(px, py, 3);

                g2.setColor(WA_TEAL);
                g2.setStroke(new BasicStroke(s / 8.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = bx + bs / 2;
                int cy = by + (int)(bs * 0.48);
                int r  = (int)(bs * 0.24);
                g2.drawArc(cx - r, cy - r, r * 2, r * 2, 130, 100);
                g2.drawArc(cx - r, cy - r, r * 2, r * 2, 310, 100);
                g2.drawArc(cx - r/2, cy - r/2, r, r, 220, 100);

                g2.dispose();
            }

            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
            @Override public boolean isOpaque() { return false; }
        };
    }

    private JTextField createStyledField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBackground(FIELD_BG);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(LABEL_COLOR);
        label.setFont(font);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createFieldPanel(JLabel label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(13));
        return panel;
    }

    private JRadioButton createProtocolButton(String text, Color selectedColor) {
        JRadioButton btn = new JRadioButton(text);
        btn.setOpaque(true);
        btn.setBackground(FIELD_BG);
        btn.setForeground(LABEL_COLOR);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(7, 0, 7, 0)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.CENTER);

        btn.addChangeListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(selectedColor);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(selectedColor.darker()),
                        BorderFactory.createEmptyBorder(7, 0, 7, 0)));
            } else {
                btn.setBackground(FIELD_BG);
                btn.setForeground(LABEL_COLOR);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER),
                        BorderFactory.createEmptyBorder(7, 0, 7, 0)));
            }
        });
        return btn;
    }

    private void initializeComponents() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG);

        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        Font font = new Font("Arial", Font.PLAIN, 13);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleRow.setBackground(PANEL_BG);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel logo = createWALogo(54);
        titleRow.add(logo);

        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setBackground(PANEL_BG);

        JLabel title = new JLabel("Chat Multicast");
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subtitle = new JLabel("UDP  ·  TCP  ·  Multicast IP");
        subtitle.setForeground(LABEL_COLOR);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 11));

        titleText.add(title);
        titleText.add(Box.createVerticalStrut(3));
        titleText.add(subtitle);
        titleRow.add(titleText);

        panel.add(titleRow);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createLabel("Protocolo:", font));
        panel.add(Box.createVerticalStrut(6));

        udpButton       = createProtocolButton("UDP",       new Color(0x1A, 0x6B, 0x3C));
        tcpButton       = createProtocolButton("TCP",       WA_TEAL);
        multicastButton = createProtocolButton("Multicast", WA_DARK);
        udpButton.setSelected(true);

        ButtonGroup group = new ButtonGroup();
        group.add(udpButton);
        group.add(tcpButton);
        group.add(multicastButton);

        JPanel protocolPanel = new JPanel(new GridLayout(1, 3, 6, 0));
        protocolPanel.setBackground(PANEL_BG);
        protocolPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        protocolPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        protocolPanel.add(udpButton);
        protocolPanel.add(tcpButton);
        protocolPanel.add(multicastButton);
        panel.add(protocolPanel);
        panel.add(Box.createVerticalStrut(16));

        JLabel lblLocalPort = createLabel("Porta Local:", font);
        txtLocalPort = createStyledField(font);
        localPortPanel = createFieldPanel(lblLocalPort, txtLocalPort);
        panel.add(localPortPanel);

        lblRemoteIp = createLabel("IP Remoto:", font);
        txtRemoteIp = createStyledField(font);
        panel.add(createFieldPanel(lblRemoteIp, txtRemoteIp));

        lblRemotePort = createLabel("Porta Remota:", font);
        txtRemotePort = createStyledField(font);
        panel.add(createFieldPanel(lblRemotePort, txtRemotePort));

        JLabel lblName = createLabel("Nickname:", font);
        txtName = createStyledField(font);
        panel.add(createFieldPanel(lblName, txtName));

        btnConnect = new JButton("Entrar no Chat");
        btnConnect.setBackground(WA_GREEN);
        btnConnect.setForeground(new Color(0x11, 0x1B, 0x21));
        btnConnect.setFocusPainted(false);
        btnConnect.setBorderPainted(false);
        btnConnect.setFont(new Font("Arial", Font.BOLD, 14));
        btnConnect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConnect.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConnect.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panel.add(btnConnect);

        outer.add(panel);
        add(outer);

        multicastButton.addActionListener(e -> {
            localPortPanel.setVisible(false);
            lblRemoteIp.setText("Endereço do Grupo:");
            lblRemotePort.setText("Porta do Grupo:");
            txtRemoteIp.setText("228.6.7.8");
            revalidate(); repaint();
        });
        udpButton.addActionListener(e -> {
            localPortPanel.setVisible(true);
            lblRemoteIp.setText("IP Remoto:");
            lblRemotePort.setText("Porta Remota:");
            txtRemoteIp.setText("");
            revalidate(); repaint();
        });
        tcpButton.addActionListener(e -> {
            localPortPanel.setVisible(true);
            lblRemoteIp.setText("IP Remoto:");
            lblRemotePort.setText("Porta Remota:");
            txtRemoteIp.setText("");
            revalidate(); repaint();
        });

        btnConnect.addActionListener(e -> connect());
        txtName.addActionListener(e -> connect());
    }

    private void connect() {
        try {
            String name = txtName.getText().trim();
            String protocol = udpButton.isSelected() ? "UDP" : tcpButton.isSelected() ? "TCP" : "MULTICAST";

            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Digite um nickname."); return; }

            if (multicastButton.isSelected()) {
                String group = txtRemoteIp.getText().trim();
                int port = Integer.parseInt(txtRemotePort.getText().trim());
                new ChatView(protocol, port, group, port, name);
            } else {
                int localPort  = Integer.parseInt(txtLocalPort.getText().trim());
                int remotePort = Integer.parseInt(txtRemotePort.getText().trim());
                String remoteIp = txtRemoteIp.getText().trim();
                new ChatView(protocol, localPort, remoteIp, remotePort, name);
            }
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "A porta deve ser um número válido.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dados inválidos: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConnectionView::new);
    }
}
