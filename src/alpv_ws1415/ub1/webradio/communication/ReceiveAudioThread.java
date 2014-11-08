package alpv_ws1415.ub1.webradio.communication;

import java.io.IOException;


import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;
import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;

public class ReceiveAudioThread implements Runnable {

	java.net.Socket socket;
	AudioPlayer audioplay;

	public ReceiveAudioThread(AudioPlayer a, java.net.Socket s) 
	{
		audioplay=a;
		socket=s;
	}
	
	@Override
	public void run() 
	{
		//receive the streaming
		try
		{
			while (true) 
			{
				
				SoundDataMessage soundDataMessage = SoundDataMessage.parseDelimitedFrom(socket.getInputStream());
				
				audioplay.writeBytes(soundDataMessage.getData().toByteArray()); //play the music!
				
			}
		} 
		catch(IOException e) 
		{ }
	}

}
