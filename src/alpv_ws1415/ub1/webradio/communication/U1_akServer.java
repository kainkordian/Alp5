package alpv_ws1415.ub1.webradio.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.MalformedURLException;
import java.io.BufferedReader;

import javax.sound.sampled.UnsupportedAudioFileException;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class U1_akServer {
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	ServerSocket socket;
	int port;
	
	public U1_akServer (){
		this.port = 24;
	}
	
	public U1_akServer (int port){
		this.port = port;
	}
	
	/**
	 * Run a new server, which awaits a client and sends a message to it.
	 */
	public void run() {
		//ini sound file and audioplayer
		String strFilename = "data/test.wav";
		File soundFile = new File(strFilename);
		
		AudioInputStream audioInputStream = null;
		
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		AudioFormat	audioFormat = audioInputStream.getFormat();
		AudioPlayer audioplay = new AudioPlayer(audioFormat);
				
		//play sound and stream
		System.out.println("audio player ini");
		audioplay.start();
		
		//send sound data
		java.net.Socket socketClient = null;
		try {
			//ini socket
			socket = new ServerSocket(this.port);
			socketClient = socket.accept();
		} catch(IOException e) { }
							
		int count=0;
		//stream
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1) {
			count+=1;
			try	{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				//audioplay.writeBytes(abData);
				System.out.print(count);
				System.out.print(":");
				System.out.println(abData[count]);
				//send sound data
				try {
				    OutputStream out = socketClient.getOutputStream(); 
				    DataOutputStream dos = new DataOutputStream(out);
				    int len= abData.length;
				    dos.writeInt(len);
			        if (len > 0) {
			        	dos.write(abData, 0, abData.length);
					}
				} catch(IOException e) { }
			}
		}
		audioplay.stop();	
		/*
		try {
			socket = new ServerSocket(port);
			java.net.Socket socketClient = socket.accept();
			
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()));
			for (int i = 0; i < 10; i++) {
				printWriter.println("\"test\"");
				printWriter.flush();
			}	 	
		}
		catch(IOException e) { }
		*/
	}
	
	/**
	 * Close this server and free any resources associated with it.
	 */
	public void close() {
		try
		{
			socket.close();
		}
		catch(IOException e)
		{
			
		}
	}

	/**
	 * Change the currently played song in case the stream has already started.
	 * Start streaming, otherwise.
	 * 
	 * @param path Relative path to a sound-file in the file-system.
	 * @throws MalformedURLException
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 */
	public void playSong(String path) throws MalformedURLException, UnsupportedAudioFileException, IOException {
		
	}
}
