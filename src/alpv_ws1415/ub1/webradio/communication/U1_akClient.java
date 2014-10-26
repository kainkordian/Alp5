package alpv_ws1415.ub1.webradio.communication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.net.InetAddress;
import java.net.InetSocketAddress;

public class U1_akClient implements Client
{
	java.net.Socket socket;
	int port = 24;
	String ip = "192.168.178.93";
	
	public void setAddress (String newIp, int newPort){
		ip = newIp;
		port = newPort;
	}
	
	public void run(){
		InetSocketAddress sockAdr = new InetSocketAddress(ip, port);
		
		try
		{
			connect(sockAdr);
			
			/**
			 * Source for the following 4 Code Rows (30-33):
			 * http://de.wikibooks.org/wiki/Java_Standard:_Socket_ServerSocket_(java.net)_UDP_und_TCP_IP
			 */
			BufferedReader bufferedReader = new BufferedReader(	new InputStreamReader(socket.getInputStream()));
			char[] buffer = new char[200];
			int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
			String message = new String(buffer, 0, anzahlZeichen);

			System.out.println("message recieved:");
			System.out.println(message);
		}
		catch(IOException e)
		{
		
		}
	}
	
	public void connect(InetSocketAddress serverAddress) throws IOException
	{
		socket = new java.net.Socket(ip, port);
	}

	/**
	 * Close this client and free any resources associated with it.
	 */
	public void close()
	{
		
	}

	/**
	 * Send a chat message. Adding this clients user-name to the message as well
	 * as packing it may be implementation-dependent.
	 * 
	 * @param message A message to send.
	 * @throws IOException
	 */
	public void sendChatMessage(String message) throws IOException
	{
		
	}

}
