package alpv_ws1415.ub1.webradio.communication;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

import java.util.ArrayList;
import java.util.List;




public class StreamingThread implements Runnable
{
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	
	AudioInputStream audioInputStream;
	ArrayList<java.net.Socket> clients;
	AudioPlayer audioplay;
	File soundFile;
	
	public StreamingThread(AudioInputStream as, AudioPlayer ap,File sf)
	{
		audioInputStream=as;
		audioplay=ap;
		soundFile=sf;
		clients=null;
	}
	public ArrayList<java.net.Socket> getSocketClients()
	{
		return clients;
	}
	public void syncSocketClients(ArrayList<java.net.Socket> sc)
	{
		clients=sc;
	}
	public int getSocketClientsSize()
	{
		if(clients==null) return 0;
		return clients.size();
	}
	
	
	public void run()
	{

		//then stream
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
				
				/*System.out.print(count);
				System.out.print(":");
				System.out.println(abData[count]);*/
				
				//send sound data
				try {
					if(clients!=null && clients.size()>0)
					{
						System.out.print(count);
						System.out.print(":");
						System.out.println(abData[count]);
						
						//send to each client
						for(int i=0;i<clients.size();i++)
						{
						    OutputStream out = clients.get(i).getOutputStream(); 
						    DataOutputStream dos = new DataOutputStream(out);
						    int len= abData.length;
						    dos.writeInt(len);
					        if (len > 0) {
					        	dos.write(abData, 0, abData.length);
							}
						}
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
	}
	
}
