package com.musichub.core;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class CapitalizeClient {

	
	DataInputStream in = null;//new DataInputStream(socket.getInputStream());
	DataOutputStream out = null;
	ReceiveDaemon receiveDaemon = null;
	static PlayDaemon playDaemon = null;
	static Queue<AudioPacket> packets = null;
	static TimeLookup timeLookup = null;
	boolean play;
	boolean connected;
	String clientName = "";
	String serverAddress = "";
	
	
	public CapitalizeClient(String clientName) {
		this();
		this.clientName = clientName;
		
		//timeLookup = new TimeLookup();
	}
	
	public CapitalizeClient() {
		play = false;
		connected = false;
		packets = new LinkedList<AudioPacket>();
		//timeLookup = new TimeLookup();
	}
	
	

	
	public boolean isPlay() {
		return play;
	}

	public boolean isConnected() {
		return connected;
	}




	public static class PlayDaemon extends Thread {
		
		
		SourceDataLine sourceDataLine = null;
		long packetDuration = 0;
		
		public PlayDaemon(SourceDataLine sourceDataLine, long packetDuration){
			this.sourceDataLine = sourceDataLine;
			this.packetDuration = packetDuration;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long residual = 0;
			while(true){
				if (packets.size() == 0){
					try {
						Thread.sleep(1000);
						continue;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						log("No packet to play.. wait..");
					}
				}
				log("packets.size() :"+packets.size());
				
				AudioPacket curPacket = packets.poll();
				//log(curPacket.toString());
				long curTime = timeLookup.getCurrentTime();
				log("curTime:"+curTime+", curPacket.playTime:"+curPacket.playTime+", gap:"+(curPacket.playTime-curTime));
				//If current time is already passed, just trow away packet
				if (curPacket.playTime < curTime){
					continue;
				}
				//Else, wait the time until the specified time.
				else{
					try {
						long sleepTime = curPacket.playTime - curTime - residual;
						log("sleep : "+sleepTime);
						if(sleepTime > 0)
							sleep(sleepTime);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				long beforeTime = timeLookup.getCurrentTime();
				sourceDataLine.write(curPacket.packet, 0, curPacket.length);
				long afterTime = timeLookup.getCurrentTime();
				residual = packetDuration - (afterTime - beforeTime);
				log("time gap : "+((afterTime-beforeTime)/(float)1000));
			}
		}
		
	}
	
	public static class ReceiveDaemon extends Thread {

		Socket socket = null;
		DataInputStream in = null;
		SourceDataLine sourceDataLine = null;
		int bufferedCycle = 0;
		
		boolean isInit = false;
		
		//Init values
		float sampleRate;
		int bits;
		int channels;
		boolean isBigEndian;
		long packetDuration;
		long severTime;
		String clientName;

		public ReceiveDaemon(Socket socket, String clientName, DataInputStream in, DataOutputStream out, int bufferedCycle) {
			this.socket = socket;
			this.in = in;
			this.bufferedCycle = bufferedCycle;
			
			try {			
				if(!isInit){
					System.out.println("client send name");
					//out.writeChars(clientName);
					out.writeUTF(clientName);
					System.out.println("client send name end");
					sampleRate = in.readFloat();
					bits = in.readInt();
					channels = in.readInt();
					isBigEndian = in.readBoolean();
					packetDuration = in.readLong();
					severTime = in.readLong();
					timeLookup = new TimeLookup(severTime);
					isInit = true;
				}
				
				AudioFormat audioFormat2 = new AudioFormat(sampleRate, bits, channels, true, isBigEndian);
				log(audioFormat2.toString());
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat2);
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceDataLine.open(audioFormat2);
				sourceDataLine.start();
				playDaemon = new PlayDaemon(sourceDataLine, packetDuration);
			} catch(IOException e){
				e.printStackTrace();
				return;
				
			} catch (LineUnavailableException e) {
			
				e.printStackTrace();
				return;
			}
		}

		@Override
		public void run() {
			
			//int i = 0;
			int errCount = 0;
			while (true) {
				try {
					int byteRead = in.readInt();
					int length = in.readInt();
				
					//log("byteRead:"+byteRead+", length:"+length);
					if(length>0) {
						long playTime = in.readLong();
					    byte[] message = new byte[length];
					    byte[] message2 = new byte[byteRead];
					    //log("receive length!! : "+length);
					    in.readFully(message, 0, length); // read the message
					    System.arraycopy(message, 0, message2, 0, byteRead);
					    
					    packets.add(new AudioPacket(byteRead, message2, playTime));
					    
					    if(!playDaemon.isAlive()){
					    	if(packets.size() > bufferedCycle){
					    		log("Play Daemon started..");
					    		playDaemon.start();
					    	}
					    }
					}else{
						log("Receive End");
						if(!playDaemon.isAlive()){
					    	playDaemon.start();
					    }
						break;
					}

				} catch (Exception ex) {
					//response = "Error: " + ex;
					//ex.printStackTrace();
					System.err.println("Err to receive file");
					errCount++;
					if(errCount > 10){
						System.err.println("Client Ends");
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	


	public void connectToServer(String serverAddress) throws IOException, InterruptedException {
		this.serverAddress = serverAddress;
		log("connectToServer:"+serverAddress);
		int timeGapBetweenFail = 1000;
		while (true) {
			try {
				log("Trying to connect server..");
				Socket socket = new Socket(serverAddress, 9898);
				log("Trying to get inputstream..");
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				connected = true;
				play(socket, in, out);
				break;
			} catch (ConnectException e) {
				System.err.println("Connection Fail.. try again after "+timeGapBetweenFail+"ms");
				connected = false;
				//e.printStackTrace();
				Thread.sleep(timeGapBetweenFail);
			}
		}
	}
	
	public void play(Socket socket, DataInputStream in, DataOutputStream out){
		if (receiveDaemon == null)
			receiveDaemon = new ReceiveDaemon(socket, clientName, in, out, 10);
		//receiveDaemon.setDaemon(true);
		log("Receive Daemon started..");
		receiveDaemon.start();
		play = true;
	}
	
//	public synchronized void resumePlay(){
//		log("resumePlay");
//		//receiveDaemon.notify();
//		//playDaemon.notify();
//		playDaemon.start();
//	}
	
	public synchronized void disconnectToServer(){
		log("disconnectToServer");
		receiveDaemon.stop();
		playDaemon.stop();
		try {
			receiveDaemon.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		play = false;
		connected = false;
		
//		try {
//			receiveDaemon.wait();
//			playDaemon.wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	

	public static void main(String[] args) throws Exception {
		log("I am client");
		// TODO Auto-generated method stub
		CapitalizeClient client = new CapitalizeClient();
		String serverIP = "";
		if (args.length < 3)
			serverIP = "localhost";
		else serverIP = args[3];
		
		client.connectToServer(serverIP);
	}
	
	private static void log(String message) {
		//System.out.println("[CLIENT] "+message);
	}

}
