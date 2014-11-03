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

    public Main(int port) throws IOException {
        // einai static de xreiazetai instance
        new Report();
        new Thread(new TerminalThread()).start();
        Socket clientSocket = null;
        try{
            ServerSocket connectionSocket = new ServerSocket(port);
            Report.lgr.log(Level.INFO, "Server started", "");
            while (true) {
                try {
                    clientSocket = connectionSocket.accept();
                    Connection con = new Connection(clientSocket);
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

    public static void main (String args[]) throws Exception{
        try{
            int port;
            System.out.print(" Please give me your desirable port: ");
            Scanner in = new Scanner(System.in);
            String portString = in.nextLine();
            if(portString==null){
                port = 1231;
            }else{
                port = Integer.parseInt(portString);
            }
            new Main(port);
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
        System.out.println("@@@@@@@   You can type 'exit' whenever you want to close server!!!   @@@@@@@");
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
