package com.musichub.core;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.musichub.utils.ThreadController;

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
	
	
	
	
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public void setVolume(int volume){
		System.out.println("[[[[SET VOLUME :"+volume);
		SourceDataLine line = playDaemon.getDataLine();
		FloatControl control=(FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
		Control[] controls = line.getControls();
		for(int i = 0 ; i < controls.length ; i++){
			System.out.println(controls[i]);
		}
		float f_volume = ((float)volume);///100;
		System.out.println("[[[[SET VOLUME :"+control.getMaximum());
		
		float gap = (control.getMaximum() - control.getMaximum())*f_volume;
		float v = control.getMinimum()+gap;
		
		control.setValue(v);	
	}
	
	public static void setVolume(float f_volume){
		SourceDataLine line = playDaemon.getDataLine();
		FloatControl control=(FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
		float gap = (control.getMaximum() - control.getMinimum())*f_volume;
		float v = control.getMinimum()+gap;
//		System.out.println("actual v:"+v);
//		System.out.println("control.getMinimum():"+control.getMinimum());
//		System.out.println("control.getMaximum():"+control.getMaximum());
		
		control.setValue(v);
	}

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




	public static class PlayDaemon extends ThreadController {
		
		
		SourceDataLine sourceDataLine = null;
		long packetDuration = 0;
		int threshold;
		SignalMeanClass signalMeanClass;
		
		public PlayDaemon(SourceDataLine sourceDataLine, long packetDuration, int threshold){
			this.sourceDataLine = sourceDataLine;
			
			//sourceDataLine.getControl(FloatControl.Type.VOLUME);
//			FloatControl control=(FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
//			control.setValue((float) 1);		
			
			this.packetDuration = packetDuration;
			this.threshold = threshold;
			signalMeanClass = new SignalMeanClass();
		}
		
		public SourceDataLine getDataLine(){
			return sourceDataLine;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long residual = 0;
			while (!isFinished()) {
				synchronized (getLock()) {
					while (isPaused()) {
						try {
							getLock().wait();
						} catch (InterruptedException e) {
						}
					}
				}
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
				log("##packets.size() :"+packets.size());
				
				AudioPacket curPacket = packets.poll();
//				sourceDataLine.write(curPacket.packet, 0, curPacket.length);
//				if(1==1) continue;
				//log(curPacket.toString());
				long curTime = timeLookup.getCurrentTime();
				log("curTime:"+curTime+", curPacket.playTime:"+curPacket.playTime+", gap:"+(curPacket.playTime-curTime));
				
				int gap = (int) (curPacket.playTime - curTime);
				
				if (gap < 0 && Math.abs(gap) > packetDuration) continue;
				log("gap : "+gap);
				int gapLength = (int)(curPacket.length * (gap/(float)packetDuration));
				gapLength-=gapLength%4;
				int expPacketSize = (int)(curPacket.length + gapLength);
				byte[] packetSyn = new byte[expPacketSize];
				if (gap >= 0){
					packetSyn = curPacket.packet;
					expPacketSize = curPacket.length;
					try {
						sleep(gap);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.arraycopy(curPacket.packet, 0, packetSyn, gapLength, curPacket.length);
				}else{
					System.arraycopy(curPacket.packet, -gapLength, packetSyn, 0, expPacketSize);
				}
				
				log("expPacketSIze:"+expPacketSize+", gapLength: "+gapLength+", curPacket.length: "+curPacket.length);
				
				long beforeTime = timeLookup.getCurrentTime();
				log("playtime gap : "+ (curPacket.playTime - beforeTime));
				sourceDataLine.write(packetSyn, 0, expPacketSize);
				long afterTime = timeLookup.getCurrentTime();
				residual = packetDuration - (afterTime - beforeTime);
				log("packetDuration : "+packetDuration);
				log("beforeTime : "+beforeTime);
				log("afterTime : "+afterTime);
				log("Residual : "+residual);
			}
		}
		
		
		
	}
	
	/**
	 * Get mean strength of signals
	 * @author mhlee
	 *
	 */
	public static class SignalMeanClass {
		int SIGNAL_SIZE = 5;
		List<Integer> signalList;
		int nextPointer = 0;
		
		public SignalMeanClass(){
			signalList = new ArrayList<Integer>(SIGNAL_SIZE);
			for(int i = 0 ; i <SIGNAL_SIZE ; i++)
				signalList.add(0);
		}
		
		
		public float signalMean(int newSignal){
			signalList.set(nextPointer, newSignal);
			nextPointer++;
			if(nextPointer == SIGNAL_SIZE) nextPointer = 0;
			
			return signalMean();
		}
		
		public float signalMean(){
			int meanSignal = 0;

			int counted = 0;
			for(int i = 0 ; i < signalList.size(); i++){
				if(signalList.get(i) != 0){
					meanSignal += signalList.get(i);
					counted++;
				}
			}
			return meanSignal / (float)counted;
		}
	}
	
//	public static class MeanClass {
//		int SIZE = 5;
//		List<Integer> list;
//		int nextPointer = 0;
//
//		public MeanClass(){
//			list = new ArrayList<Integer>(SIZE);
//			for(int i = 0 ; i < SIZE; i++)
//				list.add(0);
//		}
//
//
//		public float mean(int newData){
//			list.set(nextPointer, newData);
//			nextPointer++;
//			if(nextPointer == SIZE) nextPointer = 0;
//
//			return mean();
//		}
//
//		public float mean(){
//			int meanData = 0;
//
//			int counted = 0;
//			for(int i = 0 ; i < list.size(); i++){
//				if(list.get(i) != 0){
//					meanData += list.get(i);
//					counted++;
//				}
//			}
//			return meanData / (float)counted;
//		}
//	}
	
	
	
	
	
	
	public static class ReceiveDaemon extends ThreadController {

		Socket socket = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		SourceDataLine sourceDataLine = null;
		int bufferedCycle = 0;
		SignalMeanClass signalMeanClass;
		
		boolean isInit = false;
		
		//Init values
		float sampleRate;
		int bits;
		int channels;
		boolean isBigEndian;
		long packetDuration;
		long severTime;
		String clientName;
		int threshold;
		int packetSize;

		public ReceiveDaemon(Socket socket, String clientName, DataInputStream in, DataOutputStream out, int bufferedCycle) {
			this.socket = socket;
			this.in = in;
			this.out = out;
			this.bufferedCycle = bufferedCycle;
			signalMeanClass = new SignalMeanClass();
			
			try {			
				if(!isInit){
					
					out.writeUTF(clientName);
					out.writeInt(WifiDetector.getSignal());
					
					sampleRate = in.readFloat();
					bits = in.readInt();
					channels = in.readInt();
					isBigEndian = in.readBoolean();
					packetDuration = in.readLong();
					severTime = in.readLong();
					timeLookup = new TimeLookup(severTime);
					
					//out.writeLong(timeLookup.getCurrentTime());
					int responseTimeCheckCount = in.readInt();
					long intervalMean = 0;
					for(int ii = 0 ; ii < responseTimeCheckCount ; ii++){
						long serverTime2 = in.readLong();
						intervalMean += timeLookup.getCurrentTime() - serverTime2;
					}
					timeLookup.adjustOffset(intervalMean/responseTimeCheckCount);
					
					threshold = in.readInt();
					packetSize = in.readInt();
					
					isInit = true;
				}
				
				AudioFormat audioFormat2 = new AudioFormat(sampleRate, bits, channels, true, isBigEndian);
				log("<<<<<<Audio Format!\n");
				log(audioFormat2.toString());
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat2);
				sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
				sourceDataLine.open(audioFormat2);
				sourceDataLine.start();
				playDaemon = new PlayDaemon(sourceDataLine, packetDuration, threshold);
			} catch(IOException e){
				e.printStackTrace();
				return;
				
			} catch(java.lang.IllegalArgumentException e){
				e.printStackTrace();
				if(sourceDataLine != null)
					sourceDataLine.close();
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
			byte[] message = null;//new byte[packetSize];//new byte[length];
		    //byte[] message2 = null;//new byte[packetSize];;//new byte[byteRead];
		    while (!isFinished()) {
				synchronized (getLock()) {
					while (isPaused()) {
						try {
							getLock().wait();
						} catch (InterruptedException e) {
						}
					}
				}
				try {
					int byteRead = in.readInt();
					int length = in.readInt();
					
					
				
					log("byteRead:"+byteRead+", length:"+length+", packetSize:"+packetSize);
					
					if(byteRead < 0 || length < 0 || byteRead > packetSize*10 || length > packetSize*10){ 
						byteRead = 0;
						length = 0;
					}
					if(length>0) {
						long playTime = in.readLong();
					    
//						if(message == null || message.length < length)
							message = new byte[packetSize];
//						if(message2 == null || message2.length < byteRead)
							//message2 = new byte[packetSize];
						
					    //log("receive length!! : "+length);
					    in.readFully(message, 0, packetSize); // read the message
					    
					    int newSignal = WifiDetector.getSignal();
					    float meanSignal = signalMeanClass.signalMean(newSignal);
					    out.writeInt(Math.round(meanSignal));
					    
					    
					    //System.arraycopy(message, 0, message2, 0, packetSize);
					    
					    packets.add(new AudioPacket(byteRead, message, playTime));
					    
					    if(!playDaemon.isStarted()){
					    	if(packets.size() > bufferedCycle){
					    		log("Play Daemon started..");
					    		playDaemon.start();
					    	}
					    }
					}
					//Usually not happened.
//					else{
//						System.err.println("Receive error");
//						if(!playDaemon.isFinished()){
//					    	playDaemon.start();
//					    }
//						//break;
//					}

				} catch (Exception ex) {
					//response = "Error: " + ex;
					ex.printStackTrace();
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
		    
		    try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		if(receiveDaemon!=null)
			receiveDaemon.onStop();
		if(playDaemon != null){
			playDaemon.onStop();
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
		
		
//		MeanClass a = new MeanClass();
//		System.out.println(a.mean(10));
//		System.out.println(a.mean(10));
//		System.out.println(a.mean(10));
//		System.out.println(a.mean(10));
//		System.out.println(a.mean(-100));
//		
//
		
//		byte[] bytes = new byte[10];
//		ByteBuffer buf = ByteBuffer.wrap(bytes);
//		System.out.println(buf);
//		if(1==1) return;
		
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
		System.out.println("[CLIENT] "+message);
	}

}