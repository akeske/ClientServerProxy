package controller;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class ServerProxySSL{

    private SSLSocket sslSocket;
    private BufferedReader in;
    private DataOutputStream out;
    private BufferedReader inString;
    private PrintWriter outString;

    public ServerProxySSL(){}

	public void init(){
		try{
			in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
			out = new DataOutputStream(sslSocket.getOutputStream());
			inString = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), "UTF-8"));
			outString = new PrintWriter(new OutputStreamWriter(sslSocket.getOutputStream(), "UTF-8"));
		}catch(Exception e){
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		}
	}

    public SSLSocket getSocket() {
        return sslSocket;
    }

    public void setSocket(SSLSocket sslSocket) {
        this.sslSocket = sslSocket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public BufferedReader getInString() {
        return inString;
    }

    public void setInString(BufferedReader inString) {
        this.inString = inString;
    }

    public PrintWriter getOutString() {
        return outString;
    }

    public void setOutString(PrintWriter outString) {
        this.outString = outString;
    }
}
