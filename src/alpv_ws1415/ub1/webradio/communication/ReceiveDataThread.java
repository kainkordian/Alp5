package alpv_ws1415.ub1.webradio.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;
import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;

public class ReceiveDataThread implements Runnable {

	java.net.Socket socket;
	AudioPlayer audioplay;
	ArrayList<akChatMessage> chatmsg;

	public ReceiveDataThread(AudioPlayer a, java.net.Socket s, ArrayList<akChatMessage> c) 
	{
		chatmsg=c;
		audioplay=a;
		socket=s;
	}
	public ArrayList<akChatMessage> getChatMessages()
	{
		return chatmsg;
	}
	
	@Override
	public void run() 
	{
		//receive incoming data
		try
		{
			while (true) 
			{
				
				SoundDataMessage soundDataMessage = SoundDataMessage.parseDelimitedFrom(socket.getInputStream());
				
				//sound stream
				if(soundDataMessage.hasData())
				{
					audioplay.writeBytes(soundDataMessage.getData().toByteArray()); //play the music!
					//System.out.println("Music received");
					sendChatMessageToServer("received sound data");
				}
				
				//chat message
				if(soundDataMessage.hasMessage())
				{
					chatmsg.add(new akChatMessage(soundDataMessage.getPseudo(),soundDataMessage.getMessage()));
					
					System.out.println("Message received: "+chatmsg.get(chatmsg.size()-1).toString());
				}
			}
		} 
		catch(IOException e) 
		{ }
	}
	

	public void sendChatMessageToServer(String m)
	{

		SoundDataMessage.Builder chatMessageBuilder = SoundDataMessage.newBuilder();
		PrintWriter printWriter;
					
		chatMessageBuilder.setPseudo("clientPseudo");
		chatMessageBuilder.setMessage(m);
		
		SoundDataMessage chatmessage = chatMessageBuilder.build();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    try {
	    	chatmessage.writeDelimitedTo(outStream);

			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	 	 	printWriter.print(outStream);
	 	 	printWriter.flush();
	 	 	
	 	 	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
