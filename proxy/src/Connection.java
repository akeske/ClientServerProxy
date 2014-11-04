import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;

public class Connection implements Runnable {

	private ServerState state;
	private ClientServer client;
	private ClientServer server;
	private String serverIP;
	private int serverPort;

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
			Report.lgr.log(Level.INFO, "someone connected with IP: " + client.getSocket().getInetAddress(), "");
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
			InetAddress temp = client.getSocket().getInetAddress();
			//    Thread.sleep(2000);
			client.close();
			Report.lgr.log(Level.INFO, "someone diconnected with IP: " + temp, "");
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
					Thread.sleep(100);
					Report.lgr.log(Level.INFO, client.getSocket().getInetAddress() + " uploaded: " + fileName + " - size: " +
							fileSize, "");
				}else{
					throw new FileNotFoundException();
				}
			}catch(FileNotFoundException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}catch(Exception e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			state = new PushFileToServer(fileName);
			state.update();
		}
	}

	private class PushFileToServer implements ServerState{

		private String fileName;

		public PushFileToServer(String fileName){
			this.fileName = fileName;
			server = new ClientServer();
			try{
				server.setSocket( new Socket(serverIP, serverPort) );
			}catch( IOException e ){
				e.printStackTrace();
			}
			server.init();
			server.getOutString().write(Commands.commandMessages[Commands.COMMAND_USERNAME] + "proxy\n");
			server.getOutString().flush();
		}

		@Override
		public void update() {
			try{
				server.getOutString().write(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY]+fileName+"\n");
				server.getOutString().flush();
				Thread.sleep(100);
				File myFile = new File(fileName);
				if(!myFile.exists()){
					throw new FileNotFoundException();
				}
				server.getOut().writeBytes(myFile.length() + "\n");
				byte[] fileBytes = new byte[(int) myFile.length()];
				BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
				bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
				bufferedInFromFile.close();
				Thread.sleep(100);
				server.getOut().write(fileBytes, 0, fileBytes.length);
				server.getOut().flush();
				Thread.sleep(100);
				//    System.out.println("File sent to server");
				Report.lgr.log(Level.INFO, "Upload to server completed, file: " + fileName + " - size: "
						+ myFile.length(), "");
			}catch(FileNotFoundException e){
				try {
					server.getOut().writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
					Report.lgr.log(Level.WARNING, "File not found!", "");
				} catch (IOException e1) {
					Report.lgr.log(Level.WARNING, e1.getMessage(), e1);
				}
			}catch(IOException e){
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			} catch (InterruptedException e) {
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			server.getOutString().write(Commands.commandMessages[Commands.COMMAND_DISCONNECT] + "\n");
			server.getOutString().flush();
			Report.lgr.log(Level.INFO, "proxy disconnected from server", "");
			server.close();
			state = new Authorised();
			state.update();
		}
	}
}
