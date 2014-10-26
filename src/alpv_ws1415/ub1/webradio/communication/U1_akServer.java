package alpv_ws1415.ub1.webradio.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.MalformedURLException;

import javax.sound.sampled.UnsupportedAudioFileException;

public class U1_akServer 
{
	ServerSocket socket;
	int port  = 24;
	
	public void setAddress (int newPort){
		port = newPort;
	}
	
	/**
	 * Run a new server, which awaits a client and sends a message to it.
	 */
	public void run() {
		try
		{
			socket = new ServerSocket(port);
			java.net.Socket socketClient = socket.accept();
						 
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()));
			for (int i = 0; i < 10; i++) {
				printWriter.println("\"test\"");
				printWriter.flush();
			}
				 	
		}
		catch(IOException e)
		{
			
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
