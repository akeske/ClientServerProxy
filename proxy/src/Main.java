import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public class Main {

	private static int counter = 0;

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

	public static void main(String args[]) throws Exception{
		try{
			int proxyPort;
			int serverPort;
			String serverHost = "";
			String portString = "";
			Scanner in = new Scanner(System.in);

            System.out.print("Please give me your desirable SERVER host: ");
			serverHost = in.nextLine();
			if(serverHost==""){
				serverHost = "127.0.0.1";
			}
			System.out.print("Please give me your desirable SERVER port: ");
			portString = in.nextLine();
			if(portString==""){
				serverPort = 4321;
			}else{
				serverPort = Integer.parseInt(portString);
			}
			System.out.print("Please give me your desirable PROXY port: ");
			portString = in.nextLine();
			if(portString==""){
				proxyPort = 1234;
			}else{
				proxyPort = Integer.parseInt(portString);
			}

		//	serverHost = "127.0.0.1";
		//	serverPort = 1234;
		//	proxyPort = 4321;
			//	Report.lgr.log(Level.INFO, "server host: " + serverHost + ", server port: " + serverPort + ", " +
			//			"proxy port: " + proxyPort, "");
			new Main(serverHost, serverPort, proxyPort);
		}catch(NumberFormatException e){
			Report.lgr.log(Level.WARNING, "Invalid port number. Please enter an integer", e);
		}catch(ArrayIndexOutOfBoundsException e){
			Report.lgr.log(Level.WARNING, "No port number entered. Please enter a port number", e);
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
		System.out.println("@@@@@@@   You can type 'exit' whenever you want to close PROXY!!!   @@@@@@@");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		String command = in.nextLine();
		if(command.equalsIgnoreCase("exit")){
			System.exit(0);
		}
	}
}
