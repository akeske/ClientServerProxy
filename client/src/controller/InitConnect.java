package controller;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.logging.Level;

public class InitConnect {

    public int connectionStatus;

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

    public void setConnection(){
        if(settingsFromFile.getAnonym()==true) {
            conn = new Connection(this, settingsFromFile.getServerIP(), settingsFromFile.getServerPort(), settingsFromFile.getProxyIP(), settingsFromFile.getProxyPort());
            conn.initServer();
            conn.initProxy();
        }else{
            conn = new Connection(this, settingsFromFile.getServerIP(), settingsFromFile.getServerPort() );
            conn.initServer();
        }
    }

    public void unSetConnection(){
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
                        "be anonymous: " + InitConnect.getSettingsFromFile().getAnonym(), "");
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
            conn.pushFileToServerOrProxy(selectedFile, conn.getProxy());
        } else {
            conn.pushFileToServerOrProxy(selectedFile, conn.getServer());
        }
    }

    public void setLabels(JLabel labelProxyInfo, JLabel labelServerInfo, JLabel labelClientInfo) {
        labelClientInfo.setText(conn.getClientSocket().getLocalAddress() + ":" + conn.getClientSocket().getLocalPort());
        if(conn.getClientSocket()!=null) {
			if(conn.getClientSocket()!=null){
				labelServerInfo.setText(conn.getClientSocket().getInetAddress()+":"+conn.getClientSocket().getPort());
			}
        }
        if(conn.getProxy()!=null){
			if( conn.getClientSocketProxy()!=null ){
				labelProxyInfo.setText(conn.getClientSocketProxy().getInetAddress()+":"+conn.getClientSocketProxy().getPort());
			}
		}
    }
}
