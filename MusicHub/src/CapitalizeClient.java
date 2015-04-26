import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CapitalizeClient {

	DataInputStream in = null;//new DataInputStream(socket.getInputStream());
	//private BufferedReader in;
	//private PrintWriter out;
//	private JFrame frame = new JFrame("Capitalize Client");
//	private JTextField dataField = new JTextField(40);
//	private static JTextArea messageArea = new JTextArea(8, 60);

	public CapitalizeClient() {
//		messageArea.setEditable(false);
//		frame.getContentPane().add(dataField, "North");
//		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
//
//		dataField.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent e) {
//				out.println(dataField.getText());
//				String response;
//
//				try {
//					response = in.readLine();
//					if (response == null || response.equals("")) {
//						System.exit(0);
//					}
//				} catch (IOException ex) {
//					response = "Error: " + ex;
//				}
//				messageArea.append(response + "\n");
//				dataField.selectAll();
//			}
//		});
	}

	public static class PlayDemon extends Thread {

		Socket socket = null;
		DataInputStream in = null;
		//PrintWriter out = null;
		SourceDataLine sourceDataLine = null;

		public PlayDemon(Socket socket, DataInputStream in) {
			this.socket = socket;
			this.in = in;
			//this.out = out;

			// Open Source stream
			// Should be removed!!
			AudioInputStream audioInputStream = null;
			String wavFile = "C:/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/timetolove.wav";
			try {
				FileInputStream fstream = new FileInputStream(wavFile);
				audioInputStream = AudioSystem
						.getAudioInputStream(new BufferedInputStream(fstream));
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			try {
				//AudioFormat audioFormat = audioInputStream.getFormat();
//				Encoding e = new Encoding();
				
//				out.writeFloat(audioFormat.getSampleRate());
//				out.writeInt(audioFormat.getSampleSizeInBits());
//				out.writeInt(audioFormat.getChannels());
//				out.writeBoolean(audioFormat.isBigEndian());
				
				float sampleRate = in.readFloat();
				int bits = in.readInt();
				int channels = in.readInt();
				boolean isBigEndian = in.readBoolean();
				
				AudioFormat audioFormat2 = new AudioFormat(sampleRate, bits, channels, true, isBigEndian);
//				audioFormat = new AudioFormat(null, eetop, priority, priority, priority, eetop, daemon);
				//System.out.println(audioFormat.toString());
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
		client.connectToServer("localhost");
	}

}
