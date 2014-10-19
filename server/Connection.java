import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.logging.Level;

public class Connection implements Runnable {

    public final static int FILE_SERVER_CLIENT = 1;
    public final static int FILE_CLIENT_SERVER = 2;
    public final static int FILE_NOT_FOUND = 3;
    public final static int START_FILELIST = 4;
    public final static int END_FILELIST = 5;
    public final static int SEND_FILE_SIZE = 6;

    private String userName;

    private Socket clientSocket;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;
    private BufferedReader inFromClientString;
    private PrintWriter outToClientString;
    private ServerState state;
    private String nameOfFile;

    public String getUserName() {
        return userName;
    }

    public Connection(Socket clientSocket){
        this.clientSocket = clientSocket;
        state = new Authorised();
        try{
            inFromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            outToClient = new DataOutputStream(this.clientSocket.getOutputStream());
            inFromClientString = new BufferedReader(new InputStreamReader (clientSocket.getInputStream(), "UTF-8"));
            outToClientString = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        }catch(Exception e){
            Report.lgr.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        while(!clientSocket.isClosed()){
            try{
                Main.counter();
                String command = inFromClientString.readLine();
            //    System.out.println(command);
                userName = command.replace(Commands.commandMessages[Commands.COMMAND_USERNAME], "");
                Report.lgr.log(Level.INFO, userName + " connected with IP: " + clientSocket.getInetAddress(), "");
                state.update();
            }catch(Exception e){
                Report.lgr.log(Level.WARNING, userName + " -> brute force disconnected", e);
            }
        }
   //     System.out.println(clientSocket.isClosed());
        Main.deccounter();
    }

    private interface ServerState{
        public abstract void update();
    }

    private class Authorised implements ServerState{
        public void update() {
            try {
                String command = inFromClientString.readLine();
            //    System.out.println(command);
                // polu sovaro bug
                // an dein upirxe auti i grammi tote tha empaine se katastasi o server pou tha dimiourgouse
                // sunexeia nea threads ki en telei tha crashare o upologistis
                if(command==null){
                    state = new Disconnect();
                    state.update();
                    clientSocket.close();
                }
                if(command.startsWith(Commands.commandMessages[Commands.COMMAND_DISCONNECT])) {
                    state = new Disconnect();
                    state.update();
                }else if(command.startsWith(Commands.commandMessages[Commands.COMMAND_GET_SEND_FILELIST])){
                    state = new SendListServerFiles();
                    state.update();
                }else if(command.startsWith(Commands.commandMessages[Commands.COMMAND_DOWNLOAD_FILE_FROM_SERVER])){
                    command = command.replace(Commands.commandMessages[Commands.COMMAND_DOWNLOAD_FILE_FROM_SERVER], "");
                    state = new PushFileToClient(command);
                    state.update();
                }else if(command.startsWith(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER])){
                    command = command.replace(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER], "");
                    state = new GetFileFromClient(command);
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
            try {
                InetAddress temp = clientSocket.getInetAddress();
            //    Thread.sleep(2000);
                clientSocket.close();
                inFromClient.close();
                outToClient.close();
                inFromClientString.close();
                outToClientString.close();
                Report.lgr.log(Level.INFO, userName + " diconnected with IP: " + temp, "");
                state = null;
                temp = null;
            } catch (IOException e) {
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    private class SendListServerFiles implements ServerState{
        public void update() {
            String path = ".";
            File dir = new File(path);
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String name = pathname.getName();
                    //    return name.endsWith(".") && pathname.isFile();
                    return pathname.isFile();
                }
            });
        //    for(File f : files){
        //        System.out.println(f.getName());
        //    }
            String output = "";
            for (int fileNo = 0; fileNo < files.length; fileNo++) {
                output += files[fileNo].getName() + "\n";
            }
            //    outToClient.writeBytes(Commands.commandMessages[Commands.COMMAND_START_SENDING_FILELIST]);
            outToClientString.write(output);
            outToClientString.flush();
            outToClientString.write(Commands.commandMessages[Commands.COMMAND_END_RECEIVING_FILELIST] + "\n");
            outToClientString.flush();
            Report.lgr.log(Level.INFO, userName + " -> get file list", "");
            state = new Authorised();
            state.update();
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
                String fileExists = inFromClient.readLine();
                if(!fileExists.equals(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT])){
                    int fileSize = Integer.parseInt(fileExists);
                    byte[] mybytearray = new byte[fileSize];
                    DataInputStream is = new DataInputStream(clientSocket.getInputStream());
                    FileOutputStream fos = new FileOutputStream(fileName);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                //    outToClient.writeBytes(Commands.commandMessages[Commands.COMMAND_SEND_FILE_SIZE]);
                    int bytesRead = 0;
                    while(bytesRead < fileSize-1){
                        bytesRead+=is.read(mybytearray, bytesRead, mybytearray.length - bytesRead);
                    }
                    bos.write(mybytearray, 0, bytesRead);
                    bos.close();
                    Report.lgr.log(Level.INFO, userName + " -> downloaded: " + fileName + " - size: " + fileSize, "");
                }else{
                    throw new FileNotFoundException();
                }
            }catch(FileNotFoundException e){
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            }catch(Exception e){
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            }
            state = new Authorised();
            state.update();
        }
    }

    private class PushFileToClient implements ServerState{

        private String fileName;
        public PushFileToClient(String fileName){
            this.fileName = fileName;
        }

        @Override
        public void update() {
        //    System.out.println(fileName);
            try{
          //      outToClient.writeBytes(Commands.commandMessages[Commands.COMMAND_UPLOAD_FILE_TO_SERVER] +
          //              fileName + "\n");
                File myFile = new File(fileName);
                if(!myFile.exists()){
                    throw new FileNotFoundException();
                }
            //    System.out.println(myFile.length());
                outToClient.writeBytes(myFile.length() + "\n");
                byte[] fileBytes = new byte[(int) myFile.length()];
                BufferedInputStream bufferedInFromFile = new BufferedInputStream(new FileInputStream(myFile));
                bufferedInFromFile.read(fileBytes, 0, fileBytes.length);
                bufferedInFromFile.close();
            //    inFromClient.readLine();
                Thread.sleep(100);
                outToClient.write(fileBytes, 0, fileBytes.length);
                outToClient.flush();
                Report.lgr.log(Level.INFO, userName + " -> uploaded: " + fileName + " - size: " + fileBytes.length, "");
            }catch(FileNotFoundException e){
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
                try {
                    outToClient.writeBytes(Commands.commandMessages[Commands.COMMAND_FILE_NOT_FOUNT] + "\n");
                } catch (IOException e1) {
                    Report.lgr.log(Level.WARNING, e1.getMessage(), e1);
                }
            }catch(IOException e){
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            } catch (InterruptedException e) {
                Report.lgr.log(Level.WARNING, e.getMessage(), e);
            }
            state = new Authorised();
            state.update();
        }
    }
}
