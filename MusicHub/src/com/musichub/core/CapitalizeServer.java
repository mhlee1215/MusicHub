package com.musichub.core;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.bind.DatatypeConverter;

public class CapitalizeServer {
	static TimeLookup timeLookup = null;
	
	public CapitalizeServer() throws Exception{
		timeLookup = new TimeLookup();
		System.out.println("The capitalization server is running.");
		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(9898);
		try {
			while (true) {
				new Capitalizer(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CapitalizeServer server = new CapitalizeServer();
	}

	private static class Capitalizer extends Thread {
		private Socket socket;
		private int clientNumber;

		public Capitalizer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with client# " + clientNumber + " at " + socket);
		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				// Open Source stream
				AudioInputStream audioInputStream = null;
				String wavFile = "C:/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/timetolove.wav";
				String urlWavFile = "http://www.ics.uci.edu/~minhaenl/data/timetolove.wav";
				//wavFile = "/Users/mac/Dropbox/class/2015_spring_cs244/code/data/timetolove.wav";
				
				try{
					URL url = new URL(urlWavFile);
					//InputStream bufferedIn = new BufferedInputStream(url.openStream());
					audioInputStream = AudioSystem
							.getAudioInputStream(url);
				}catch(Exception ee){
					ee.printStackTrace();
					try {
						FileInputStream fstream = new FileInputStream(wavFile);
						audioInputStream = AudioSystem
								.getAudioInputStream(new BufferedInputStream(
										fstream));
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
						return;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
					
				}
				
				

//
//				sourceDataLine.start();
				AudioFormat audioFormat = audioInputStream.getFormat();
				//System.out.println("Signed?"+audioFormat.getEncoding());
				out.writeFloat(audioFormat.getSampleRate());
				out.writeInt(audioFormat.getSampleSizeInBits());
				out.writeInt(audioFormat.getChannels());
				out.writeBoolean(audioFormat.isBigEndian());
				
				File file = new File(wavFile);
				long audioFileLength = file.length();
				int frameSize = audioFormat.getFrameSize();
				float frameRate = audioFormat.getFrameRate();
				float durationInSeconds = (audioFileLength / (frameSize * frameRate));
				System.out.println("frameSize: "+frameSize+", frameRate: "+frameRate);
				System.out.println("audioFileLength: "+audioFileLength+", durationInSeconds: "+durationInSeconds);
				
				int pId = 0;
				long beginTimeGap = 1000;
				float packetSecLength = 0.1f; //Second
				
				out.writeLong((long)(1000*packetSecLength));
				
				int packetSize = (int) Math.ceil(frameSize*frameRate*packetSecLength);
				byte[] data = new byte[packetSize];// 1 second
				byte[] dataBuffer = new byte[(int) (packetSize*2)];// 1 second
				byte[] dataBufferLarge = new byte[(int) (packetSize*10)];// 1 second
				byte[] dataBufferLargeTmp = new byte[(int) (packetSize*10)];// 1 second
				
				long curTime = timeLookup.getCurrentTime();
				try {
					int bytesRead = 0;
					int bytesReadAll = 0;
					while (true) {
						//System.out.println("pID :"+pId++);
						//bytesRead = audioInputStream.read(data, 0, data.length);
						bytesRead = audioInputStream.read(dataBuffer, 0, dataBuffer.length);
						if (bytesRead != -1){
							//break;
							//System.out.println("bytesReadAll: "+bytesReadAll+", bytesRead:"+bytesRead);
							System.arraycopy(dataBuffer, 0, dataBufferLarge, bytesReadAll, bytesRead);
							bytesReadAll += bytesRead;
						}
						
						//System.out.println("bytesReadAll: "+bytesReadAll+", "+bytesRead);
						
						
						
						//System.out.println("bytesRead: "+bytesRead+", data.length: "+data.length);
						
//						out.writeInt(bytesRead); // write length of the message
//						out.writeInt(data.length); // write length of the message
//						//System.out.println("!! "+bytesRead);
//						out.write(data); 
//						System.out.println(toHexString(data).substring(0, 10));
						
						//System.out.println(toHexString(dataBuffer).substring(0, 10));
						if (bytesReadAll > packetSize)
						{
							System.arraycopy(dataBufferLarge, 0, data, 0, packetSize);
							
							System.arraycopy(dataBufferLarge, packetSize, dataBufferLargeTmp, 0, bytesReadAll-packetSize);
							System.arraycopy(dataBufferLargeTmp, 0, dataBufferLarge, 0, bytesReadAll-packetSize);
							bytesReadAll -= packetSize;
							
							//dataBufferLarge = dataBufferLargeTmp;
							//dataBufferLargeTmp = new byte[(int) (packetSize*10)];
							
							long playTime = (long) (curTime + beginTimeGap + pId*packetSecLength*1000);
							
							
							out.writeInt(packetSize); // write length of the message
							out.writeInt(data.length); // write length of the message
							out.writeLong(playTime);
							//System.out.println("!! "+bytesRead);
							out.write(data); 
							pId++;
							//System.out.println(toHexString(data).substring(0, 10));
							//System.out.println("pID "+(pId++));
						}else{
							//Buffer flush when reading is finished.
							//Content in buffer is less than packet size.
							if(bytesRead == -1){
								//long curTime = timeLookup.getCurrentTime();
								long playTime = (long) (curTime + beginTimeGap + pId*packetSecLength*1000);
								
								out.writeInt(bytesReadAll); // write length of the message
								out.writeInt(data.length); // write length of the message
								out.writeLong(playTime);
								//System.out.println("!! "+bytesRead);
								out.write(data);  
								bytesReadAll = 0;
							}
						}
						
						
						
						
						if( bytesRead == -1 && bytesReadAll == 0)
							break;
						
						
//						out.writeInt(bytesRead); // write length of the message
//						out.writeInt(data.length); // write length of the message
//						//System.out.println("!! "+bytesRead);
//						out.write(data);           // write the message
						//System.out.println("!!2");

					}
					System.out.println("Send End!");
					out.writeInt(0); // write length of the message
					out.writeInt(0); // write length of the message
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}

			} catch (IOException e) {
				log("Error handling client# " + clientNumber + ": " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				log("Connection with client# " + clientNumber + " closed");
			}
		}

		private void log(String message) {
			System.out.println(message);
		}
		
		public static String toHexString(byte[] array) {
		    return DatatypeConverter.printHexBinary(array);
		}

		public static byte[] toByteArray(String s) {
		    return DatatypeConverter.parseHexBinary(s);
		}
	}

}
