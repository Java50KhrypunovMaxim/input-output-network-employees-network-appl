package telran.net;

import java.io.*;
import java.net.*;

public class TcpClientHandler implements Closeable, NetworkHandler {
	Socket socket;
	ObjectOutputStream writer;
	ObjectInputStream reader;
	String host;
	int port;
	private boolean flConnected = false;

	public TcpClientHandler(String host, int port) throws Exception {
		this.host = host;
		this.port = port;

	}

	private void connect() {
		try {
			socket = new Socket(host, port);
			writer = new ObjectOutputStream(socket.getOutputStream());
			reader = new ObjectInputStream(socket.getInputStream());
			flConnected = true;
		} catch (Exception e) {
			flConnected = false;
			throw new RuntimeException("server is unavailable, please repeat request later on");
		}

	}

	@Override
	public void close() throws IOException {
		socket.close();

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T send(String requestType, Serializable requestData) {
		Request request = new Request(requestType, requestData);
		Response response = null;
		boolean running = false;
		try {
			if (!flConnected) {
				connect();
			}
			do {
				running = false;
				try {
					writer.writeObject(request);

					response = (Response) reader.readObject();
				} catch (SocketException e) {
					connect();
					running = true;
				}
			} while (running);
			if (response.code() != ResponseCode.OK) {
				throw new Exception(response.code() + ": " + response.responseData().toString());
			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return (T) response.responseData();

	}

}
