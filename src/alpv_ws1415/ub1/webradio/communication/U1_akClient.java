package alpv_ws1415.ub1.webradio.communication;
import java.io.*;
//import java.net.InetAddress;
import java.net.InetSocketAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

public class U1_akClient implements Client
{
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
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
		//cheat; Client should get the audio format FROM THE SERVER
		

		String strFilename="data/test.wav";
		File soundFile = new File(strFilename);
		
		AudioInputStream audioInputStream = null;
		try
		{
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		AudioFormat	audioFormat = audioInputStream.getFormat();//<<<<<< audio format needed
		
		
		AudioPlayer audioplay=new AudioPlayer(audioFormat);
		audioplay.start();
		
		//connecting
		InetSocketAddress sockAdr = new InetSocketAddress(ip, port);
		
		try
		{
			connect(sockAdr);
			
			/**
			 * Source for the following Code:
			 * http://de.wikibooks.org/wiki/Java_Standard:_Socket_ServerSocket_(java.net)_UDP_und_TCP_IP
			 */
			
			/*
			BufferedReader bufferedReader = new BufferedReader(	new InputStreamReader(socket.getInputStream()));
			char[] buffer;
			int anzahlZeichen;
			String message;

			System.out.println("receiving messages:");
			*/
			while (true) {
				 System.out.println("waiting for package..");
				 InputStream in = socket.getInputStream();
				 DataInputStream dis = new DataInputStream(in);

				 int len = dis.readInt();
				 byte[] data = new byte[len];
				 if (len > 0) {
					 dis.readFully(data);
				 }
				 audioplay.writeBytes(data); //play bytes
				 System.out.println(data);
				 System.out.println();
				/*
				buffer = new char[10];
				anzahlZeichen = bufferedReader.read(buffer, 0 , 10);
				message = new String(buffer, 0, anzahlZeichen);
				System.out.println(message);
				*/
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
