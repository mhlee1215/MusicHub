package com.musichub.core;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WifiDetector extends Thread {

	public static int SIGNAL_INIT = -1000;
	private boolean isStarted = false;
				

	public WifiDetector(){
		//setDaemon(true);
	}

	public static void main(String[] args) {
		Thread t = new WifiDetector();
		t.start();
	}
	
	public static int getSignal(){
		int signal = SIGNAL_INIT;
		try{
			ProcessBuilder builder = new ProcessBuilder("/sbin/iwconfig");
			//ProcessBuilder builder = new ProcessBuilder("/sbin/ifconfig");
			builder.redirectErrorStream(false);
			Process p = builder.start();
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			//String line = "Link Quality=70/70  Signal level=-19 dBm";
			String line;
			
			
			while (true) {
				line = r.readLine();
				if (line != null && line.contains("Signal")){
					Pattern pt = Pattern.compile("-\\d+");
					Matcher m = pt.matcher(line);
					while (m.find()) {
						//System.out.println(m.group());
						signal = Integer.parseInt(m.group());
					} 
					break;
				}	
			}
		}catch(Exception e){
			System.err.println("THIS MACHINE IS NOT A LINUX");
		}
		return signal;
	}
	
	public void run(){

		while(true){
			
			try{
				sleep(1000);
				
				int signal = getSignal();
				
				if(!isStarted && signal > -45) {
					WAVPlayer.play("./test.wav");
					isStarted = true;
				}
				if(isStarted && signal <= -45) {
					//WAVPlayer.stop();
					//isStarted = false;
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
