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
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.bind.DatatypeConverter;

public class CapitalizeServer {
	static TimeLookup timeLookup = null;
	private Capitalizer server;

	public CapitalizeServer() throws Exception {
		timeLookup = new TimeLookup();
		System.out.println("The capitalization server is running.");
		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(9898);
		server = new Capitalizer();
		try {

			while (true) {
				System.out.println("Add!" + clientNumber);
				server.addListener(listener.accept(), clientNumber++);
				if (clientNumber == 1) {
					server.start();
				}
			}
		} finally {
			listener.close();
		}
	}
	
	public List<Client> getClients(){
		return server.getClients();
	}
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		CapitalizeServer server = new CapitalizeServer();
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
		private float packetSecLength = 0.1f; // Second
		private long beginTimeGap = 1000;

		// private int clientNumber;

		public List<Client> getClients(){
			return this.clients;
		}
		
		public Capitalizer() {
//			sockets = new ArrayList<Socket>();
//			ins = new ArrayList<BufferedReader>();
//			outs = new ArrayList<DataOutputStream>();
//			isInit = new ArrayList<Boolean>();
//			isAlive = new ArrayList<Boolean>();
			
			clients = new ArrayList<Client>();

			// Open Source stream

			String wavFile = "C:/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/timetolove.wav";
			String urlWavFile = "http://www.ics.uci.edu/~minhaenl/data/timetolove.wav";
			// wavFile =
			// "/Users/mac/Dropbox/class/2015_spring_cs244/code/data/timetolove.wav";

			try {
				URL url = new URL(urlWavFile);
				// InputStream bufferedIn = new
				// BufferedInputStream(url.openStream());
				audioInputStream = AudioSystem.getAudioInputStream(url);
			} catch (Exception ee) {
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
			audioFormat = audioInputStream.getFormat();

			File file = new File(wavFile);
			long audioFileLength = file.length();
			int frameSize = audioFormat.getFrameSize();
			float frameRate = audioFormat.getFrameRate();
			float durationInSeconds = (audioFileLength / (frameSize * frameRate));
			System.out.println("frameSize: " + frameSize + ", frameRate: "
					+ frameRate);
			System.out.println("audioFileLength: " + audioFileLength
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
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				
				DataOutputStream out = new DataOutputStream(
						socket.getOutputStream());
				
				clients.add(clientNumber, new Client(socket, in, out, false, true));
				
//				sockets.add(clientNumber, socket);	
//				ins.add(in);
//				outs.add(out);
//				isInit.add(clientNumber, false);
//				isAlive.add(clientNumber, true);
				
				
				out.writeFloat(audioFormat.getSampleRate());
				out.writeInt(audioFormat.getSampleSizeInBits());
				out.writeInt(audioFormat.getChannels());
				out.writeBoolean(audioFormat.isBigEndian());
				out.writeLong((long) (1000 * packetSecLength));
				out.writeLong(timeLookup.getCurrentTime());
				
				//isInit.set(clientNumber, true);
				
			} catch (IOException e) {
				log("Error Adding client# " + clientNumber + ": " + e);
			}

			

			log("INIT finished. " + clientNumber);

		}

		public void massWriteInt(int v) {
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (!clients.get(clientNumber).isAlive())
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
				if (!clients.get(clientNumber).isAlive())
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
				if (!clients.get(clientNumber).isAlive())
					continue;

				// Socket socket = sockets.get(clientNumber);
				// BufferedReader in = ins.get(clientNumber);
				DataOutputStream out = clients.get(clientNumber).getOut();

				try {
					out.write(data);
				} catch (IOException e) {
					log("Error handling client# " + clientNumber + ": " + e);
					clients.get(clientNumber).setAlive(false);
				}
			}
		}

		public void massWriteBoolean(boolean v) {
			for (int sId = 0; sId < clients.size(); sId++) {
				int clientNumber = sId;
				if (!clients.get(clientNumber).isAlive())
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

		public void run() {

			int pId = 0;
			long curTime = timeLookup.getCurrentTime();

			System.out.println("Sending packet..");

			try {

				byte[] data = new byte[packetSize];// 1 second
				byte[] dataBuffer = new byte[(int) (packetSize * 2)];			//buffer
				byte[] dataBufferLarge = new byte[(int) (packetSize * 10)];		//buffer2
				byte[] dataBufferLargeTmp = new byte[(int) (packetSize * 10)];	//buffer3

				try {
					int bytesRead = 0;
					int bytesReadAll = 0;
					while (true) {
						bytesRead = audioInputStream.read(dataBuffer, 0,
								dataBuffer.length);
						if (bytesRead != -1) {
							System.arraycopy(dataBuffer, 0, dataBufferLarge,
									bytesReadAll, bytesRead);
							bytesReadAll += bytesRead;
						}

						while (bytesReadAll > packetSize) {
							System.arraycopy(dataBufferLarge, 0, data, 0, packetSize);

							System.arraycopy(dataBufferLarge, packetSize, dataBufferLargeTmp, 0, bytesReadAll - packetSize);
							System.arraycopy(dataBufferLargeTmp, 0, dataBufferLarge, 0, bytesReadAll - packetSize);
							bytesReadAll -= packetSize;

							long playTime = (long) (curTime + beginTimeGap + pId * packetSecLength * 1000);

							massWriteInt(packetSize); // write length of the message
							massWriteInt(data.length); // write length of the message
							massWriteLong(playTime);
							massWrite(data);

							pId++;
						}
						// Buffer flush when reading is finished.
						// Content in buffer is less than packet size.
						if (bytesRead == -1) {
							// long curTime = timeLookup.getCurrentTime();
							long playTime = (long) (curTime + beginTimeGap + pId * packetSecLength * 1000);

							massWriteInt(bytesReadAll); // write length of the buffer
							massWriteInt(data.length); // write length of the message
							massWriteLong(playTime);
							// System.out.println("!! "+bytesRead);
							massWrite(data);

							bytesReadAll = 0;
						}

						//If byte reading is finished.
						if (bytesRead == -1 && bytesReadAll == 0) break;

						try {
							Thread.sleep((long) (packetSecLength * 200));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					System.out.println("Send End!");

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
