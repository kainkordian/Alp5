package alpv_ws1415.ub1.webradio.communication;

import java.io.IOException;
import java.util.ArrayList;

import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;


//SERVER SIDE: listen to incoming chat message and send them to all
public class ChatThread implements Runnable {

	ArrayList<java.net.Socket> clients;
	ArrayList<akChatMessage> chatmsg;
	boolean debugstuff;
	StreamingThread st;
	
	public ChatThread()
	{
		clients=null;
		chatmsg = new ArrayList<akChatMessage>();
		clients = new ArrayList<java.net.Socket>();
		debugstuff=false;
	}
	
	public void setStreamingThread(StreamingThread s)
	{
		st=s;
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
		debugstuff=true;
	}
	
	public int getSocketClientsSize() {
		if(clients==null) return 0;
		return clients.size();
	}
	
	@Override
	public void run() {

		//chatmsg.add(new akChatMessage("wixxer","fuk u"));
		//System.out.println("running chat thread");

		@SuppressWarnings("unused")
		int clientISent=0;
		try
		{
			while (true) 
			{
				//check if any client has sent a message
				//in this case we wait for each client after another to send a message.
				//how can we check for all of them at the same time?
				//one thread for each?...
				//System.out.print("scum:");
				
				if(debugstuff)
				{
					System.out.print(clients.size());
					debugstuff=false;
				}
				
				if(clients.size()>0) 
				{
					clientISent=-1;
					//System.out.println("trying chat thread");
					for(int i=0; i < clients.size(); i++) 
					{
						SoundDataMessage chatMessage = SoundDataMessage.parseDelimitedFrom(clients.get(i).getInputStream());
						
						if(chatMessage.hasMessage())
						{
							chatmsg.add(new akChatMessage(chatMessage.getPseudo(),chatMessage.getMessage()));
	
							//System.out.println("(server)Message received: "+chatmsg.get(chatmsg.size()-1).toString());
							
							clientISent=i;
							
							//update other stream to allow it to send the msg to other clients
							st.newMsg=true;
							st.lastMsg=chatmsg.get(chatmsg.size()-1);
						}
						
					}
					
					
				}
			}
		} 
		catch(IOException e) 
		{ }
		
	}

}
