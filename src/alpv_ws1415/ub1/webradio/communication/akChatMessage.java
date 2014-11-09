package alpv_ws1415.ub1.webradio.communication;

public class akChatMessage 
{
	public int timestamp;
	public String pseudo;
	public String message;
	
	public akChatMessage(String p, String m)
	{
		pseudo=p;
		message=m;
	}
	
	public String toString()
	{
		return message;
		//return "["+pseudo+"]: "+message;
	}
}
