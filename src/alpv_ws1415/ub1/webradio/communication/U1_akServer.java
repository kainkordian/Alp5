package alpv_ws1415.ub1.webradio.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.MalformedURLException;
import java.io.BufferedReader;

import javax.sound.sampled.UnsupportedAudioFileException;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class U1_akServer implements Server{
	
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	ServerSocket socket;
	int port;
	
	
	//default port is 24
	public U1_akServer (){
		this.port = 7777;
	}
	
	//set the server port
	public U1_akServer (int port){
		this.port = port;
	}
	
	/**
	 * Runnable for the server.
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
				
		System.out.println("Initiating audio player...");
		audioplay.start();

		System.out.print("Done. The audio format is: ");
		System.out.println(audioFormat.toString());
		
		//launch connection thread; manage connecting clients
		ConnectionsThread cJob = new ConnectionsThread(audioFormat, socket, port);
		Thread cThread = new Thread(cJob);
		cThread.start();
		
		//launch streaming thread; stream music to different clients
		StreamingThread sJob = new StreamingThread(audioInputStream, audioplay, soundFile);
		Thread sThread = new Thread(sJob);
		sThread.start();
		
		//sync threads
		while(true)
		{
			System.out.print("");
			
			//check if connectionThread got a new client
			//if(cJob.getSocketClientsSize()!=sJob.getSocketClientsSize())
			if(cJob.getSocketClientsSize()>0)
			{
				//if so, update client list in streamingThread
				sJob.syncSocketClients(cJob.getSocketClients());
				//System.out.println("socket client synced");
			}
		}
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
