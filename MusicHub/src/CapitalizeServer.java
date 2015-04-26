import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CapitalizeServer {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
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
				System.out.println("audioFileLength: "+audioFileLength+", durationInSeconds: "+durationInSeconds);
							
				byte[] data = new byte[52428];// 128Kb
				try {
					int bytesRead = 0;
					while (bytesRead != -1) {

						bytesRead = audioInputStream.read(data, 0, data.length);
						out.writeInt(data.length); // write length of the message
						out.write(data);           // write the message

					}
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
	}

}
