import com.sun.corba.se.spi.activation.Server;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class Connection implements Runnable {

	private ServerState state;
	private ClientServer client;
	private SocketSSL socketSSL;
	private ServerSocket socket;
	private String serverIP;
	private int serverPort;
	private Boolean isVolunteer;
	private String IP;
	private String Port;

	public Connection(String serverIP, int serverPort, Socket clientSocket){
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		client = new ClientServer();
		client.setSocket( clientSocket );
		client.init();
		state = new Authorised();
	}

	@Override
	public void run() {
		while(!client.getSocket().isClosed()){
			Main.counter();
			String command = null;
			try{
				command = client.getInString().readLine();
				isVolunteer = Boolean.valueOf( command.replace(Commands.commandMessages[Commands.VOLUNTEER],"") );
				if(isVolunteer==true){
					IP = client.getInString().readLine().replace(Commands.commandMessages[Commands.COMMAND_NODE_IP],"");
					Port = client.getInString().readLine().replace(Commands.commandMessages[Commands.COMMAND_NODE_PORT],"");
				}
			}catch( IOException e ){
				e.printStackTrace();
			}
			if(isVolunteer==true){
				Main.volunteerConnection.add(this);
			}
			Report.lgr.log(Level.INFO, "Someone connected with IP: "+client.getSocket().getInetAddress()+", "+
					"PORT: "+client.getSocket().getPort()+", volunteer: "+isVolunteer, "");
			state.update();
		}

		//     System.out.println(client.getSocket().isClosed());
		Main.deccounter();
	}

	private interface ServerState{
		public abstract void update();
	}

	private class Authorised implements ServerState{
		public void update() {
			try {
				String command = client.getInString().readLine();
			//	System.out.println(command);
				// polu sovaro bug
				// an dein upirxe auti i grammi tote tha empaine se katastasi o server pou tha dimiourgouse
				// sunexeia nea threads ki en telei tha crashare o upologistis
				if(command==null){
					state = new Disconnect();
					state.update();
					client.getSocket().close();
				}
				if(command.startsWith(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY])){
					command = command.replace(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY], "");
					state = new GetFileFromClient(command);
					state.update();
				}else if(command.startsWith(Commands.commandMessages[Commands.COMMAND_DISCONNECT])) {
					state = new Disconnect();
					state.update();
				}
			} catch (IOException e) {
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}

	private class Disconnect implements ServerState{
		@Override
		public void update(){
			InetAddress tempIP = client.getSocket().getInetAddress();
			int tempPort = client.getSocket().getPort();
			Main.volunteerConnection.remove(Connection.this);
			try{
				client.getSocket().close();
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			Report.lgr.log(Level.INFO, "Someone diconnected with IP: " + tempIP + " - " + tempPort, "");
			state = null;
		}
	}

	private class GetFileFromClient implements ServerState{

		String fileName;

		public GetFileFromClient(String fileName){
			this.fileName = fileName;
		}

		@Override
		public void update() {
			try{
				//        outToClient.writeBytes(Commands.commandMessages[Commands
				// .COMMAND_UPLOAD_FILE_TO_SERVER]+fileName+"\n");
				String fileExists = client.getIn().readLine();
			//	System.out.println(fileExists);
				if(!fileExists.equals(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT])){
					int fileSize = Integer.parseInt(fileExists);
					byte[] mybytearray = new byte[fileSize];
					DataInputStream is = new DataInputStream(client.getSocket().getInputStream());
					FileOutputStream fos = new FileOutputStream(fileName);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					//    outToClient.writeBytes(Commands.commandMessages[Commands.COMMAND_SEND_FILE_SIZE]);
					int bytesRead = 0;
					while(bytesRead < fileSize-1){
						bytesRead+=is.read(mybytearray, bytesRead, mybytearray.length - bytesRead);
					}
					bos.write(mybytearray, 0, bytesRead);
					bos.close();
					Thread.sleep(10);
					Report.lgr.log(Level.INFO, client.getSocket().getInetAddress()+" uploaded: "+fileName + " - size: "+fileSize, "");
				}else{
					throw new FileNotFoundException();
				}
			}catch(FileNotFoundException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}catch(Exception e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			if(Main.volunteerConnection.size()>=3){
				state = new PushFileToFakeProxy(fileName);
			}else{
				state = new PushFileToServer(fileName);
			}
			state.update();
		}
	}

	private class PushFileToServer implements ServerState{

		private String fileName;

		public PushFileToServer(String fileName){
			this.fileName = fileName;
			socketSSL = new SocketSSL();
			SSLSocketFactory sslsocketfactoryServer = (SSLSocketFactory ) SSLSocketFactory.getDefault();
			try{
				socketSSL.setSocket( (SSLSocket) sslsocketfactoryServer.createSocket(serverIP, serverPort) );
				socketSSL.init();
				socketSSL.getOutString().write(Commands.commandMessages[Commands.COMMAND_USERNAME] + "proxy\n");
				socketSSL.getOutString().flush();
				Thread.sleep(10);
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}catch( InterruptedException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
		}

		@Override
		public void update() {
			try{
				socketSSL.getOutString().write(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY]+fileName+"\n");
				socketSSL.getOutString().flush();
				Thread.sleep(10);
				File myFile = new File(fileName);
				if(!myFile.exists()){
					throw new FileNotFoundException();
				}
				socketSSL.getOut().writeBytes(myFile.length() + "\n");
				byte[] fileBytes = new byte[(int) myFile.length()];
				BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
				bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
				bufferedInFromFile.close();
				Thread.sleep(10);
				socketSSL.getOut().write(fileBytes, 0, fileBytes.length);
				socketSSL.getOut().flush();
				Thread.sleep(10);
				//    System.out.println("File sent to server");
				Report.lgr.log(Level.INFO, "Upload to server completed, file: " + fileName + " - size: "
						+ myFile.length(), "");
			}catch(FileNotFoundException e){
				try {
					socketSSL.getOut().writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
					Report.lgr.log(Level.WARNING, "File not found!", "");
				} catch (IOException e1) {
					Report.lgr.log(Level.WARNING, e1.getMessage(), e1);
				}
			}catch(IOException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			} catch (InterruptedException e) {
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			socketSSL.getOutString().write(Commands.commandMessages[Commands.COMMAND_DISCONNECT] + "\n");
			socketSSL.getOutString().flush();
			Report.lgr.log(Level.INFO, "Proxy disconnected from server", "");
			try{
				socketSSL.getSocket().close();
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			state = new Authorised();
			state.update();
		}
	}

	private class PushFileToFakeProxy implements ServerState{

		private String fileName;

		public PushFileToFakeProxy(String fileName){
			this.fileName = fileName;
			socket = new ServerSocket();
			try{
				ArrayList<Connection> temp = new ArrayList<Connection>();
			//	temp.remove(this);
				System.out.println(getIP()+":"+getPort());
				Connection tempCon;
				Iterator<Connection> it = Main.volunteerConnection.iterator();
				// afairei ton euato tou
				while(it.hasNext()){
					Connection c = it.next();
					if(c.getPort()!=getPort() && c.getIP()!=getIP()){
						temp.add(c);
					}
				}
				for(int i=0; i<temp.size(); i++){
					System.out.println(" Possible client-proxies"+temp.get(i).getIP()+":"+temp.get(i).getPort());
				}
				int key;

				key = new Random().nextInt(temp.size());
				tempCon = temp.get(key);
				int node1Port = Integer.valueOf(tempCon.getPort());
				String node1IP;
				if(OsCheck.getOperatingSystemType()==OsCheck.OSType.Linux){
					node1IP = tempCon.getIP().replace("localhost/", "");
				}else{
					node1IP = tempCon.getIP().replace("/", "");
				}
				// afairei ton prwto node apo tin lista gia na min ksanaxrisimopoihthei
				temp.remove(tempCon);
				tempCon = null;

				key = new Random().nextInt(temp.size());
				tempCon = temp.get(key);
				int node2Port = Integer.valueOf(tempCon.getPort());
				String node2IP;
				if(OsCheck.getOperatingSystemType()==OsCheck.OSType.Linux){
					node2IP = tempCon.getIP().replace("localhost/", "");
				}else{
					node2IP = tempCon.getIP().replace("/", "");
				}
				tempCon = null;
				temp = null;

				System.out.println("Node 1, IP: " +node1IP +" - PORT: "+ node1Port);
				System.out.println("Node 2, IP: " +node2IP +" - PORT: "+ node2Port);

				socket.setSocket( new Socket(node1IP, node1Port) );
				socket.init();

				socket.getOutString().write(Commands.commandMessages[Commands.COMMAND_USERNAME]+"anonym\n");
				socket.getOutString().flush();
				Thread.sleep(10);
				socket.getOutString().write(Commands.commandMessages[Commands.COMMAND_HOOP]+"1\n");
				socket.getOutString().flush();
				Thread.sleep(10);
				socket.getOutString().write(Commands.commandMessages[Commands.COMMAND_NEXT_NODE_IP]+node2IP+"\n");
				socket.getOutString().flush();
				Thread.sleep(10);
				socket.getOutString().write(Commands.commandMessages[Commands.COMMAND_NEXT_NODE_PORT]+node2Port+"\n");
				socket.getOutString().flush();
				Thread.sleep(10);
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}catch( InterruptedException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
		}

		@Override
		public void update() {
			try{
				socket.getOutString().write(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY]+fileName+"\n");
				socket.getOutString().flush();
				Thread.sleep(10);
				File myFile = new File(fileName);
				if(!myFile.exists()){
					throw new FileNotFoundException();
				}
				socket.getOut().writeBytes(myFile.length() + "\n");
				byte[] fileBytes = new byte[(int) myFile.length()];
				BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
				bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
				bufferedInFromFile.close();
				Thread.sleep(10);
				socket.getOut().write(fileBytes, 0, fileBytes.length);
				socket.getOut().flush();
				Thread.sleep(10);
				//    System.out.println("File sent to server");
				Report.lgr.log(Level.INFO, "Upload to node1 completed, file: " + fileName + " - size: "
						+ myFile.length(), "");
			}catch(FileNotFoundException e){
				try {
					socket.getOut().writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
					Report.lgr.log(Level.WARNING, "File not found!", "");
				} catch (IOException e1) {
					Report.lgr.log(Level.WARNING, e1.getMessage(), e1);
				}
			}catch(IOException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			} catch (InterruptedException e) {
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			socket.getOutString().write(Commands.commandMessages[Commands.COMMAND_DISCONNECT] + "\n");
			socket.getOutString().flush();
			Report.lgr.log(Level.INFO, "Proxy disconnecting from node1", "");
			try{
				socket.getSocket().close();
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			state = new Authorised();
			state.update();
		}
	}

	public String getIP(){ return IP; }

	public String getPort(){
		return Port;
	}

}
