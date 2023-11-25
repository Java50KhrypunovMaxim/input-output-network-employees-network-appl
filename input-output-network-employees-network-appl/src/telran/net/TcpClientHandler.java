package telran.net;

import java.io.*;
import java.net.*;
public class TcpClientHandler implements Closeable, NetworkHandler {
	 Socket socket;
	 ObjectOutputStream writer;
	 ObjectInputStream reader;
	 private String host;
	  private int port;
	  public TcpClientHandler (String host, int port) throws Exception {
	    	this.host = host;
	    	this.port = port;
	    	connection();
	    }
	 
	 private void connection () throws UnknownHostException, IOException {
		 socket = new Socket(host, port);
		 writer = new ObjectOutputStream(socket.getOutputStream());
		 reader = new ObjectInputStream(socket.getInputStream());
	 }
	@Override
	public void close() throws IOException {
		socket.close();

	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T send(String requestType, Serializable requestData)  {
		Request request = new Request(requestType, requestData);
		boolean running = true;
		
		while (running) {
			running = false;
			try {
				writer.writeObject(request);
				Response response = (Response) reader.readObject();
				if (response.code() != ResponseCode.OK) {
					throw new RuntimeException(response.responseData().toString());
				}
				@SuppressWarnings("unchecked")
				T res = (T) response.responseData();
				return res;
				
			} catch (Exception e) {
				if(e instanceof SocketException) {
					running = true;
					try {
						connection();
					} catch (Exception e1) {
						
					} 
				} else {
					throw new RuntimeException(e.getMessage());
				}
				
			} 
		}
		return null;
	}

}