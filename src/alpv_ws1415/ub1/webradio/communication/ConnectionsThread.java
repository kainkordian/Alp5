package alpv_ws1415.ub1.webradio.communication;

import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.AudioFormatMessage;

import java.io.ByteArrayOutputStream;

import com.google.protobuf.ByteString;



import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

public class ConnectionsThread implements Runnable {
	AudioFormat audioformat;
	ServerSocket socket;
	ArrayList<java.net.Socket> clients;
	int port;
	public int testyo;
		
	public ConnectionsThread(AudioFormat a, ServerSocket s, int p) {
		super();
		audioformat = a;
		socket = s;
		port = p;
		clients = new ArrayList<java.net.Socket>();
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
		AudioFormatMessage.Builder audioFormatBuilder = AudioFormatMessage.newBuilder();
		audioFormatBuilder.setFormatString(audioformat.toString());
		audioFormatBuilder.setTestString("bonsoir");
		
		AudioFormatMessage audioformatmsg = audioFormatBuilder.build();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    try {
	    	audioformatmsg.writeDelimitedTo(outStream);
	    	//audioformatmsg.writeTo(outStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("waiting for client...");
		
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		java.net.Socket socketClient;
		try 
		{
			//wait for connections
			while(true) 
			{
				socketClient = socket.accept();
				clients.add(socketClient);
				//send audio format as string
				//printWriter = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()));
				printWriter = new PrintWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		 	 	printWriter.print(outStream);
		 	 	printWriter.flush();

				//audioFormatBuilder.writeTo(socketClient.getOutputStream());
		 	 	

				//System.out.println("format sent");
			}	
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
