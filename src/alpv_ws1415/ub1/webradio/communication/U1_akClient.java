package alpv_ws1415.ub1.webradio.communication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.net.InetAddress;
import java.net.InetSocketAddress;

public class U1_akClient implements Client
{
	java.net.Socket socket;
	int port;
	String ip;
	public U1_akClient (){
		this.ip = "localhost";
		this.port = 24;
	}
	public U1_akClient (String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public void run(){
		InetSocketAddress sockAdr = new InetSocketAddress(ip, port);
		
		try
		{
			connect(sockAdr);
			
			/**
			 * Source for the following Code:
			 * http://de.wikibooks.org/wiki/Java_Standard:_Socket_ServerSocket_(java.net)_UDP_und_TCP_IP
			 */
			BufferedReader bufferedReader = new BufferedReader(	new InputStreamReader(socket.getInputStream()));
			char[] buffer;
			int anzahlZeichen;
			String message;

			System.out.println("receiving messages:");
			
			while (true) {
				buffer = new char[10];
				anzahlZeichen = bufferedReader.read(buffer, 0 , 10);
				message = new String(buffer, 0, anzahlZeichen);
				System.out.println(message);
			}
			
		}
		catch(IOException e)
		{
		
		}
	}
	
	public void connect(InetSocketAddress serverAddress) throws IOException
	{
		socket = new java.net.Socket(serverAddress.getAddress(), serverAddress.getPort());
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
