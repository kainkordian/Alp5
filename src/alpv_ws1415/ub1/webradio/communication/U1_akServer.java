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
	
	public U1_akServer (){
		this.port = 7777;
	}
	
	public U1_akServer (int port){
		this.port = port;
	}
	
	/**
	 * Run a new server, which awaits a client and streams a .wav to it.
	 */
	public void run() 
	{
		//ini sound file and audioplayer
		String strFilename = "data/swimwater1.wav";
		File soundFile = new File(strFilename);
		
		AudioInputStream audioInputStream = null;
		
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		AudioFormat	audioFormat = audioInputStream.getFormat();
		AudioPlayer audioplay = new AudioPlayer(audioFormat);
				
		System.out.println("audio player ini");
		audioplay.start();

		System.out.print("The audio format is: ");
		System.out.println(audioFormat.toString());
		
		//launch connection thread; manage connecting clients
		ConnectionsThread cJob=new ConnectionsThread(audioFormat,socket,port);
		Thread cThread=new Thread(cJob);
		cThread.start();
		
		//launch streaming thread; stream music to different clients
		StreamingThread sJob=new StreamingThread(audioInputStream,audioplay,soundFile);
		Thread sThread = new Thread(sJob);
		sThread.start();
		
		//sync threads
		while(true)
		{
			/*System.out.print(cJob.getSocketClientsSize());
			System.out.print(":");
			System.out.println(sJob.getSocketClientsSize());*/
			
			//check if connectionThread got a new client
			//if(cJob.getSocketClientsSize()!=sJob.getSocketClientsSize())
			if(cJob.getSocketClientsSize()>0)
			{
				//if so, update client list in streamingThread
				sJob.syncSocketClients(cJob.getSocketClients());
				System.out.println("socket client synced");
			}
		}
		
		
		
		
		/*
		//accept connection
		try {
			socket = new ServerSocket(this.port);
			socketClient = socket.accept();
		} catch(IOException e) { }
							
		
		
		
		//firts send format to client
	 	PrintWriter printWriter;
		try 
		{
			printWriter = new PrintWriter(
			new OutputStreamWriter(
					socketClient.getOutputStream()));
	 	 	printWriter.print(audioFormat.toString());
	 	 	printWriter.flush();
		} catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		*/
		
		
		
		//then stream
		/*
		int count=0;
		
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		while (nBytesRead != -1) 
		{
			count+=1;
			try	{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				//audioplay.writeBytes(abData);
				
				//System.out.print(count);
				//System.out.print(":");
				//System.out.println(abData[count]);
				
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
			
			//end sound, loop
			if(nBytesRead==-1)
			{
				//setze alles auf null
				nBytesRead=0;
				abData = new byte[EXTERNAL_BUFFER_SIZE];
				count=0;
				audioplay.stop();
				
				//lade wieder das audio input stream
				try {
					audioInputStream = AudioSystem.getAudioInputStream(soundFile);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				//und starte neu
				audioplay.start();
			}
		}
		System.out.println("Stopping!");
		audioplay.stop();
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
