package alpv_ws1415.ub1.webradio.communication;


import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;
import alpv_ws1415.ub1.webradio.ui.akClientGUI;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class U1_akClient implements Client {
	akClientGUI clientGUI;
	java.net.Socket socket;
	int port;
	String ip;
	ArrayList<akChatMessage> chatmsg;
	
	boolean closeAll;
	ReceiveDataThread rdJob=null;
	
	
	public U1_akClient () {
		closeAll=false;
		this.ip = "192.168.178.86";
		this.ip = "localhost";
		this.port = 7777;
		
		chatmsg = new ArrayList<akChatMessage>();
	}
	
	public U1_akClient (String ip, int port) {
		closeAll=false;
		this.ip = ip;
		this.port = port;

		chatmsg = new ArrayList<akChatMessage>();
	}
	
	
	
	
	
	public void run() 
	{
		clientGUI=new akClientGUI(this);
		clientGUI.run();
		
		
		AudioFormat	audioFormat = null;
		
		//connecting
		InetSocketAddress sockAdr = new InetSocketAddress(ip, port);
		
		try
		{
			System.out.println("connecting...");
			connect(sockAdr);
			
			
			//launch receive audio thread; get audio data and play it
			rdJob = new ReceiveDataThread(this,socket,chatmsg);
			Thread rdThread;
			rdThread= new Thread(rdJob);
			rdThread.start();
			
			

		} catch(IOException e) { }
	}
	
	
	public void connect(InetSocketAddress serverAddress) throws IOException
	{
		socket = new java.net.Socket(serverAddress.getAddress(), serverAddress.getPort());
	}

	/**
	 * Close this client and free any resources associated with it.
	 */
	public void close()
	{
		closeAll=true;
		
		if(rdJob!=null)
			rdJob.close();
	}

	/**
	 * Send a chat message. Adding this clients user-name to the message as well
	 * as packing it may be implementation-dependent.
	 * 
	 * @param message A message to send.
	 * @throws IOException
	 */
	public void sendChatMessage(String message) throws IOException
	{

		SoundDataMessage.Builder chatMessageBuilder = SoundDataMessage.newBuilder();
		PrintWriter printWriter;
					
		chatMessageBuilder.setMessage(message);
		
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
	
	
	public void newMsg(String m)
	{
		clientGUI.pushChatMessage(m+"\n");
	}

}
