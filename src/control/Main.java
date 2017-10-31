package control;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import model.State;




public class Main {
	
	static boolean stop = false;
	static State piState;
	
	// should start on the launch of the pi
	public static void main(String[] args) {
		System.out.println("starting prog");
		
		piState = new State();

		//the very big loop
		while(!stop){
		
			try {
				aConnectionLoop();
			}catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
		System.out.println("the end");
	}
	
	
	
	public static void aConnectionLoop() throws IOException{
		boolean stoploop = false;
		ServerSocket socketserver  ;
		Socket socketduserveur ;
        BufferedReader in;
        PrintWriter out;
		socketserver = new ServerSocket(20009);
		socketduserveur = socketserver.accept(); 
		System.out.println("connection!");
		
        in = new BufferedReader (new InputStreamReader (socketduserveur.getInputStream()));
		out = new PrintWriter(socketduserveur.getOutputStream());
		

        out.println("pi listening!");
        out.flush();
        
        while(!stoploop){
            String command = in.readLine();
            System.out.println(command);
            
            if(command == null){ break; }
            
            switch (command) {
            case "startWIFI":
            	
            	//TODO
            	
                String ssid = in.readLine();
                State.wifiSSID = ssid;
                String pass = in.readLine();
                State.wifiPass = pass;
                
                break;
            case "startIPSEC":
            	//TODO: sudo and so on
            	String[] cmdArray = {"ipsec", "restart"};
            	executeCommandSH(cmdArray);

            	String[] cmdArray2 = {"ipsec", "up", "home"};
            	executeCommandSH(cmdArray2);
            	break;
            case "getWIFI":
            	State.refrWifi();
                out.println("WIFI"+State.connectedWifi);
                out.flush();
            	break;
            case "getWEB":
            	State.isInternetReachable();
                out.println("WEB"+State.connectedWeb);
                System.out.println("WEB"+State.connectedWeb);
                out.flush();
                break;
            case "getIPSEC":
            	State.refrIPSEC();
                out.println("IPSEC"+State.connectedIPSEC);
                out.flush();
                break;
                
            case "Sunday":
                break;
            default:
            
        }
            
        }
        
        
        
        

	    socketserver.close();
        socketduserveur.close();
	}
	
	
	public static boolean executeCommandSH(String[] cmdArray){
		//String[] cmdArray = new String[1];
        // first argument is the program we want to open
        //cmdArray[0] = a;
        // second argument is a txt file we want to open with notepad
        //cmdArray[1] = b;
        //cmdArray[2] = c;
        try {
			Process process = Runtime.getRuntime().exec(cmdArray,null);
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	
	
	public static void connectToWifi(){		//TODO: boolean?
		
		//TODO
		
	}
	
	
	

}
