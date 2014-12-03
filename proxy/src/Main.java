import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.logging.Level;

public class Main {

	private static int counter = 0;
	public static HashMap<Integer, String> volunteers;

	public Main(String serverHost, int serverPort, int portProxy) throws IOException {
		// einai static de xreiazetai instance
		new Report();
		new Thread(new TerminalThread()).start();
		Socket clientSocket = null;
		try{
			ServerSocket connectionSocket = new ServerSocket(portProxy);
			Report.lgr.log(Level.INFO, "Proxy started", "");
			while (true) {
				try {
					clientSocket = connectionSocket.accept();
					Connection con = new Connection(serverHost, serverPort, clientSocket);
					new Thread(con).start();
				}catch (SocketTimeoutException e){
					Report.lgr.log(Level.WARNING, "Timeout Occurred", e);
				}
			}
		} catch(SocketException e) {
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		} catch (IOException e) {
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		}finally{
			Report.getFh().close();
			try {
				clientSocket.close();
			} catch (IOException e) {
				Report.lgr.log(Level.WARNING, "Could not close port - " + e.getMessage(), e);
				System.exit(1);
			}
		}
	}

	public static void main(String args[]){
		System.setProperty("javax.net.ssl.trustStore", "server.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");

		volunteers = new HashMap< Integer, String >();

		try{
			int proxyPort = 4321;
			int serverPort = 1234;
			String serverHost ="127.0.0.1";
			String portString;
			String portString2;
			Scanner in = new Scanner(System.in);

            System.out.print("Please give me your desirable SERVER host: ");
		//	serverHost = in.nextLine();
		//	if(serverHost.isEmpty()){
		//		serverHost = "127.0.0.1";
		//	}

			System.out.print("Please give me your desirable SERVER port: ");
		//	portString = in.nextLine();
		//	if(portString.isEmpty()){
		//		serverPort = 1234;
		//	}else{
		//		serverPort = Integer.parseInt(portString);
		//	}

			System.out.print("Please give me your desirable PROXY port: ");
		//	portString2 = in.nextLine();
		//	if(portString2.isEmpty()){
		//		proxyPort = 4321;
		//	}else{
		//		proxyPort = Integer.parseInt(portString2);
		//	}

			System.out.println("\n\tServer host: " + serverHost + "\n\tServer port: " + serverPort);

			InetAddress inetAddr = InetAddress.getLocalHost();
			System.out.println("\n\tHostname: " + inetAddr.getHostName());
			System.out.println("\tLocal IP Address: " + inetAddr.getHostAddress());
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while(en.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) en.nextElement();
				if(ni.getName().startsWith("eth0")){
					System.out.println("\tNet interface: "+ni.getName());
					Enumeration e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()){
						InetAddress ip = (InetAddress) e2.nextElement();
						System.out.println("\t\tIP address: "+ ip.toString());
					}
				}
			}
			System.out.println("\tYou have to open port " +proxyPort+ " from router");


			new Main(serverHost, serverPort, proxyPort);

		}catch(NumberFormatException e){
			Report.lgr.log(Level.WARNING, "Invalid port number. Please enter an integer", e);
		}catch(ArrayIndexOutOfBoundsException e){
			Report.lgr.log(Level.WARNING, "No port number entered. Please enter a port number", e);
		} catch (SocketException e) {

		} catch (UnknownHostException e) {

		} catch (IOException e) {

		}
	}

	static synchronized void counter(){
		counter++;
	}

	static synchronized void deccounter(){
		counter--;
	}
}

class TerminalThread implements Runnable{

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		System.out.println();
		System.out.println("@@@@@@@      You can type 'exit' whenever you want to close PROXY!!!       @@@@@@@");
		System.out.println("@@@@@@@   You can type 'display' to display users and who are volunteers   @@@@@@@");
		System.out.println("\n");
		while(true){
			String command = in.nextLine();
			if( command.equalsIgnoreCase("exit") ){
				System.exit(0);
			}
			if( command.equalsIgnoreCase("display") ){
				System.out.println("  Total volunteers: "+Main.volunteers.size());
				Iterator<Integer> keySetIterator = Main.volunteers.keySet().iterator();
				while(keySetIterator.hasNext()){
					Integer key = keySetIterator.next();
					System.out.println("    PORT: " + key + ", IP: " + Main.volunteers.get(key));
				}
			}
		}
	}
}
