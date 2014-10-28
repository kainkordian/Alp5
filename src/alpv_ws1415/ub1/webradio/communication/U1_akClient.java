package alpv_ws1415.ub1.webradio.communication;
import java.io.*;
//import java.net.InetAddress;
import java.net.InetSocketAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class U1_akClient implements Client {
	java.net.Socket socket;
	int port;
	String ip;
	
	public U1_akClient () {
		this.ip = "localhost";
		this.port = 24;
	}
	
	public U1_akClient (String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	//converts a string to an audioformat
	private AudioFormat getAudioFormat(String s)
	{
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        
        //TODO: read the audio format from the string
        //swimwater.wav: PCM_SIGNED 22050.0 Hz, 16 bit, mono, 2 bytes/frame, little-endian
        //test.wav: PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
        sampleRate = 44100.0F;
        sampleSizeBits = 16;
        channels = 2;
        signed = true;
        bigEndian = false;
        
        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }
	
	
	public void run() 
	{
		AudioFormat	audioFormat = null;
		
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

			InputStream in;
			DataInputStream dis;
			byte[] data;
			int len;
			
			
			//first get the audio format from the server
		 	BufferedReader bufferedReader =
		 	    new BufferedReader(
		 		new InputStreamReader(
		 	  	    socket.getInputStream()));
		 	char[] buffer = new char[200];
		 	int anzahlZeichen = bufferedReader.read(buffer, 0, 200); // blockiert bis Nachricht empfangen
		 	String nachricht = new String(buffer, 0, anzahlZeichen);
		 	
			audioFormat=getAudioFormat(nachricht);//interpretiere die nachricht in ein audio format
			
			AudioPlayer audioplay=new AudioPlayer(audioFormat);
			audioplay.start();
			
			//then receive the streaming
			while (true) {
				System.out.println("waiting for a package...");
				in = socket.getInputStream();
				dis = new DataInputStream(in);
				len = dis.readInt();
				data = new byte[len];
				if (len > 0) {
					dis.readFully(data);
				}
				audioplay.writeBytes(data); //play the music!
				System.out.println("received!");
				System.out.println();
				/*
				buffer = new char[10];
				anzahlZeichen = bufferedReader.read(buffer, 0 , 10);
				message = new String(buffer, 0, anzahlZeichen);
				System.out.println(message);
				*/
			}
			//System.out.println("Good bye!");
		} catch(IOException e) { }
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
