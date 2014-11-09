package alpv_ws1415.ub1.webradio.communication;


import java.io.ByteArrayOutputStream;



import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;

public class ConnectionsThread implements Runnable {
	AudioFormat audioformat;
	ServerSocket socket;
	ArrayList<java.net.Socket> clients;
	int port;
	public int testyo;
	boolean closeAll;
	SoundDataMessage audioformatmsg;
	ByteArrayOutputStream outStream;
		
	public ConnectionsThread(AudioFormat a, ServerSocket s, int p) {
		super();
		audioformat = a;
		socket = s;
		port = p;
		clients = new ArrayList<java.net.Socket>();
	}
	
	public void setAudioFormat(AudioFormat a)
	{
		audioformat=a;
		SoundDataMessage.Builder audioFormatBuilder = SoundDataMessage.newBuilder();
		audioFormatBuilder.setFormatString(audioformat.toString());

		audioformatmsg = audioFormatBuilder.build();

		outStream = new ByteArrayOutputStream();
	    try {
	    	audioformatmsg.writeDelimitedTo(outStream);
	    	//audioformatmsg.writeTo(outStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void close()
	{
		closeAll=true;
	}
	public ArrayList<java.net.Socket> getSocketClients() {
		return clients;
	}
	
	public int getSocketClientsSize() {
		if(clients==null) return 0;
		return clients.size();
	}
	
	public void run() {
		//create server socket
		try {
			socket = new ServerSocket(this.port);
		} catch(IOException e) { }
		
		PrintWriter printWriter;
		
		//protobuf usage: build audio format message
		SoundDataMessage.Builder audioFormatBuilder = SoundDataMessage.newBuilder();
		audioFormatBuilder.setFormatString(audioformat.toString());
		
		audioformatmsg = audioFormatBuilder.build();

		outStream = new ByteArrayOutputStream();
	    try {
	    	audioformatmsg.writeDelimitedTo(outStream);
	    	//audioformatmsg.writeTo(outStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("waiting for client...");
		
		java.net.Socket socketClient;
		try 
		{
			//wait for connections
			while(closeAll==false) 
			{
				socketClient = socket.accept();
				clients.add(socketClient);
				//send audio format as string
				printWriter = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		 	 	printWriter.print(outStream);
		 	 	printWriter.flush();

				//System.out.println("New client!");
			}	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
