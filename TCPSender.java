import java.io.PrintWriter;
import java.net.Socket;

class TCPSender implements Sender {

    private String ip;
    private int port;
    
    public TCPSender(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void send(String message)
            throws ChatException {
        try {
            Socket socket = new Socket(ip, port);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println(message);
            socket.close();
        } catch (Exception e) {
            throw new ChatException(
                    "Erro ao enviar mensagem TCP.", e);
        }
    }
}