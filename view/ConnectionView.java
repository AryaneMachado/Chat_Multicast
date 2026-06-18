import javax.swing.*;
import java.awt.*;

public class ConnectionView extends JFrame {

    private JTextField txtLocalPort;
    private JTextField txtRemotePort;
    private JTextField txtRemoteIp;
    private JTextField txtName;
    private JButton btnConnect;
    private JRadioButton udpButton;
    private JRadioButton tcpButton;
    private JRadioButton multicastButton;

    // Painéis das linhas que podem ser ocultados no modo Multicast
    private JPanel localPortPanel;
    private JPanel remoteIpPanel;
    private JPanel remotePortPanel;

    // Labels que mudam de texto no modo Multicast
    private JLabel lblRemoteIp;
    private JLabel lblRemotePort;

    public ConnectionView() {
        setTitle("Conectar ao Chat");
        setSize(420, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 30, 30));
        initializeComponents();
        setVisible(true);
    }

    private JPanel createFieldPanel(JLabel label, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(24, 24, 24));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JTextField createStyledField(Font font) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setBackground(new Color(40, 40, 40));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(font);
        return label;
    }

    private void initializeComponents() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(24, 24, 24));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 80));

        Font font = new Font("Arial", Font.PLAIN, 14);

        JLabel title = new JLabel("UDP / TCP / MULTICAST CHAT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title);

        panel.add(Box.createVerticalStrut(10));

        // Botões de protocolo
        JLabel lblProtocol = new JLabel("Protocolo:");
        lblProtocol.setForeground(Color.WHITE);

        udpButton = new JRadioButton("UDP");
        tcpButton = new JRadioButton("TCP");
        multicastButton = new JRadioButton("Multicast");

        udpButton.setSelected(true);

        for (JRadioButton btn : new JRadioButton[]{udpButton, tcpButton, multicastButton}) {
            btn.setBackground(new Color(24, 24, 24));
            btn.setForeground(Color.WHITE);
            btn.setFont(font);
        }

        ButtonGroup group = new ButtonGroup();
        group.add(udpButton);
        group.add(tcpButton);
        group.add(multicastButton);

        JPanel protocolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        protocolPanel.setBackground(new Color(24, 24, 24));
        protocolPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        protocolPanel.add(lblProtocol);
        protocolPanel.add(udpButton);
        protocolPanel.add(tcpButton);
        protocolPanel.add(multicastButton);
        protocolPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(protocolPanel);

        // Campo: Porta Local
        JLabel lblLocalPort = createStyledLabel("Porta Local:", font);
        txtLocalPort = createStyledField(font);
        localPortPanel = createFieldPanel(lblLocalPort, txtLocalPort);
        panel.add(localPortPanel);

        // Campo: IP Remoto / Endereço do Grupo
        lblRemoteIp = createStyledLabel("IP Remoto:", font);
        txtRemoteIp = createStyledField(font);
        remoteIpPanel = createFieldPanel(lblRemoteIp, txtRemoteIp);
        panel.add(remoteIpPanel);

        // Campo: Porta Remota / Porta do Grupo
        lblRemotePort = createStyledLabel("Porta Remota:", font);
        txtRemotePort = createStyledField(font);
        remotePortPanel = createFieldPanel(lblRemotePort, txtRemotePort);
        panel.add(remotePortPanel);

        // Campo: Nome / Nickname
        JLabel lblName = createStyledLabel("Nome (Nickname):", font);
        txtName = createStyledField(font);
        panel.add(createFieldPanel(lblName, txtName));

        panel.add(Box.createVerticalStrut(15));

        btnConnect = new JButton("Conectar");
        styleButton(btnConnect);
        btnConnect.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConnect.setMaximumSize(new Dimension(140, 40));
        panel.add(btnConnect);

        add(panel);

        // Quando Multicast é selecionado, ajusta os campos da tela
        multicastButton.addActionListener(e -> {
            localPortPanel.setVisible(false);
            lblRemoteIp.setText("Endereço do Grupo:");
            lblRemotePort.setText("Porta do Grupo:");
            txtRemoteIp.setText("228.6.7.8");
            revalidate();
            repaint();
        });

        // Quando UDP ou TCP é selecionado, restaura os campos normais
        udpButton.addActionListener(e -> {
            localPortPanel.setVisible(true);
            lblRemoteIp.setText("IP Remoto:");
            lblRemotePort.setText("Porta Remota:");
            txtRemoteIp.setText("");
            revalidate();
            repaint();
        });

        tcpButton.addActionListener(e -> {
            localPortPanel.setVisible(true);
            lblRemoteIp.setText("IP Remoto:");
            lblRemotePort.setText("Porta Remota:");
            txtRemoteIp.setText("");
            revalidate();
            repaint();
        });

        btnConnect.addActionListener(e -> connect());
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void connect() {
        try {
            String name = txtName.getText().trim();
            String protocol = udpButton.isSelected() ? "UDP" : tcpButton.isSelected() ? "TCP" : "MULTICAST";

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite um nome (nickname).");
                return;
            }

            if (multicastButton.isSelected()) {
                // No modo Multicast só precisamos do endereço do grupo e da porta
                String groupAddress = txtRemoteIp.getText().trim();
                int port = Integer.parseInt(txtRemotePort.getText().trim());

                new ChatView(protocol, port, groupAddress, port, name);
            } else {
                int localPort = Integer.parseInt(txtLocalPort.getText().trim());
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
