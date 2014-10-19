package controller;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;

public class Connection {

    private Socket clientSocket;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private BufferedReader inFromServerString;
    private PrintWriter outToServerString;

    public State state;
    public InitConnect initConnect;

    public Connection(InitConnect initConnect, String host, String port){
        initConnect.connectionStatus = InitConnect.CONNECTING;
        this.initConnect = initConnect;
        try{
            this.clientSocket = new Socket(host, Integer.parseInt(port) );
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServerString = new BufferedReader(new InputStreamReader (clientSocket.getInputStream(), "UTF-8"));
            outToServerString = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        } catch ( UnknownHostException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        } catch ( IOException e ) {
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void init() {
        if(clientSocket!=null){
            if(!clientSocket.isClosed()){
                try{
                    initConnect.connectionStatus = InitConnect.CONNECTED;
                    outToServerString.write(Commands.commandMessages[Commands.COMMAND_USERNAME] + initConnect
                            .getSettingsFromFile().getNickName() + "\n");
                    outToServerString.flush();
                    Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName() + " connected" , "");
                    state = new StateConnect();
                    state.execute();
                }catch ( Exception e ){
                    initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
                    state = new StateDisconnect();
                    state.execute();
                    Report.lgr.log(Level.WARNING, e.getMessage(), e);
                }
            }
        }else{
            initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            Report.lgr.log(Level.WARNING, "Could not connect to server!", "");
        }
    }

    public void destroy() {
        try {
            initConnect.connectionStatus = InitConnect.DISCONNECTING;
            outToServerString.write(Commands.commandMessages[Commands.COMMAND_DISCONNECT] + "\n");
            outToServerString.flush();
            Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName() + " disconnected" , "");
            Thread.sleep(100);
            clientSocket.close();
            initConnect.connectionStatus = InitConnect.DISCONNECTED;
        } catch (IOException e) {
            Report.lgr.log(Level.WARNING, "IO Error, please check connection to server", e);
            initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            clientSocket = null;
        } catch (InterruptedException e) {
            //do nothing if the pause is interupted
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
            initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            clientSocket = null;
        }
    }

    public DefaultListModel<String> getListFromServer(){
        outToServerString.write(Commands.commandMessages[Commands.COMMAND_GET_SEND_FILELIST] + "\n");
        outToServerString.flush();
        state = new StateGetListFromServer();
        DefaultListModel<String> files = state.executeGetList();
        initConnect.connectionStatus = InitConnect.CONNECTED;
        return files;
    }

    public void getFileFromServer(String selectedFile){
        outToServerString.write(Commands.commandMessages[Commands.COMMAND_DOWNLOAD_FILE_FROM_SERVER] +
                selectedFile + "\n");
        outToServerString.flush();
        state = new StateDownloadFile(selectedFile);
        state.execute();
    }

    public void pushFileToServer(File selectedFile) {
        outToServerString.write(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER] + selectedFile.getName
                () + "\n");
        outToServerString.flush();
        state = new StateUploadFile(selectedFile);
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
        public void execute(){
        }
    }

    private class StateDisconnect implements State{
        @Override
        public DefaultListModel<String> executeGetList() {
            return null;
        }

        @Override
        public void execute() {
        }
    }

    private class StateGetListFromServer implements State{
        @Override
        public void execute() {
        }

        @Override
        public DefaultListModel<String> executeGetList() {
            try {
                int i  = 0;
                DefaultListModel<String> file = new DefaultListModel<String>();
                file.addElement(inFromServerString.readLine());
                //    System.out.println(file.get(i));
                while(!file.get(i).startsWith(Commands.commandMessages[Commands.COMMAND_END_RECEIVING_FILELIST])){
                    file.addElement(inFromServerString.readLine());
                //    System.out.println(file.get(i));
                    i++;
                }
                Report.lgr.log(Level.INFO, InitConnect.getSettingsFromFile().getNickName() + " pull list of files" ,
                        "");
                return file;
            } catch (IOException e) {
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
                initConnect.connectionStatus = InitConnect.FAIL_CONNECTION;
            }
            return null;
        }
    }

    private class StateUploadFile implements State{

        private File selectedFile;
        public StateUploadFile(File selectedFile) {
            this.selectedFile = selectedFile;
        }

        @Override
        public DefaultListModel<String> executeGetList() {
            return null;
        }

        @Override
        public void execute() {
            try{
                File myFile = new File(String.valueOf(selectedFile.getAbsoluteFile()));
                if(!myFile.exists()){
                    throw new FileNotFoundException();
                }

                outToServer.writeBytes(myFile.length() + "\n");
                byte[] fileBytes = new byte[(int) myFile.length()];
                BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
                bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
                bufferedInFromFile.close();
                Thread.sleep(100);
                outToServer.write(fileBytes, 0, fileBytes.length);
                outToServer.flush();
                System.out.println("File sent to server");
                Report.lgr.log(Level.INFO, "Upload completed\nFile: " + selectedFile.getName() +"\nSize: " + myFile.length(),
                        "");
            }catch(FileNotFoundException e){
                try {
                    outToServer.writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
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
                String fileExists = inFromServer.readLine();
                if(!fileExists.equals(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT])){
                    int fileSize = Integer.parseInt(fileExists);
                //    System.out.println(fileSize);
                    byte[] mybytearray = new byte[fileSize];
                    DataInputStream is = new DataInputStream(clientSocket.getInputStream());
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
