import java.net.*;

public class Server {

	private static ServerSocket ss;
    private static Socket s = null;
    
	public static void main(String[] args) {
		try {
			InetAddress IP = InetAddress.getLocalHost();
	        System.out.println("Server IP: "+IP.getHostAddress());//Print Server IP
            ss = new ServerSocket(2399);//2399 used for data transfer
            System.out.println("Server is Running.");
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
		while (true) {
            try {
               s = ss.accept();
               System.out.println("\nClient Found: " + s);
                Thread t = new Thread(new serviceClient(s,ss));
                t.start();//to create Child 
            } catch (Exception e) {
                System.err.println(e);
            }
        }
	}

}
