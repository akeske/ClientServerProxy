package controller;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class Connection {

    ServerProxy server;
    ServerProxy proxy;

    public boolean anonym;

    public State state;
    public InitConnect initConnect;

    public Connection(InitConnect initConnect){
        this.initConnect = initConnect;
        initConnect.connectionStatus = InitConnect.CONNECTING;
        anonym = initConnect.getSettingsFromFile().getAnonym();
    }

    public Connection(InitConnect initConnect, String serverIP, String serverPort, String proxyIP, String proxyPort){
        this(initConnect);
        server = new ServerProxy();
        proxy = new ServerProxy();
        try{
            server.setSocket( new Socket(serverIP, Integer.parseInt(serverPort)) );
            server.setIn( new BufferedReader(new InputStreamReader(server.getSocket().getInputStream())) );
            server.setOut( new DataOutputStream(server.getSocket().getOutputStream()) );
            server.setInString( new BufferedReader(new InputStreamReader (server.getSocket().getInputStream(),"UTF-8")) );
            server.setOutString( new PrintWriter(new OutputStreamWriter(server.getSocket().getOutputStream(),"UTF-8")) );
        } catch ( UnknownHostException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        } catch ( IOException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
        try{
            proxy.setSocket( new Socket(proxyIP, Integer.parseInt(proxyPort)) );
            proxy.setIn( new BufferedReader(new InputStreamReader(proxy.getSocket().getInputStream())) );
            proxy.setOut( new DataOutputStream(proxy.getSocket().getOutputStream()) );
            proxy.setInString( new BufferedReader(new InputStreamReader (proxy.getSocket().getInputStream(),"UTF-8")) );
            proxy.setOutString( new PrintWriter(new OutputStreamWriter(proxy.getSocket().getOutputStream(), "UTF-8")) );
        } catch ( UnknownHostException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        } catch ( IOException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public Connection(InitConnect initConnect, String serverIP, String serverPort) {
        this(initConnect);
        server = new ServerProxy();
        try{
            server.setSocket( new Socket(serverIP, Integer.parseInt(serverPort)) );
            server.setIn( new BufferedReader(new InputStreamReader(server.getSocket().getInputStream())) );
            server.setOut( new DataOutputStream(server.getSocket().getOutputStream()) );
            server.setInString( new BufferedReader(new InputStreamReader (server.getSocket().getInputStream(),"UTF-8")) );
            server.setOutString( new PrintWriter(new OutputStreamWriter(server.getSocket().getOutputStream(),"UTF-8")) );
        } catch ( UnknownHostException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        } catch ( IOException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public ServerProxy getProxy() {
        return proxy;
    }

    public ServerProxy getServer() {
        return server;
    }

    public Socket getClientSocket() {
        return server.getSocket();
    }

    public Socket getClientSocketProxy() {
        return proxy.getSocket();
    }

    public void initServer() {
        if ( server.getSocket()!=null ) {
            if ( !server.getSocket().isClosed() ) {
                try {
					if(anonym==true){
						initConnect.connectionStatus = InitConnect.CONNECTING;
					}else{
						initConnect.connectionStatus = InitConnect.CONNECTED;
					}
                    server.getOutString().write(Commands.commandMessages[Commands.COMMAND_USERNAME] +
                            initConnect.getSettingsFromFile().getNickName() + "\n");
                    server.getOutString().flush();
                    Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName() + " connected with " +
							"server: " + server.getSocket().getInetAddress()+":"+server.getSocket().getPort(), "");
                    state = new StateConnect();
                    state.execute();
                } catch ( Exception e ) {
                    initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
                    state = new StateDisconnect();
                    state.execute();
                    Report.lgr.log(Level.WARNING, e.getMessage(), e);
                }
            }
        } else {
            initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            Report.lgr.log(Level.WARNING, "Could not connect to server!", "");
        }
    }

    public void initProxy(){
		if ( !server.getSocket().isClosed() ){
			if( proxy.getSocket()!=null ){
				if( ! proxy.getSocket().isClosed() ){
					try{
						initConnect.connectionStatus = InitConnect.CONNECTED;
						Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName()+" connected with "+
								"proxy: "+proxy.getSocket().getInetAddress()+":"+proxy.getSocket().getPort(), "");
						state = new StateConnect();
						state.execute();
					}catch( Exception e ){
						initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
						state = new StateDisconnect();
						state.execute();
						destroy();
						Report.lgr.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}else{
				destroy();
				initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
				Report.lgr.log(Level.WARNING, "Could not connect to proxy!", "");
			}
		}else{
			initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
			Report.lgr.log(Level.WARNING, "Could not connect to proxy!", "");
		}
    }

    public void destroy() {
        try {
            initConnect.connectionStatus = InitConnect.DISCONNECTING;
			if(anonym==true && proxy.getSocket()!=null ){
				proxy.getOutString().write(Commands.commandMessages[Commands.COMMAND_DISCONNECT]+"\n");
				proxy.getOutString().flush();
				Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName()+" disconnected from proxy", "");
				Thread.sleep(100);
				proxy.getSocket().close();
			}
            server.getOutString().write(Commands.commandMessages[Commands.COMMAND_DISCONNECT] + "\n");
            server.getOutString().flush();
            Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName() + " disconnected from server", "");
            Thread.sleep(100);
            server.getSocket().close();

            initConnect.connectionStatus = InitConnect.DISCONNECTED;
        } catch (IOException e) {
            Report.lgr.log(Level.WARNING, "IO Error, please check the connection with the server or with the proxy", e);
            initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            server.setSocket(null);
            proxy.setSocket(null);
        } catch (InterruptedException e) {
            //do nothing if the pause is interupted
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
            initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            server.setSocket(null);
            proxy.setSocket(null);
        }
    }

    public DefaultListModel<String> getListFromServer(){
        server.getOutString().write(Commands.commandMessages[Commands.COMMAND_GET_SEND_FILELIST] + "\n");
        server.getOutString().flush();
        state = new StateGetListFromServer();
        DefaultListModel<String> files = state.executeGetList();
        initConnect.connectionStatus = InitConnect.CONNECTED;
        return files;
    }

    public void getFileFromServer(String selectedFile){
        server.getOutString().write(Commands.commandMessages[Commands.COMMAND_DOWNLOAD_FILE_FROM_SERVER] +
                selectedFile + "\n");
        server.getOutString().flush();
        state = new StateDownloadFile(selectedFile);
        state.execute();
    }

    public void pushFileToServerOrProxy(File selectedFile, ServerProxy temp) {
	//	System.out.println("selected socket: " + temp.getSocket().getInetAddress() +":"+temp.getSocket().getPort());
		temp.getOutString().write(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER_PROXY]+selectedFile.getName()+"\n");
		temp.getOutString().flush();
        state = new StateUploadFileViaProxyOrServer(selectedFile, temp);
        state.execute();
    }

    private interface State{
        public abstract DefaultListModel<String> executeGetList();
        public abstract void execute();
    }

    private class StateConnect implements State{
        @Override
        public DefaultListModel<String> executeGetList() {
            return null;
        }

        @Override
        public void execute(){}
    }

    private class StateDisconnect implements State{
        @Override
        public DefaultListModel<String> executeGetList() {
            return null;
        }

        @Override
        public void execute() {}
    }

    private class StateGetListFromServer implements State{
        @Override
        public void execute() {}

        @Override
        public DefaultListModel<String> executeGetList() {
            try {
                int i  = 0;
                DefaultListModel<String> file = new DefaultListModel<String>();
                file.addElement(server.getInString().readLine());
                //    System.out.println(file.get(i));
                while(!file.get(i).startsWith(Commands.commandMessages[Commands.COMMAND_END_RECEIVING_FILELIST])){
                    file.addElement(server.getInString().readLine());
                //    System.out.println(file.get(i));
                    i++;
                }
                Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName() + " pull list of files", "");
                return file;
            } catch (IOException e) {
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
                initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            }
            return null;
        }
    }

    private class StateUploadFileViaProxyOrServer implements State{

        private File selectedFile;
        private ServerProxy temp;

        public StateUploadFileViaProxyOrServer(File selectedFile, ServerProxy temp) {
            this.selectedFile = selectedFile;
            this.temp = temp;
        }

        @Override
        public DefaultListModel<String> executeGetList() {
            return null;
        }

        @Override
        public void execute() {
            try{
				Thread.sleep(100);
                File myFile = new File(String.valueOf(selectedFile.getAbsoluteFile()));
                if(!myFile.exists()){
                    throw new FileNotFoundException();
                }

                temp.getOut().writeBytes(myFile.length() + "\n");
                byte[] fileBytes = new byte[(int) myFile.length()];
                BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
                bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
                bufferedInFromFile.close();
                Thread.sleep(100);
                temp.getOut().write(fileBytes, 0, fileBytes.length);
                temp.getOut().flush();
            //    System.out.println("File sent to server");
                if(anonym==true){
                    Report.lgr.log(Level.INFO, "Upload to proxy completed -> file: " + selectedFile.getName() +
                                    " - size: " + myFile.length(), "");
                }else {
                    Report.lgr.log(Level.INFO, "Upload to server completed -> file: " + selectedFile.getName() +
                                    " - size: " + myFile.length(), "");
                }
			//	String report = temp.getInString().readLine();
				Thread.sleep(100);
            }catch(FileNotFoundException e){
                try {
                    temp.getOut().writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
                    Report.lgr.log(Level.WARNING, "File not found!", "");
                } catch (IOException e1) {
                    Report.lgr.log(Level.WARNING, e1.getMessage(), e1);
                }
            }catch(IOException e){
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            } catch (InterruptedException e) {
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    private class StateDownloadFile implements State{

        private String fileName;
        public StateDownloadFile(String fileName){
            this.fileName = fileName;
        }

        @Override
        public DefaultListModel<String> executeGetList() {
            return null;
        }

        @Override
        public void execute() {
            try{
                String fileExists = server.getIn().readLine();
                if(!fileExists.equals(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT])){
                    int fileSize = Integer.parseInt(fileExists);
                //    System.out.println(fileSize);
                    byte[] mybytearray = new byte[fileSize];
                    DataInputStream is = new DataInputStream(server.getSocket().getInputStream());
                    FileOutputStream fos = new FileOutputStream(fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    //        outToServer.writeBytes(Commands.commandMessages[Commands.COMMAND_SEND_FILE_SIZE]);
                    int bytesRead = 0;
                    while(bytesRead < fileSize-1){
                        bytesRead+=is.read(mybytearray, bytesRead, mybytearray.length - bytesRead);
                    }
                    bos.write(mybytearray, 0, bytesRead);
                    bos.close();
                    Report.lgr.log(Level.INFO, "Download completed\nFile: " + fileName +"\nSize: " + fileSize, "");
                }else{
                    throw new FileNotFoundException();
                }
            }catch(FileNotFoundException e){
                Report.lgr.log(Level.WARNING, "File not found to download", e);
            }catch(Exception e){
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
