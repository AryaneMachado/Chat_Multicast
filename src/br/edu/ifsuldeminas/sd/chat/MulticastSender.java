package br.edu.ifsuldeminas.sd.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class MulticastSender implements Sender {

	private DatagramSocket socket;
	private InetAddress groupAddress;
	private int port;

	public MulticastSender(String groupAddress, int port) throws ChatException {
		try {
			this.groupAddress = InetAddress.getByName(groupAddress);
			this.port = port;
			this.socket = new DatagramSocket();
		} catch (Exception e) {
			throw new ChatException("Erro ao criar o socket para envio multicast.", e);
		}
	}

	@Override
	public void send(String message) throws ChatException {
		byte[] bytes = message.getBytes();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, groupAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			throw new ChatException("Erro ao enviar mensagem multicast.", e);
		}
	}
}
