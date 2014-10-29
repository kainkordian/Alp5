package alpv_ws1415.ub1.webradio.communication;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;


public class ConnectionsThread implements Runnable
{
	AudioFormat audioformat;
	ServerSocket socket;
	ArrayList<java.net.Socket> clients;
	int port;
	public int testyo;
	
	
	public ConnectionsThread(AudioFormat a, ServerSocket s, int p)
	{
		super();
		audioformat=a;
		socket=s;
		port=p;
		clients=new ArrayList<java.net.Socket>();
	}

	public ArrayList<java.net.Socket> getSocketClients()
	{
		return clients;
	}
	public int getSocketClientsSize()
	{
		if(clients==null) return 0;
		return clients.size();
	}
	
	public void run()
	{

		//create socket server
		try 
		{
			socket = new ServerSocket(this.port);
		} catch(IOException e)
		{
			
		}

		
		//wait for connections
	 	 PrintWriter printWriter;
		while(true)
		{
			try 
			{
				java.net.Socket socketClient = socket.accept();
				clients.add(socketClient);
				
				//send audio format as string
				printWriter = new PrintWriter(
				new OutputStreamWriter(
						socketClient.getOutputStream()));
		 	 	printWriter.print(audioformat.toString());
		 	 	printWriter.flush();
			}
			catch(IOException e) 
			{
				e.printStackTrace();
			}
			
			
		}
		
	}

}
