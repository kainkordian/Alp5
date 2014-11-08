package alpv_ws1415.ub1.webradio.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.ChatMessage;


//SERVER SIDE: listen to incoming chat message and send them to all
public class ChatThread implements Runnable {

	ArrayList<java.net.Socket> clients;
	ArrayList<akChatMessage> chatmsg;
	
	public ChatThread()
	{
		clients=null;
		chatmsg = new ArrayList<akChatMessage>();
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
	
	@Override
	public void run() {

		ChatMessage.Builder chatMessageBuilder = ChatMessage.newBuilder();
		PrintWriter printWriter;
		
		
		try
		{
			while (true) 
			{
				int clientISent=0;
				//check if any client has sent a message
				//in this case we wait for each client after another to send a message.
				//how can we check for all of them at the same time?
				//one thread for each?...
				if(clients!=null && clients.size()>0) 
				{
					for(int i=0; i < clients.size(); i++) 
					{
						ChatMessage chatMessage = ChatMessage.parseDelimitedFrom(clients.get(i).getInputStream());
		
						chatmsg.add(new akChatMessage(chatMessage.getPseudo(),chatMessage.getMessage()));

						System.out.println("(server)Message received: "+chatmsg.get(chatmsg.size()-1).toString());
						
						clientISent=i;
						
					}
					
					
					//send it to all
					if(chatmsg.size()>0)
					{
						String pseudo=chatmsg.get(chatmsg.size()-1).message;
						String msg=chatmsg.get(chatmsg.size()-1).pseudo;
						
						chatMessageBuilder.setPseudo(pseudo);
						chatMessageBuilder.setMessage(msg);
						
						ChatMessage chatmessage = chatMessageBuilder.build();
		
						
						ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					    try {
					    	chatmessage.writeDelimitedTo(outStream);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					    
						//send to each client
						for(int i=0; i < clients.size(); i++) 
						{
							if(i!=clientISent || true)//dont send to client that sent the msg
							{
								printWriter = new PrintWriter(new OutputStreamWriter(clients.get(i).getOutputStream()));
						 	 	printWriter.print(outStream);
						 	 	printWriter.flush();
							}
						}
					}
				}
			}
		} 
		catch(IOException e) 
		{ }
		
	}

}
