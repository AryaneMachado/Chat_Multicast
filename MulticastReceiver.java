import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class MulticastReceiver implements Runnable {

    private MulticastSocket socket;
    private InetAddress group;
    private MessageContainer container;
    private static final int BUFFER_SIZE = 1000;

    public MulticastReceiver(String groupAddress, int port, MessageContainer container) throws ChatException {
        try {
            this.group = InetAddress.getByName(groupAddress);
            this.socket = new MulticastSocket(port);
            // Entra no grupo multicast para começar a receber mensagens
            this.socket.joinGroup(group);
            this.container = container;
            // Inicia a Thread de recebimento para não bloquear a interface
            new Thread(this).start();
        } catch (Exception e) {
            throw new ChatException("Erro ao entrar no grupo multicast.", e);
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                // receive() é bloqueante, por isso roda em Thread separada
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                container.newMessage(message);
            } catch (Exception e) {
                break;
            }
        }
    }
}
