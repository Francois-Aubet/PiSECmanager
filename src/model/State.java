package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;


public class State {
	private static String networkInterfaceName = "wlp1s0";
	public static boolean connectedWifi = false;
	public static boolean connectedWeb = false;
	public static boolean connectedIPSEC = false;
	public static String wifiSSID = "";
	public static String wifiPass = "";
	
	
	public State(){
		connectedWifi = false;
		connectedWeb = false;
		connectedIPSEC = false;
	}
	
	
	public static void refreshAll(){
		refrWifi();
		isInternetReachable();
		refrIPSEC();
	}
	
	
	public static void refrIPSEC(){
		
		Process process;
		try {
			process = Runtime.getRuntime().exec("ipsec status | grep TUNNEL");
	    	
	    	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
	    	
	    	//System.out.println("Here is the standard output of the command:\n");
	    	String s = null, a = null;
	    	while ((s = stdInput.readLine()) != null) {
	    	    a = s;
	    	}
	    	
	    	if(a != null){
	    		connectedIPSEC = true;
	    	} else {
	    		connectedIPSEC = false;
	    	}
    	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void refrWifi(){
		try {
			NetworkInterface wlan = NetworkInterface.getByName(networkInterfaceName);
			if(wlan != null){
			connectedWifi = wlan.isUp();	
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
			connectedWifi = false;
		}
	}	
	//checks for connection to the internet through dummy request
    public static void isInternetReachable()
    {
        try {
            //make a URL to a known source
            URL url = new URL("http://www.google.com");

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            connectedWeb = false;
        }
        catch (IOException e) {
            e.printStackTrace();
            connectedWeb = false;
        }
        connectedWeb = true;
    }
	
    
    
    
    

}
