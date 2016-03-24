package com.musichub.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.bind.DatatypeConverter;

public class CapitalizeServer {
	static TimeLookup timeLookup = null;
	private static Capitalizer server;
	private ListeningDaemon listeningDaemon;
	private boolean start;
	private static ServerSocket listener;
	
	boolean isLazy = false;
	int lazyNum = 0;
	int threshold = 0;
	
	
	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public int getLazyNum() {
		return lazyNum;
	}

	public void setLazyNum(int lazyNum) {
		this.lazyNum = lazyNum;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public CapitalizeServer() throws Exception {
		this(false,  0, -45);
	}
	
	public CapitalizeServer(boolean isLazy, int lazyNum, int threshold) throws Exception {
		this.isLazy = isLazy;
		this.lazyNum = lazyNum;
		this.threshold = threshold;
		
		timeLookup = new TimeLookup();
		//if(listeningDaemon == null)
		//listeningDaemon = new ListeningDaemon();
		start = false;
		listener = new ServerSocket(9898);
	}
	
	public void startServer() throws Exception{
		if(start == false){
			log("The capitalization server is running.");
			listeningDaemon = new ListeningDaemon(threshold);
			listeningDaemon.start();
			start = true;
		}
	}
	
	public void stopServer() throws Exception{
		if(start == true){
			log("The capitalization server is stopped.");
			listeningDaemon.stop();
			listener.close();
			server.stop();
			start = false;
		}
	}
	
	
	
	public static class ListeningDaemon extends Thread {
		int threshold;
		public ListeningDaemon(int threshold){
			this.threshold = threshold;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int clientNumber = 0;
				
			try {
				server = new Capitalizer(threshold);
				try {
	
					while (true) {
						log("Waiting.. next is " + clientNumber);
						server.addListener(listener.accept(), clientNumber++);
						if (clientNumber == 1) {
							server.start();
						}
					}
				} finally {
					listener.close();
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public List<Client> getClients(){
		return server.getClients();
	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CapitalizeServer server = new CapitalizeServer();
		server.startServer();
		System.err.println("END!?!?");
	}

	private static class Capitalizer extends Thread {
		
		private List<Client> clients;
		
//		private List<Socket> sockets;
//		private List<BufferedReader> ins;
//		private List<DataOutputStream> outs;
//		private List<Boolean> isInit;
//		private List<Boolean> isAlive;

		// private Socket socket;
		private AudioInputStream audioInputStream = null;
		private AudioFormat audioFormat = null;
		private int packetSize = 0;
		private float packetSecLength = .1f; // Second
		private long beginTimeGap = 1000;
		int threshold = 0;

		// private int clientNumber;

		public List<Client> getClients(){
			return this.clients;
		}
		
		public int getLazyClientNum(){
			int lazyNum = 0;
			for(int i = 0 ; i < clients.size() ; i++){
				if(threshold < clients.get(i).getSignal())
					lazyNum++;
			}
			return lazyNum;
		}
		
		public static AudioInputStream getAudioInputStream(){
			//String wavFile = "C:/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/Metronome120.wav";
			String wavFile = "C:/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/ratherbe.wav";
			String urlWavFile = "//http://www.ics.uci.edu/~minhaenl/data/timetolove.wav";
			String wavFile2 = "/Users/joshua_mac/Desktop/data/ratherbe.wav";
			AudioInputStream aInputStream = null;
			try {
				URL url = new URL(urlWavFile);
				// InputStream bufferedIn = new
				// BufferedInputStream(url.openStream());
				aInputStream = AudioSystem.getAudioInputStream(url);
			} catch (Exception ee) {
				//ee.printStackTrace();
				System.err.println("Fail to load online file.");
				try {
					FileInputStream fstream = new FileInputStream(wavFile);
					
					aInputStream = AudioSystem
							.getAudioInputStream(new BufferedInputStream(
									fstream));
					
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
					//return;
				} catch (IOException e) {
					try {
						FileInputStream fstream = new FileInputStream(wavFile2);
						
						aInputStream = AudioSystem
								.getAudioInputStream(new BufferedInputStream(
										fstream));
					} catch (UnsupportedAudioFileException eeee) {
						eeee.printStackTrace();
						//return;
					} catch (IOException eee) {
						eee.printStackTrace();
						//return;
					}
				}

			}
			return aInputStream;
		}
		
		public Capitalizer(int threshold) {
			this.threshold = threshold;
//			sockets = new ArrayList<Socket>();
//			ins = new ArrayList<BufferedReader>();
//			outs = new ArrayList<DataOutputStream>();
//			isInit = new ArrayList<Boolean>();
//			isAlive = new ArrayList<Boolean>();
			
			clients = new ArrayList<Client>();

			// Open Source stream

			audioInputStream = getAudioInputStream();
			audioFormat = audioInputStream.getFormat();

			String wavFile = "C:/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/timetolove.wav";
			File file = new File(wavFile);
			long audioFileLength = file.length();
			int frameSize = audioFormat.getFrameSize();
			float frameRate = audioFormat.getFrameRate();
			float durationInSeconds = (audioFileLength / (frameSize * frameRate));
			log("frameSize: " + frameSize + ", frameRate: "
					+ frameRate);
			log("audioFileLength: " + audioFileLength
					+ ", durationInSeconds: " + durationInSeconds);

			packetSize = (int) Math.ceil(frameSize * frameRate
					* packetSecLength);

		}

		// public Capitalizer(Socket socket, int clientNumber) {
		// this.socket = socket;
		// this.clientNumber = clientNumber;
		// log("New connection with client# " + clientNumber + " at " + socket);
		// }

		public void addListener(Socket socket, int clientNumber) {
			
			log("New connection with client# " + clientNumber + " at " + socket);

			try {
				//BufferedReader in = new BufferedReader(new InputStreamReader(
				//		socket.getInputStream()));
				
				DataInputStream in = new DataInputStream(socket.getInputStream());
				
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				
				
//				sockets.add(clientNumber, socket);	
//				ins.add(in);
//				outs.add(out);
//				isInit.add(clientNumber, false);
//				isAlive.add(clientNumber, true);
				
				log("audioFormat.getSampleRate():"+audioFormat.getSampleRate());
				log("audioFormat.getSampleSizeInBits():"+audioFormat.getSampleSizeInBits());
				log("audioFormat.getChannels():"+audioFormat.getChannels());
				log("audioFormat.isBigEndian():"+audioFormat.isBigEndian());
				log("(long) (1000 * packetSecLength):"+(long) (1000 * packetSecLength));
				log("timeLookup.getCurrentTime():"+timeLookup.getCurrentTime());
				
				System.out.println("Server receive name");
				String clientName = in.readUTF();
				int signal = in.readInt();
				System.out.println("Server receive name end");
				out.writeFloat(audioFormat.getSampleRate());
				out.writeInt(audioFormat.getSampleSizeInBits());
				out.writeInt(audioFormat.getChannels());
				out.writeBoolean(audioFormat.isBigEndian());
				out.writeLong((long) (1000 * packetSecLength));
				out.writeLong(timeLookup.getCurrentTime());
				
				//long clientTime = in.readLong();
				
				int responseTimeCheckCount = 10;
				out.writeInt(responseTimeCheckCount);
				for(int ii = 0 ; ii < responseTimeCheckCount ; ii++)
					out.writeLong(timeLookup.getCurrentTime());
				
				out.writeInt(threshold);
				out.writeInt(packetSize);
				
				
				clients.add(clientNumber, new Client(socket, clientName, in, out, false, true, signal));
				
				
				//isInit.set(clientNumber, true);
				
			} catch (IOException e) {
				log("Error Adding client# " + clientNumber + ": " + e);
			}

			

			log("INIT finished. " + clientNumber);

		}

		public void massWriteInt(int v) {
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (!clients.get(clientNumber).isAlive() ||
						!clients.get(clientNumber).isStreaming())
					continue;

				// Socket socket = sockets.get(clientNumber);
				// BufferedReader in = ins.get(clientNumber);
				DataOutputStream out = clients.get(clientNumber).getOut();

				try {
					out.writeInt(v);
				} catch (IOException e) {
					log("Error handling client# " + clientNumber + ": " + e);
					clients.get(clientNumber).setAlive(false);
				}
			}
		}

		public void massWriteLong(long v) {
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (!clients.get(clientNumber).isAlive() ||
						!clients.get(clientNumber).isStreaming())
					continue;

				// Socket socket = sockets.get(clientNumber);
				// BufferedReader in = ins.get(clientNumber);
				DataOutputStream out = clients.get(clientNumber).getOut();

				try {
					out.writeLong(v);
				} catch (IOException e) {
					log("Error handling client# " + clientNumber + ": " + e);
					clients.get(clientNumber).setAlive(false);
				}
			}
		}

		public void massWrite(byte[] data) {
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (!clients.get(clientNumber).isAlive() ||
						!clients.get(clientNumber).isStreaming())
					continue;

				// Socket socket = sockets.get(clientNumber);
				// BufferedReader in = ins.get(clientNumber);
				DataOutputStream out = clients.get(clientNumber).getOut();
				DataInputStream in = clients.get(clientNumber).getIn();

				try {
					out.write(data);
				} catch (IOException e) {
					log("Error handling client# " + clientNumber + ": " + e);
					clients.get(clientNumber).setAlive(false);
				}
				
				try {
					int signal = in.readInt();
					clients.get(clientNumber).setSignal(signal);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log("Error handling client# " + clientNumber + ": " + e);
					clients.get(clientNumber).setAlive(false);
				}
			}
		}

		public void massWriteBoolean(boolean v) {
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (!clients.get(clientNumber).isAlive() ||
						!clients.get(clientNumber).isStreaming())
					continue;

				// Socket socket = sockets.get(clientNumber);
				// BufferedReader in = ins.get(clientNumber);
				DataOutputStream out = clients.get(clientNumber).getOut();

				try {
					out.writeBoolean(v);
				} catch (IOException e) {
					log("Error handling client# " + clientNumber + ": " + e);
					clients.get(clientNumber).setAlive(false);
				}
			}
		}
		
		public void updateStreamingInfo(){
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (clients.get(clientNumber).isAlive())
					clients.get(clientNumber).setStreaming(true);
			}
		}

		public void run() {

			int pId = 0;
			long curTime = timeLookup.getCurrentTime();

			//log("Sending packet..");

			try {

				byte[] data = new byte[packetSize];// 1 second
				byte[] dataBuffer = new byte[(int) (packetSize * 2)];			//buffer
				byte[] dataBufferLarge = new byte[(int) (packetSize * 10)];		//buffer2
				byte[] dataBufferLargeTmp = new byte[(int) (packetSize * 10)];	//buffer3

				try {
					int bytesRead = 0;
					int bytesReadAll = 0;
					while (true) {
						log("Send packet #"+pId);
						updateStreamingInfo();
						//long beginTime = timeLookup.getCurrentTime();
						if(bytesReadAll < packetSize){
							bytesRead = audioInputStream.read(dataBuffer, 0,
									dataBuffer.length);
							if (bytesRead != -1) {
								System.arraycopy(dataBuffer, 0, dataBufferLarge,
										bytesReadAll, bytesRead);
								bytesReadAll += bytesRead;
							}else{
								audioInputStream = getAudioInputStream();
								continue;
								//audioInputStream.reset();
//								try {
//									Clip clip = AudioSystem.getClip();
//									clip.open(audioInputStream);
//									clip.setFramePosition(0);
//								} catch (LineUnavailableException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								};
//								
//								continue;
							}
						}

						
						int sizeToSend = 0;
						if (bytesReadAll >= packetSize) {
							sizeToSend = packetSize;
						}else{
							sizeToSend = bytesReadAll;
						}
						sizeToSend = Math.max(0, sizeToSend);
						
//						if (bytesReadAll >= packetSize) {
							System.arraycopy(dataBufferLarge, 0, data, 0, sizeToSend);

							System.arraycopy(dataBufferLarge, sizeToSend, dataBufferLargeTmp, 0, bytesReadAll - sizeToSend);
							System.arraycopy(dataBufferLargeTmp, 0, dataBufferLarge, 0, bytesReadAll - sizeToSend);
							bytesReadAll -= sizeToSend;

							long playTime = (long) (curTime + beginTimeGap + pId * packetSecLength * 1000);

							log("send! byteRead:"+sizeToSend);
							massWriteInt(sizeToSend); // write length of the message
							log("send! length:"+data.length);
							massWriteInt(data.length); // write length of the message
							massWriteLong(playTime);
							massWrite(data);
							
							pId++;
//						}
						// Buffer flush when reading is finished.
						// Content in buffer is less than packet size.
//						if (bytesRead == -1) {
//							// long curTime = timeLookup.getCurrentTime();
//							long playTime = (long) (curTime + beginTimeGap + pId * packetSecLength * 1000);
//
//							log("flush massWrite #"+pId);
//							massWriteInt(bytesReadAll); // write length of the buffer
//							massWriteInt(data.length); // write length of the message
//							massWriteLong(playTime);
//							// log("!! "+bytesRead);
//							massWrite(data);
//							log("flush massWrite end #"+pId);
//
//							bytesReadAll = 0;
//						}

						//If byte reading is finished.
						if (bytesRead == -1 && bytesReadAll == 0){
							log("Byte read finished.");
							break;
						}

						long endTime = timeLookup.getCurrentTime();
						long expectedEndTime = (long) (curTime + pId * packetSecLength * 1000);
						if( expectedEndTime < endTime){
							log("Capacity Overhead!");
						}else{
							try {
								log("Sleep : ["+(expectedEndTime - endTime - 100)+"]");
								Thread.sleep((long) (Math.max(0, expectedEndTime - endTime - 100)));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
						
						

					}
					log("Send End!");

					massWriteInt(0); // write length of the message
					massWriteInt(0); // write length of the message
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}

			} finally {

				for (int sId = 0; sId < clients.size(); sId++) {
					int clientNumber = sId;
					try {
						clients.get(clientNumber).getSockets().close();
					} catch (IOException e) {
						log("Couldn't close a socket #"+clientNumber+", what's going on?");
					}

					log("Connection with client# " + clientNumber + " closed");
					clients.get(clientNumber).setAlive(false);
				}

			}

		}

//		private void log(String message) {
//			System.out.println("[SERVER] "+message);
//		}

		public static String toHexString(byte[] array) {
			return DatatypeConverter.printHexBinary(array);
		}

		public static byte[] toByteArray(String s) {
			return DatatypeConverter.parseHexBinary(s);
		}
	}
	
	private static void log(String message) {
		System.out.println("[SERVER] "+message);
	}

}