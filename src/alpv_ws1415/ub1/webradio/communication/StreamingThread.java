package alpv_ws1415.ub1.webradio.communication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

import com.google.protobuf.ByteString;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;
import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;

import java.util.ArrayList;

public class StreamingThread implements Runnable {
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	
	AudioInputStream audioInputStream;
	ArrayList<java.net.Socket> clients;
	AudioPlayer audioplay;
	File soundFile;
	
	public StreamingThread(AudioInputStream as, AudioPlayer ap, File sf) {
		audioInputStream = as;
		audioplay = ap;
		soundFile = sf;
		clients = null;
	}
	
	public ArrayList<java.net.Socket> getSocketClients() {
		return clients;
	}
	
	public void syncSocketClients(ArrayList<java.net.Socket> sc) {
		clients=sc;
	}
	
	public int getSocketClientsSize() {
		if(clients==null) return 0;
		return clients.size();
	}
	
	
	public void run() {
		System.out.println("Streaming...");

		SoundDataMessage.Builder soundDataBuilder = SoundDataMessage.newBuilder();
		
		
		
		//stream
		int	nBytesRead = 0;
		byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
		
		
		while (nBytesRead != -1) {
			try	{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				//audioplay.writeBytes(abData);

				//protobuf usage: build sound data message
				ByteString tempData=ByteString.copyFrom(abData);
				soundDataBuilder.setData(tempData);
				
				SoundDataMessage sounddatatmsg = soundDataBuilder.build();

				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			    try {
			    	sounddatatmsg.writeDelimitedTo(outStream);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			    
			    //send to all
				PrintWriter printWriter;
				try {
					if(clients!=null && clients.size()>0) 
					{
						//send to each client
						for(int i=0; i < clients.size(); i++) 
						{
							printWriter = new PrintWriter(new OutputStreamWriter(clients.get(i).getOutputStream()));
					 	 	printWriter.print(outStream);
					 	 	printWriter.flush();
						}
					}
				} catch(IOException e) { }
				
				
				
				/*try {
					if(clients!=null && clients.size()>0) {
						//send to each client
						for(int i=0; i < clients.size(); i++) {
						    OutputStream out = clients.get(i).getOutputStream(); 
						    DataOutputStream dos = new DataOutputStream(out);
						    int len = abData.length;
						    dos.writeInt(len);
					        if (len > 0) {
					        	dos.write(abData, 0, abData.length);
							}
						}
					}
				} catch(IOException e) { }*/
			}
			
			//end sound, loop
			if(nBytesRead == -1) {
				//reset
				nBytesRead = 0;
				abData = new byte[EXTERNAL_BUFFER_SIZE];
				audioplay.stop();
				
				//reload the music
				try {
					audioInputStream = AudioSystem.getAudioInputStream(soundFile);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
				//start over
				audioplay.start();
			}
		}
		System.out.println("Good bye!");
		audioplay.stop();
	}
	
}
