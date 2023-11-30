package telran.net;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
public class TcpServer implements Runnable {
   public static final int IDLE_TIMEOUT = 100;
	private int port;
    private ApplProtocol protocol;
    private ServerSocket serverSocket;
    ExecutorService executor; 
    int nThreads = Runtime.getRuntime().availableProcessors();
    AtomicInteger clientsCounter = new AtomicInteger(0);
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
			while (!executor.isShutdown()) {
				try {
					Socket socket = serverSocket.accept();
					clientsCounter.incrementAndGet();
					socket.setSoTimeout(IDLE_TIMEOUT);
					ClientSessionHandler client = new ClientSessionHandler(socket, protocol, this);
					executor.execute(client);
				} 
				catch (SocketTimeoutException e) {
					//for exit from accept to another iteration of cycle
				}
				catch(Exception e) {
					throw new RuntimeException(e.toString());
				}
				
			
		} 

	}
	public void shutdown() {
		executor.shutdownNow();
	}

}
