package alpv_ws1415.ub1.webradio.communication;


import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.AudioFormatMessage;
import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.ChatMessage;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class U1_akClient implements Client {
	java.net.Socket socket;
	int port;
	String ip;
	ArrayList<akChatMessage> chatmsg;
	
	
	public U1_akClient () {
		this.ip = "192.168.178.86";
		this.ip = "localhost";
		this.port = 7777;
		
		chatmsg = new ArrayList<akChatMessage>();
	}
	
	public U1_akClient (String ip, int port) {
		this.ip = ip;
		this.port = port;

		chatmsg = new ArrayList<akChatMessage>();
	}
	
	//converts a string to an audioformat
	private AudioFormat getAudioFormat(String s)
	{
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        
        //TODO: read the audio format from the string
        
        //examples:
        //swimwater1.wav: PCM_SIGNED 22050.0 Hz, 16 bit, mono, 2 bytes/frame, little-endian
        //test.wav: PCM_SIGNED 44100.0 Hz, 16 bit, stereo, 4 bytes/frame, little-endian
        
        //store different aspects in differents strings
        String lastValue="";
        String sampleRateString="";
        String sampleSizeBitsString="";
        String channelsString="";
        String signedString="";
        String bigEndianString="";
        
        for (char ch : s.toCharArray())
        {
            //System.out.print(ch);
            
            if(ch!=',')//komma= nächste wert
            {
            	if(ch!=' ')
            		lastValue+=ch;
            }
            else
            {
            	if(sampleRateString.isEmpty()) sampleRateString=lastValue;
            	else if(sampleSizeBitsString.isEmpty()) sampleSizeBitsString=lastValue;
            	else if(channelsString.isEmpty()) channelsString=lastValue;
            	else if(signedString.isEmpty()) signedString=lastValue;
            	
            	lastValue="";
            }
        }
        if(bigEndianString.isEmpty()) bigEndianString=lastValue;

        //interpret those strings

        //sampleRate
        String temp="";
        for (char ch : sampleRateString.toCharArray())
        {
            if((ch>='0' && ch<='9'))
            {
            	temp+=ch;
            }
            else if(ch=='.')
            	break;
        }
        sampleRate=(float)Integer.parseInt(temp);

        //sampleSizeBits
        temp="";
        for (char ch : sampleSizeBitsString.toCharArray())
        {
            if(ch>='0' && ch<='9')
            {
            	temp+=ch;
            }
        }
        sampleSizeBits=Integer.parseInt(temp);

        //channels
        if(channelsString.contains("mono"))
        	channels=1;
        else if(channelsString.contains("stereo"))
        	channels=2;
        
        //signed
        signed=true;
        
        //bigEndian
        if(bigEndianString.contains("little-endian"))
        	bigEndian=false;
        else bigEndian=true;
        
        
        /*System.out.println();
        System.out.println(sampleRate);
        System.out.println(sampleSizeBits);
        System.out.println(channels);
        System.out.println(signed);
        System.out.println(bigEndian);*/
        
        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }
	
	
	
	
	
	public void run() 
	{
		AudioFormat	audioFormat = null;
		
		//connecting
		InetSocketAddress sockAdr = new InetSocketAddress(ip, port);
		
		try
		{
			System.out.println("connecting...");
			connect(sockAdr);
			
			
			//System.out.println("connected!");
			
			//protobuf audio format message
		    AudioFormatMessage audioformatmessage = 
		    		AudioFormatMessage.parseDelimitedFrom(socket.getInputStream());

			//interpretiere die nachricht in ein audio format
			audioFormat=getAudioFormat(audioformatmessage.getFormatString());
		    

			//sendChatMessageToServer("hi server");
			
			//start audio player
			AudioPlayer audioplay=new AudioPlayer(audioFormat);
			audioplay.start();

			
			//launch receive audio thread; get audio data and play it
			ReceiveAudioThread raJob = new ReceiveAudioThread(audioplay,socket);
			Thread raThread = new Thread(raJob);
			raThread.start();

			//launch receive chat thread; get incoming chat messages
			ReceiveChatThread rcJob = new ReceiveChatThread(chatmsg,socket);
			Thread rcThread = new Thread(rcJob);
			//rcThread.start();
			

		} catch(IOException e) { }
	}
	
	
	public void sendChatMessageToServer(String m)
	{

		ChatMessage.Builder chatMessageBuilder = ChatMessage.newBuilder();
		PrintWriter printWriter;
					
		chatMessageBuilder.setPseudo("clientPseudo");
		chatMessageBuilder.setMessage(m);
		
		ChatMessage chatmessage = chatMessageBuilder.build();

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    try {
	    	chatmessage.writeDelimitedTo(outStream);

			printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	 	 	printWriter.print(outStream);
	 	 	printWriter.flush();
	 	 	
	 	 	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
	}

}
