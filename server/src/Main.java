import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;

public class Main {

	private static int counter = 0;

	public Main(int port) throws IOException {
		// einai static de xreiazetai instance
		new Report();
		new Thread(new TerminalThread()).start();
		SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	//	Socket clientSocket = null;
		SSLSocket sslsocket = null;

		try{
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
			Report.lgr.log(Level.INFO, "Server started", "");
			while (true) {
				try {
					sslsocket = (SSLSocket) sslserversocket.accept();
					ConnectionSSL con = new ConnectionSSL(sslsocket);
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
			//	clientSocket.close();
				sslsocket.close();
			} catch (IOException e) {
				Report.lgr.log(Level.WARNING, "Could not close port - " + e.getMessage(), e);
				System.exit(1);
			}
		}
	}

	public static void main (String args[]){
		System.setProperty("javax.net.ssl.keyStore", "server.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		try{
			int port;
			System.out.print("Please give me your desirable port: ");
			Scanner in = new Scanner(System.in);
			String portString = in.nextLine();
			if(portString.isEmpty()){
				port = 1234;
			}else{
				port = Integer.parseInt(portString);
			}

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
			System.out.println("\tYou have to open port " +port+ " from router");


			try {
				new Main(port);
			} catch (IOException e) {
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}

		}catch(NumberFormatException e){
			Report.lgr.log(Level.WARNING, "Invalid port number. Please enter an integer", e);
		}catch(ArrayIndexOutOfBoundsException e){
			Report.lgr.log(Level.WARNING, "No port number entered. Please enter a port number", e);
		} catch (SocketException e) {
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		} catch (UnknownHostException e) {
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		}
	}

	static synchronized void counter(){
		counter++;
		//    System.out.println(counter);
	}

	static synchronized void deccounter(){
		counter--;
		//    System.out.println(counter);
	}
}

class TerminalThread implements Runnable{

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		System.out.println();
		System.out.println("@@@@@@@   You can type 'exit' whenever you want to close SERVER!!!   @@@@@@@");
		System.out.println("\n");
		String command = in.nextLine();
		if(command.equalsIgnoreCase("exit")){
			System.exit(0);
		}
	}
}
