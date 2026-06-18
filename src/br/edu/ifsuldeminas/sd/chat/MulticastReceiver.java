package br.edu.ifsuldeminas.sd.chat;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class MulticastReceiver implements Receiver {

	private MulticastSocket socket;
	private InetAddress group;
	private MessageContainer container;
	private static final int BUFFER_SIZE = 1000;

	public MulticastReceiver(String groupAddress, int port, MessageContainer container) throws ChatException {
		try {
			this.group = InetAddress.getByName(groupAddress);
			this.socket = new MulticastSocket(port);
			this.socket.joinGroup(group); // entra no grupo multicast
			this.container = container;
			new Thread(this).start(); // receive() é bloqueante, roda em thread separada
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
				socket.receive(packet);
				String message = new String(packet.getData(), 0, packet.getLength());
				container.newMessage(message);
			} catch (Exception e) {
				break;
			}
		}
	}
}
