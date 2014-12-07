package controller;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class ConnectionVolunteer implements Runnable {

	private Connection conn;
	private ServerState state;
	private ServerProxy socketInput;
	private ServerProxy socketOutput;
	private String name;
	private int volPort;
	private String nextIP;
	private int nextPort;
	private int hoop;
	private static final int MAX_HOOPS = 3;

	public ConnectionVolunteer(Socket socket, int volPort, Connection conn ){
		this.volPort = volPort;
		this.conn = conn;
		socketInput = new ServerProxy();
		socketInput.setSocket( socket );
		socketInput.init();
		state = new Authorised();
	}

	@Override
	public void run() {
		while(!socketInput.getSocket().isClosed()){
			String command = null;
			try{
				name = socketInput.getInString().readLine().replace(Commands.commandMessages[Commands.COMMAND_USERNAME], "");
				hoop = Integer.valueOf( socketInput.getInString().readLine().replace(Commands.commandMessages[Commands.COMMAND_HOOP], ""));
				hoop++;
				if(hoop==MAX_HOOPS){

				}else{
					nextIP = socketInput.getInString().readLine().replace(Commands.commandMessages[Commands
							.COMMAND_NEXT_NODE_IP],"");
					nextPort = Integer.valueOf( socketInput.getInString().readLine().replace(Commands.commandMessages[Commands
							.COMMAND_NEXT_NODE_PORT],"") );
				}
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			Report.lgr.log(Level.INFO, "Someone ANONYM want to upload a file via you and connected with IP: " +
					""+socketInput.getSocket().getInetAddress()	+", "+ "PORT: "+socketInput.getSocket().getPort(), "");
			state.update();
		}
	}

	private interface ServerState{
		public abstract void update();
	}

	private class Authorised implements ServerState{
		public void update() {
			try {
				String command = socketInput.getInString().readLine();
			//	System.out.println(command);
				// polu sovaro bug
				// an dein upirxe auti i grammi tote tha empaine se katastasi o server pou tha dimiourgouse
				// sunexeia nea threads ki en telei tha crashare o upologistis
				if(command==null){
					state = new Disconnect();
					state.update();
					socketInput.getSocket().close();
				}
				if(command.startsWith(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY])){
					command = command.replace(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY], "");
					state = new GetFileFromProxy(command);
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
			InetAddress temp = socketInput.getSocket().getInetAddress();
			int tempPort = socketInput.getSocket().getPort();
			//    Thread.sleep(2000);
			try{
				socketInput.getSocket().close();
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			Report.lgr.log(Level.INFO, "You diconnected with IP: " + temp+":"+tempPort, "");
			state = null;
		}
	}

	private class GetFileFromProxy implements ServerState{

		String fileName;

		public GetFileFromProxy(String fileName){
			this.fileName = fileName;
		}

		@Override
		public void update() {
			try{
				// outToClient.writeBytes(Commands.commandMessages[Commands
				// .COMMAND_UPLOAD_FILE_TO_SERVER]+fileName+"\n");
				String fileExists = socketInput.getIn().readLine();
				//	System.out.println(fileExists);
				if(!fileExists.equals(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT])){
					int fileSize = Integer.parseInt(fileExists);
					byte[] mybytearray = new byte[fileSize];
					DataInputStream is = new DataInputStream(socketInput.getSocket().getInputStream());
					FileOutputStream fos = new FileOutputStream(fileName);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					//    outToClient.writeBytes(Commands.commandMessages[Commands.COMMAND_SEND_FILE_SIZE]);
					int bytesRead = 0;
					while(bytesRead < fileSize-1){
						bytesRead+=is.read(mybytearray, bytesRead, mybytearray.length - bytesRead);
					}
					bos.write(mybytearray, 0, bytesRead);
					bos.close();
					Thread.sleep(100);
					Report.lgr.log(Level.INFO, "I downloaded: " + ""+fileName+" - size: " + fileSize, "");

					if(hoop==MAX_HOOPS){
						File myFile = new File(fileName);
						conn.pushFileToServerOrProxy(myFile, conn.getServer());
					}else{
						state = new PushFileToNextNode(fileName, nextIP, nextPort);
					}
					state.update();
				}else{
					throw new FileNotFoundException();
				}
			}catch(FileNotFoundException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}catch(Exception e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}

		}
	}

	private class PushFileToNextNode implements ServerState{

		private String fileName;

		public PushFileToNextNode(String fileName, String nextIP, int nextPort){
			this.fileName = fileName;
			socketOutput = new ServerProxy();
			try{
				socketOutput.setSocket( new Socket(nextIP, nextPort) );
				socketOutput.init();
				socketOutput.getOutString().write(Commands.commandMessages[Commands.COMMAND_USERNAME]+"anonym\n");
				socketOutput.getOutString().flush();
				Thread.sleep(50);
				socketOutput.getOutString().write(Commands.commandMessages[Commands.COMMAND_HOOP] + hoop + "\n");
				socketOutput.getOutString().flush();
				Thread.sleep(50);
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}catch( InterruptedException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
		}

		@Override
		public void update() {
			try{
				socketOutput.getOutString().write(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY]+fileName+"\n");
				socketOutput.getOutString().flush();
				Thread.sleep(50);
				File myFile = new File(fileName);
				if(!myFile.exists()){
					throw new FileNotFoundException();
				}
				socketOutput.getOut().writeBytes(myFile.length() + "\n");
				byte[] fileBytes = new byte[(int) myFile.length()];
				BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
				bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
				bufferedInFromFile.close();
				Thread.sleep(50);
				socketOutput.getOut().write(fileBytes, 0, fileBytes.length);
				socketOutput.getOut().flush();
				Thread.sleep(50);
				//    System.out.println("File sent to server");
				Report.lgr.log(Level.INFO, "Upload to next node completed, file: " + fileName + " - size: "
						+ myFile.length(), "");
			}catch(FileNotFoundException e){
				try {
					socketOutput.getOut().writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
					Report.lgr.log(Level.WARNING, "File not found!", "");
				} catch (IOException e1) {
					Report.lgr.log(Level.WARNING, e1.getMessage(), e1);
				}
			}catch(IOException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			} catch (InterruptedException e) {
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			socketOutput.getOutString().write(Commands.commandMessages[Commands.COMMAND_DISCONNECT] + "\n");
			socketOutput.getOutString().flush();
			Report.lgr.log(Level.INFO, "Disconnecting from node2", "");
			try{
				socketOutput.getSocket().close();
			}catch( IOException e ){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			state = new Authorised();
			state.update();
		}
	}
}
