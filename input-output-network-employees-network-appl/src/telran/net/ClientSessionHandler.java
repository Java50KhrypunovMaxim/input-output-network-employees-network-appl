package telran.net;
import java.net.*;
import java.io.*;
public class ClientSessionHandler implements Runnable {
 Socket socket;
 ObjectInputStream reader;
 ObjectOutputStream writer;
 ApplProtocol protocol;
 TcpServer tcpServer;
 final static int TIMEOUT=60000;
 int time = 0;
 public ClientSessionHandler(Socket socket, ApplProtocol protocol, TcpServer tcpServer) throws Exception {
	 this.socket = socket;
	 this.protocol = protocol;
	 this.tcpServer = tcpServer;
	 reader = new ObjectInputStream(socket.getInputStream());
	 writer = new ObjectOutputStream(socket.getOutputStream());
 }
	@Override
	public void run() {
			while(!tcpServer.executor.isShutdown()) {
				try {
					Request request = (Request) reader.readObject();
					Response response = protocol.getResponse(request);
					writer.writeObject(response);
					writer.reset();
					
				} catch(SocketTimeoutException e) {
					time = time + TcpServer.IDLE_TIMEOUT;
					if (time > TIMEOUT && 
						tcpServer.clientsCounter.get() > tcpServer.nThreads) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						System.out.println("socket closed - idle time exeeds total timeout");
						break;
					}
					if(tcpServer.shutdown()) {
						try {
							socket.close();
							
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						System.out.println("socket closed - server has been shutdown");
						break;
					}
					}
		
				catch (EOFException e) {
					System.out.println("Client closed connection");
				} 
				catch (Exception e) {
					System.out.println("Abnormal closing connection");
				}
				tcpServer.clientsCounter.decrementAndGet();
			}
			
		} 


}
