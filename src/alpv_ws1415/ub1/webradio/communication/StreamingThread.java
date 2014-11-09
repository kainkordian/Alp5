package alpv_ws1415.ub1.webradio.communication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

import com.google.protobuf.ByteString;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;
import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;

import java.util.ArrayList;

public class StreamingThread implements Runnable {
	private static final int	EXTERNAL_BUFFER_SIZE = 128000;
	
	ConnectionsThread cthread;
	
	AudioInputStream audioInputStream;
	ArrayList<java.net.Socket> clients;
	public akChatMessage lastMsg;
	public boolean newMsg;
	AudioFormat	audioformat;
	
	AudioPlayer audioplay;
	public File soundFile;
	boolean debugstuff;
	
	public boolean newSoundFile;
	
	boolean closeAll;
	
	public StreamingThread(ConnectionsThread cth, AudioInputStream as, AudioPlayer ap, File sf) {
		cthread=cth;
		closeAll=false;
		audioInputStream = as;
		audioplay = ap;
		soundFile = sf;
		clients = new ArrayList<java.net.Socket>();
		newMsg=false;
		newSoundFile=false;
	}
	
	public void close()
	{
		closeAll=true;
	}
	
	public ArrayList<java.net.Socket> getSocketClients() {
		return clients;
	}
	
	public void syncSocketClients(ArrayList<java.net.Socket> sc) {
		clients.clear();
		for(int i=0;i<sc.size();i++)
		{
			clients.add(sc.get(i));
		}
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
		
		
		while (nBytesRead != -1 && closeAll==false) {
			try	{
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (nBytesRead >= 0) {
				//audioplay.writeBytes(abData);

				//protobuf
				ByteString tempData=ByteString.copyFrom(abData);
				
				soundDataBuilder.clear();
				soundDataBuilder.setData(tempData);//audio data
				
				//add msg if there is one
				if(newMsg)
				{
					newMsg=false;
					soundDataBuilder.setPseudo(lastMsg.pseudo);
					soundDataBuilder.setMessage(lastMsg.message);
					//System.out.println("transmitting msg to client");
				}
				
				
				//write protobuf to outputstream
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
				
			}
			
			//end sound, loop
			if(nBytesRead == -1 || newSoundFile) {
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
				
				if(newSoundFile)
					informClientsNewSound();
				newSoundFile=false;
			}
		}
		System.out.println("Good bye!");
		audioplay.stop();
	}
	
	public void changeSong(String path)
	{
		soundFile=new File(path);
		newSoundFile=true;
	}
	public void informClientsNewSound()
	{

		audioformat= audioInputStream.getFormat();
		cthread.setAudioFormat(audioformat);
		
		PrintWriter printWriter;
		
		//protobuf usage: build audio format message
		SoundDataMessage.Builder audioFormatBuilder = SoundDataMessage.newBuilder();
		audioFormatBuilder.setFormatString(audioformat.toString());
		
		SoundDataMessage audioformatmsg = audioFormatBuilder.build();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    try {
	    	audioformatmsg.writeDelimitedTo(outStream);
	    	//audioformatmsg.writeTo(outStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try 
		{
			if(clients!=null && clients.size()>0) 
			{
				//send to each client
				for(int i=0; i < clients.size(); i++) 
				{
					//send audio format as string
					printWriter = new PrintWriter(new OutputStreamWriter(clients.get(i).getOutputStream()));
			 	 	printWriter.print(outStream);
			 	 	printWriter.flush();

				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
