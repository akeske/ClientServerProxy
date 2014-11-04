import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

public class ClientServer{

	private Socket socket;
	private BufferedReader in;
	private DataOutputStream out;
	private BufferedReader inString;
	private PrintWriter outString;

	public ClientServer(){ }

	public void init(){
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream());
			inString = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			outString = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		}catch(Exception e){
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public void close(){
		try{
			in.close();
			out.close();
			outString.close();
			inString.close();
			socket.close();
		}catch( IOException e ){
			Report.lgr.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public Socket getSocket(){
		return socket;
	}

	public void setSocket( Socket socket ){
		this.socket = socket;
	}

	public PrintWriter getOutString(){
		return outString;
	}

	public void setOutString( PrintWriter outString ){
		this.outString = outString;
	}

	public BufferedReader getInString(){
		return inString;
	}

	public void setInString( BufferedReader inString ){
		this.inString = inString;
	}

	public DataOutputStream getOut(){
		return out;
	}

	public void setOut( DataOutputStream out ){
		this.out = out;
	}

	public BufferedReader getIn(){
		return in;
	}

	public void setIn( BufferedReader in ){
		this.in = in;
	}
}
