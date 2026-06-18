package br.edu.ifsuldeminas.sd.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class TCPReceiver implements Receiver {

	private ServerSocket serverSocket;
	private MessageContainer container;

	public TCPReceiver(int port, MessageContainer container) throws ChatException {
		this.container = container;
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
			throw new ChatException("Erro ao iniciar receiver TCP.", e);
		}
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				String message = reader.readLine();
				if (message != null) {
					container.newMessage(message);
				}
				socket.close();
			} catch (Exception e) {
				container.newMessage("Erro no receiver TCP.");
			}
		}
	}
}
