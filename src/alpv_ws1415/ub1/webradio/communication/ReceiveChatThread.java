package alpv_ws1415.ub1.webradio.communication;

import java.io.IOException;
import java.util.ArrayList;

import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.ChatMessage;


//CLIENT SIDE: receive message from server
public class ReceiveChatThread implements Runnable {

	java.net.Socket socket;
	ArrayList<akChatMessage> chatmsg;
	
	
	public ReceiveChatThread(ArrayList<akChatMessage> c, java.net.Socket s)
	{
		chatmsg=c;
		socket=s;
	}
	
	public ArrayList<akChatMessage> getChatMessages()
	{
		return chatmsg;
	}
	
	@Override
	public void run() {
		try
		{
			while (true) 
			{
				
				ChatMessage chatMessage = ChatMessage.parseDelimitedFrom(socket.getInputStream());

				chatmsg.add(new akChatMessage(chatMessage.getPseudo(),chatMessage.getMessage()));
				
				System.out.println("Message received: "+chatmsg.get(chatmsg.size()-1).toString());
				
			}
		} 
		catch(IOException e) 
		{ }
	}

}
