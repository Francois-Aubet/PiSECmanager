package control;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import model.State;




public class Main {
	
	static boolean stop = false;
	static State piState;
	
	// should start on the launch of the pi
	public static void main(String[] args) {
		System.out.println("starting prog");
		
		piState = new State();
		
		//start all things and set it up:
		//ap
		executeCommandSH("hostapd /etc/hostapd/hostapd.conf");
		executeCommandSH("systemctl start isc-dhcp-server");
		executeCommandSH("ip a add 10.0.1.1 dev wlan0");
		
		//TODO: uncomment
		//executeCommandSH("service networking restart");

		//test before start:
/*		State.wifiPass = "thebestever";
		State.wifiSSID = "theotherone";
		connectToWifi();
*/
		
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
            	State.refrWifi();
                if(!State.connectedWifi){
                	connectToWifi();
                }
            	State.refrWifi();
                out.println("WIFI"+State.connectedWifi);
                out.flush();                
                
                break;
            case "startIPSEC":
            	State.refrIPSEC();
            	if(!State.connectedIPSEC){
	            	executeCommandSH("ipsec restart");
	            	executeCommandSH("ipsec up home");
            	}
            	State.refrIPSEC();
                out.println("IPSEC"+State.connectedIPSEC);
                out.flush();
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
	
	
	public static boolean executeCommandSH(String cmd){	    
        try {
        	Process process = Runtime.getRuntime().exec(cmd);
        	
        	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        	BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        	
        	//System.out.println("Here is the standard output of the command:\n");
        	String s = null;
        	while ((s = stdInput.readLine()) != null) {
        	    System.out.println(s);
        	}

        	// read any errors from the attempted command
        	//System.out.println("Here is the standard error of the command (if any):\n");
        	while ((s = stdError.readLine()) != null) {
        	    System.out.println(s);
        	}
        	
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	
	
	public static void connectToWifi(){		//TODO: boolean?
		//TODO
		//executeCommandSH("echo hello >> /etc/wpa_supplicant.conf");
		//wont work this way, need to do it java style
		
		String txt = "\nnetwork={\n\tssid=\"" + State.wifiSSID + "\"\n\tpsk=\"" + State.wifiPass + "\"\n}\n";
		
		try {
		    Files.write(Paths.get("/etc/wpa_supplicant/wpa_supplicant.conf"), txt.getBytes(), StandardOpenOption.APPEND);
		}catch (IOException e) {
		    //exception handling left as an exercise for the reader
		}
		
		executeCommandSH("service networking restart");
		
	}
	


}
