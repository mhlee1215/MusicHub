package com.musichub.core;
import java.io.DataInputStream;
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
	ReceiveDaemon receiveDaemon = null;
	static PlayDaemon playDaemon = null;
	static Queue<AudioPacket> packets = null;
	
	public CapitalizeClient() {
		packets = new LinkedList<AudioPacket>();
	}

	
	public static class PlayDaemon extends Thread {
		
		
		SourceDataLine sourceDataLine = null;
		
		public PlayDaemon(SourceDataLine sourceDataLine){
			this.sourceDataLine = sourceDataLine;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(packets.size() > 0){
				System.out.println("packets.size() :"+packets.size());
				
				AudioPacket curPacket = packets.poll();
				System.out.println(curPacket.toString());
				sourceDataLine.write(curPacket.packet, 0, curPacket.length);
			}
		}
		
	}
	
	public static class ReceiveDaemon extends Thread {

		Socket socket = null;
		DataInputStream in = null;
		SourceDataLine sourceDataLine = null;
		int bufferedCycle = 0;

		public ReceiveDaemon(Socket socket, DataInputStream in, int bufferedCycle) {
			this.socket = socket;
			this.in = in;
			this.bufferedCycle = bufferedCycle;
			
			try {			
				float sampleRate = in.readFloat();
				int bits = in.readInt();
				int channels = in.readInt();
				boolean isBigEndian = in.readBoolean();
				
				AudioFormat audioFormat2 = new AudioFormat(sampleRate, bits, channels, true, isBigEndian);
				System.out.println(audioFormat2.toString());
				DataLine.Info info = new DataLine.Info(SourceDataLine.class,
						audioFormat2);
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceDataLine.open(audioFormat2);
				sourceDataLine.start();
				
				playDaemon = new PlayDaemon(sourceDataLine);
				//playDaemon.setDaemon(true);
				
				//PlayDaemon
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
			while (true) {
				try {
					int byteRead = in.readInt();
					int length = in.readInt();
					if(length>0) {
					    byte[] message = new byte[length];
					    //System.out.println("receive length!! : "+length);
					    in.readFully(message, 0, length); // read the message
					    //System.out.println("receive length : "+length);
					    //sourceDataLine.write(message, 0, length);
					    
					    packets.add(new AudioPacket(byteRead, message));
					    
					    
					    if(!playDaemon.isAlive()){
					    	if(packets.size() > bufferedCycle){
					    		System.out.println("Play Daemon started..");
					    		playDaemon.start();
					    	}
					    		
					    }
					}else{
						System.out.println("Receive End");
						if(!playDaemon.isAlive()){
					    	playDaemon.start();
					    }
						break;
					}

				} catch (IOException ex) {
					//response = "Error: " + ex;
					ex.printStackTrace();
				}
			}
		}
	}
	


	public void connectToServer(String serverAddress) throws IOException, InterruptedException {
		int timeGapBetweenFail = 1000;
		while (true) {
			try {
				Socket socket = new Socket(serverAddress, 9898);
				in = new DataInputStream(socket.getInputStream());
				receiveDaemon = new ReceiveDaemon(socket, in, 10000);
				//receiveDaemon.setDaemon(true);
				System.out.println("Receive Daemon started..");
				receiveDaemon.start();
				break;
			} catch (ConnectException e) {
				System.err.println("Connection Fail.. try again after "+timeGapBetweenFail+"ms");
				//e.printStackTrace();
				Thread.sleep(timeGapBetweenFail);
			}
		}
	}
	
	public void disconnectToServer(){
		receiveDaemon.stop();
		playDaemon.stop();
	}
	

	public static void main(String[] args) throws Exception {
		System.out.println("I am client");
		// TODO Auto-generated method stub
		CapitalizeClient client = new CapitalizeClient();
		String serverIP = "";
		if (args.length < 3)
			serverIP = "localhost";
		else serverIP = args[3];
		
		client.connectToServer(serverIP);
	}

}
