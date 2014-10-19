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
        conn = new Connection(this, settingsFromFile.getServerIP(), settingsFromFile.getPortNumber());
        conn.init();
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
                settingsFromFile.setPortNumber(line.split("=")[1]);
            }
            line = readLine();
            if(line!=null && line.split("=").length==2) {
                settingsFromFile.setNickName(line.split("=")[1]);
            }
            Report.lgr.log(Level.INFO, "server ip: " + InitConnect.getSettingsFromFile().getServerIP() + ", " +
                            "port number: " + InitConnect.getSettingsFromFile().getPortNumber() + ", " +
                            "nickname: " + InitConnect.getSettingsFromFile().getNickName(), "");
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
        conn.pushFileToServer(selectedFile);
    }
}
