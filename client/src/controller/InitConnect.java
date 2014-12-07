package controller;

import view.InitGUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;

public class InitConnect {

    public int connectionStatus;
	public static int volPort;

    public final static int FAIL_CONNECTION = 0;
    public final static int DISCONNECTED = 1;
    public final static int DISCONNECTING = 2;
    public final static int CONNECTING = 3;
    public final static int CONNECTED = 4;
    public final static int UPLOADING = 5;
    public final static int DOWNLOADING = 6;
    public final static int REFRESHING = 7;

    public final static String statusMessages[] = {
            " Error! Could not connect! ", " Disconnected ",
            " Disconnecting... ", " Connecting... ", " Connected ",
            " Uploading... ", " Downloading... ", " Get list from server!!! "
    };

    public final static Color statusColors[] = {
            Color.RED, Color.blue, Color.ORANGE, Color.orange, Color.GREEN, Color.CYAN, Color.CYAN, Color.ORANGE
    };

    private static POJOSettings settingsFromFile;
    private LineNumberReader lineRead;
    private Connection conn;

    public InitConnect() {
        settingsFromFile = new POJOSettings();
        loadSettings();
    }

    public void setConnection(InitGUI initGUI){
        if(settingsFromFile.getAnonym()==true) {
			try {
				conn = new Connection(this, settingsFromFile.getServerIP(), settingsFromFile.getServerPort(), settingsFromFile.getProxyIP(), settingsFromFile.getProxyPort());
			} catch (IOException e) {
				this.connectionStatus = InitConnect.FAIL_CONNECTION;
				initGUI.changeGUIStatus();
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}
			if(settingsFromFile.getVolunteer()==true){

				new Thread(new Runnable(){
					Socket socket = null;
					ServerSocket clientServerSocket = null;
					@Override
					public void run(){
						try{
							clientServerSocket = new ServerSocket(0);
							volPort = clientServerSocket.getLocalPort();
							Report.lgr.log(Level.INFO, "Volunteer port: " + clientServerSocket.getLocalPort(), "");
							while( true ){
								try{
									socket = clientServerSocket.accept();
									ConnectionVolunteer con = new ConnectionVolunteer(socket, volPort, conn);
									new Thread(con).start();
								}catch( SocketTimeoutException e ){
									Report.lgr.log(Level.WARNING, "Timeout Occurred", e);
								}
							}
						}catch( SocketException e ){
							Report.lgr.log(Level.WARNING, e.getMessage(), e);
						}catch( IOException e ){
							Report.lgr.log(Level.WARNING, e.getMessage(), e);
						}finally{
							try{
								socket.close();
							}catch( IOException e ){
								Report.lgr.log(Level.WARNING, "Could not close port - "+e.getMessage(), e);
							}
						}
					}
				}).start();
			}
			conn.initServer();
			conn.initProxy();
        }else{
			try {
				conn = new Connection(this, settingsFromFile.getServerIP(), settingsFromFile.getServerPort() );
				conn.initServer();
			} catch (IOException e) {
				this.connectionStatus = InitConnect.FAIL_CONNECTION;
				initGUI.changeGUIStatus();
				Report.lgr.log(Level.WARNING, e.getMessage(), e);
			}

        }
    }

    public void unSetConnection(InitGUI initGUI){
        conn.destroy();
        conn = null;
        System.gc();
    }

    private void loadSettings(){
        try {
            lineRead = new LineNumberReader(new FileReader("connect.ini"));
            String line = null;

            line = readLine();
            if(line!=null && line.split("=").length==2) {
                settingsFromFile.setServerIP(line.split("=")[1]);
            }
            line = readLine();
            if(line!=null && line.split("=").length==2) {
                settingsFromFile.setServerPort(line.split("=")[1]);
            }
            line = readLine();
            if(line!=null && line.split("=").length==2) {
                settingsFromFile.setProxyIP(line.split("=")[1]);
            }
            line = readLine();
			if(line!=null && line.split("=").length==2) {
				settingsFromFile.setProxyPort(line.split("=")[1]);
			}
			line = readLine();
			if(line!=null && line.split("=").length==2) {
				settingsFromFile.setAnonym(Boolean.parseBoolean(line.split("=")[1] ));
			}
			line = readLine();
			if(line!=null && line.split("=").length==2) {
				settingsFromFile.setVolunteer(Boolean.parseBoolean(line.split("=")[1]));
			}
			line = readLine();
            if(line!=null && line.split("=").length==2) {
                settingsFromFile.setNickName(line.split("=")[1]);
            }
            if(settingsFromFile.getAnonym()==Boolean.FALSE) {
                Report.lgr.log(Level.INFO, "server ip: " + InitConnect.getSettingsFromFile().getServerIP() + ", " +
                        "server port: " + InitConnect.getSettingsFromFile().getServerPort() + ", " +
                        "nickname: " + InitConnect.getSettingsFromFile().getNickName() + ", " +
                        "be anonymous: " + InitConnect.getSettingsFromFile().getAnonym(), "");
            }else{
                Report.lgr.log(Level.INFO, "server ip: " + InitConnect.getSettingsFromFile().getServerIP() + ", " +
                        "server port: " + InitConnect.getSettingsFromFile().getServerPort() + ", " +
                        "proxy ip: " + InitConnect.getSettingsFromFile().getProxyIP() + ", " +
                        "proxy port: " + InitConnect.getSettingsFromFile().getProxyPort() + ", " +
                        "nickname: " + InitConnect.getSettingsFromFile().getNickName() + ", " +
                        "be anonymous: " + InitConnect.getSettingsFromFile().getAnonym() + ", " +
						"be volunteer: " + InitConnect.getSettingsFromFile().getVolunteer(), "");
            }
        } catch (FileNotFoundException e) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private String readLine(){
        try {
            return lineRead.readLine();
        } catch (IOException e) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static POJOSettings getSettingsFromFile() {
        return settingsFromFile;
    }

    public DefaultListModel<String> informServerAboutMyRequestGetList() {
			return conn.getListFromServer();

	}

    public void informServerAboutMyRequestDownload(String selectedFile) {
        conn.getFileFromServer(selectedFile);
    }

    public void informServerAboutMyRequestUpload(File selectedFile) {
        System.out.println(selectedFile.getAbsoluteFile());
        if(getSettingsFromFile().getAnonym()==true){
			Report.lgr.log(Level.INFO, "Upload via proxy", "");
            conn.pushFileToServerOrProxy(selectedFile, conn.getProxy());
        } else {
			Report.lgr.log(Level.INFO, "Upload direct to server", "");
            conn.pushFileToServerOrProxy(selectedFile, conn.getServer());
        }
    }

    public void setLabels(JLabel labelProxyInfo, JLabel labelServerInfo, JLabel labelClientInfo) {
        labelClientInfo.setText(conn.getClientSocket().getLocalAddress() + ":" + conn.getClientSocket().getLocalPort());
        if(conn.getClientSocket()!=null) {
			if(conn.getClientSocket()!=null){
				labelServerInfo.setText(conn.getClientSocket().getInetAddress().getHostAddress()+":"+conn.getClientSocket().getPort());
			}
        }
        if(conn.getProxy()!=null){
			if( conn.getClientSocketProxy()!=null ){
				labelProxyInfo.setText(conn.getClientSocketProxy().getInetAddress().getHostAddress()+":"+conn.getClientSocketProxy().getPort());
			}
		}
    }
}
