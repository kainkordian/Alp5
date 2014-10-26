package alpv_ws1415.ub1.webradio.webradio;
import alpv_ws1415.ub1.webradio.communication.U1_akClient;
import alpv_ws1415.ub1.webradio.communication.U1_akServer;

public class Main {
	private static final String	USAGE	= String.format("usage: java -jar UB%%X_%%NAMEN [-options] server tcp|udp|mc PORT%n" +
														"         (to start a server)%n" +
														"or:    java -jar UB%%X_%%NAMEN [-options] client tcp|udp|mc SERVERIPADDRESS SERVERPORT USERNAME%n" +
														"         (to start a client)");

	/**
	 * Starts a server/client according to the given arguments, using a GUI or
	 * just the command-line according to the given arguments.
	 * 
	 * @param args
	 */
	
	
	//maul du schmuck
	
	
	
	
	
	
	
	public static void main(String[] args) 
	{
		try 
		{
			boolean useGUI = false;
			int i = -1;

			// Parse options. Add additional options here if you have to. Do not
			// forget to mention their usage in the help-string!
			while(args[++i].startsWith("-")) 
			{
				if(args[i].equals("-help")) 
				{
					System.out.println(USAGE + String.format("%n%nwhere options include:"));
					System.out.println("  -help      Show this text.");
					System.out.println("  -gui       Show a graphical user interface.");
					System.exit(0);
				}
				else if(args[i].equals("-gui")) 
				{
					useGUI = true;
				}
			}

			if(args[i].equals("server")) {
				System.out.println("server ini");
				U1_akServer server;
				
				if (args[i+1] != null) {
					server = new U1_akServer(Integer.parseInt(args[i+1]));
				} else { server  = new U1_akServer();}
				server.run();
			}
			else if(args[i].equals("client")) {
				System.out.println("client ini");
				
				U1_akClient client;
				if (args[i+1] != null && args[i+2] != null) {
					client  = new U1_akClient(args[i+1], Integer.parseInt(args[i+2]));
				} else { client  = new U1_akClient(); }
				client.run();
			}
			else
				throw new IllegalArgumentException();
		}
		catch(ArrayIndexOutOfBoundsException e) 
		{
			System.err.println(USAGE);
		}
		catch(NumberFormatException e) 
		{
			System.err.println(USAGE);
		}
		catch(IllegalArgumentException e) 
		{
			System.err.println(USAGE);
		}
	}
}
