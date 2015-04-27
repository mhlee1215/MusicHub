package com.musichub.core;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class CapitalizeClient {

	DataInputStream in = null;//new DataInputStream(socket.getInputStream());
	public CapitalizeClient() {

	}

	public static class PlayDemon extends Thread {

		Socket socket = null;
		DataInputStream in = null;
		SourceDataLine sourceDataLine = null;

		public PlayDemon(Socket socket, DataInputStream in) {
			this.socket = socket;
			this.in = in;

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
					int length = in.readInt();

					if(length>0) {
					    byte[] message = new byte[length];
					    in.readFully(message, 0, length); // read the message
					    //System.out.println("receive length : "+length);
					    sourceDataLine.write(message, 0, length);
					}else{
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
				new PlayDemon(socket, in).start();
				break;
			} catch (ConnectException e) {
				System.err.println("Connection Fail.. try again after "+timeGapBetweenFail+"ms");
				//e.printStackTrace();
				Thread.sleep(timeGapBetweenFail);
			}

		}

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
