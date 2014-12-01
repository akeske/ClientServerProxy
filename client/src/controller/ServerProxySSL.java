package controller;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerProxySSL{

    private SSLSocket sslSocket;
    private BufferedReader in;
    private DataOutputStream out;
    private BufferedReader inString;
    private PrintWriter outString;

    public ServerProxySSL(){}

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
