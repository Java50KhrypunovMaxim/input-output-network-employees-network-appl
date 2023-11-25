package telran.net;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
public class TcpServer implements Runnable {
   public static final int IDLE_TIMEOUT = 100;
	private int port;
	boolean isShutdown = false;
    private ApplProtocol protocol;
    private ServerSocket serverSocket;
    AtomicInteger clientsCounter = new AtomicInteger();
    ExecutorService executor; 
    int nThreads = Runtime.getRuntime().availableProcessors();
	public TcpServer(int port, ApplProtocol protocol) throws Exception {
		this.port = port;
		this.protocol = protocol;
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(IDLE_TIMEOUT);
		executor = Executors.newFixedThreadPool(nThreads);
	}

	@Override
	public void run() {
		System.out.println("Server is listening on port " + port);
			while (!isShutdown) {
				try {
					Socket socket = serverSocket.accept();
					socket.setSoTimeout(IDLE_TIMEOUT);
					ClientSessionHandler client = new ClientSessionHandler(socket, protocol, this);
					executor.execute(client);
					clientsCounter.incrementAndGet();
					if(!isShutdown) {
						executor.execute(client);
					}
				} 
				catch (SocketTimeoutException e) {
					//for exit from accept to another iteration of cycle
				}
				catch(Exception e) {
					throw new RuntimeException(e.toString());
				}
				
			
		} 

	}
	public boolean shutdown() {
		executor.shutdownNow();
		isShutdown = true;
		return isShutdown;
	}

}
