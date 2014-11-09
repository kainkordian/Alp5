package alpv_ws1415.ub1.webradio.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

import alpv_ws1415.ub1.webradio.audioplayer.AudioPlayer;
import alpv_ws1415.ub1.webradio.protobuf.PacketProtos.SoundDataMessage;

public class ReceiveDataThread implements Runnable {

	U1_akClient akClient;
	java.net.Socket socket;
	AudioPlayer audioplay=null;
	ArrayList<akChatMessage> chatmsg;
	boolean closeAll;

	public ReceiveDataThread(U1_akClient cc,java.net.Socket s, ArrayList<akChatMessage> c) 
	{
		akClient=cc;
		closeAll=false;
		chatmsg=c;
		socket=s;
	}
	
	public ArrayList<akChatMessage> getChatMessages()
	{
		return chatmsg;
	}
	
	public void close()
	{
		closeAll=true;
	}
	
	public void newMsg(String m)
	{
		akClient.newMsg(m);
	}
	
	
	@Override
	public void run() 
	{
		//receive incoming data
		try
		{
			while (closeAll==false) 
			{
				
				SoundDataMessage soundDataMessage = SoundDataMessage.parseDelimitedFrom(socket.getInputStream());

				//format sound
				if(soundDataMessage.hasFormatString())
				{
					AudioFormat audioFormat=getAudioFormat(soundDataMessage.getFormatString());
					
					//start audio player
					audioplay=new AudioPlayer(audioFormat);
					audioplay.start();
				}
				
				//sound stream
				if(soundDataMessage.hasData())
				{
					audioplay.writeBytes(soundDataMessage.getData().toByteArray()); //play the music!
					//System.out.println("Music received");
				}
				
				//chat message
				if(soundDataMessage.hasMessage())
				{
					newMsg(soundDataMessage.getMessage());
				}
			}
		} 
		catch(IOException e) 
		{ }
	}

	//converts a string to an audioformat
	private AudioFormat getAudioFormat(String s)
	{
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        
        
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
	
}
