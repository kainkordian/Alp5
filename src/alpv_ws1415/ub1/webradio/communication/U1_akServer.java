package alpv_ws1415.ub1.webradio.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.MalformedURLException;

import javax.sound.sampled.UnsupportedAudioFileException;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;
import alpv_ws1415.ub1.webradio.ui.akServerGUI;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.IOException;

public class U1_akServer implements Server{
	
	ServerSocket socket=null;
	int port;

	StreamingThread sJob;
	ChatThread chJob;
	ConnectionsThread cJob;
	
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

		akServerGUI serverGUI=new akServerGUI(this);
		serverGUI.run();
		
		
		
		//ini sound file and audioplayer
		String strFilename = "data/swimwater1.wav";
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
		cJob= new ConnectionsThread(audioFormat, socket, port);
		Thread cThread = new Thread(cJob);
		cThread.start();
		
		//launch streaming thread; stream music to different clients
		sJob= new StreamingThread(cJob, audioInputStream, audioplay, soundFile);
		Thread sThread = new Thread(sJob);
		sThread.start();
		
		//launch chat thread; listen to incoming chat message and send them to all
		chJob= new ChatThread();
		Thread chThread = new Thread(chJob);
		chThread.start();
		chJob.setStreamingThread(sJob);
		
		//sync threads
		while(true)
		{
			System.out.print("");
			
			//check if connectionThread got a new client
			if(cJob.getSocketClientsSize()!=sJob.getSocketClientsSize())
			//if(cJob.getSocketClientsSize()>0)
			{
				//if so, update client list in streamingThread
				sJob.syncSocketClients(cJob.getSocketClients());
				chJob.syncSocketClients(cJob.getSocketClients());
				System.out.println("socket client synced");
			}
		}
	}
	
	/**
	 * Close this server and free any resources associated with it.
	 */
	public void close() {
		try
		{ 
			if(cJob!=null)
				cJob.close();
			
			if(chJob!=null)
				chJob.close();
			
			if(sJob!=null)
				sJob.close();
			
			if(socket!=null)
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
		if(sJob!=null)
		{
			sJob.changeSong(path);
		}
	}
}
