package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerProxy {

    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;
    private BufferedReader inString;
    private PrintWriter outString;

    public ServerProxy(){

    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
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
