package telran.net;
import java.net.*;
import java.io.*;
public class ClientSessionHandler implements Runnable {
 private static final long TOTAL_IDLE_TIMEOUT = 30000;
Socket socket;
 ObjectInputStream reader;
 ObjectOutputStream writer;
 ApplProtocol protocol;
 TcpServer tcpServer;
 long totalIdleTime = 0;
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
					//for exit from readObject to another iteration of cycle
					totalIdleTime += TcpServer.IDLE_TIMEOUT;
					if(totalIdleTime > TOTAL_IDLE_TIMEOUT &&
					tcpServer.clientsCounter.get() > tcpServer.nThreads) {
						break;
					}
				}
				catch (EOFException e) {
					System.out.println("Client closed connection");
					break;
				} 
				catch (Exception e) {
					System.out.println("Abnormal closing connection");
					break;
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				tcpServer.clientsCounter.decrementAndGet();
			}
			
		} 


}
