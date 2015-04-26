
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class TheMusicPlayer {

  public static void play(final InputStream inputStream) {
    new Thread() {
      @Override
      public void run() {
        AudioInputStream audioInputStream = null;
        try {
          audioInputStream = AudioSystem
              .getAudioInputStream(new BufferedInputStream(inputStream));
        } catch (UnsupportedAudioFileException e) {
          e.printStackTrace();
          return;
        } catch (IOException e) {
          e.printStackTrace();
          return; 
        }

        SourceDataLine sourceDataLine = null;
        try {
          AudioFormat audioFormat 
              = audioInputStream.getFormat();
          DataLine.Info info = new DataLine.Info(
              SourceDataLine.class, audioFormat);
          sourceDataLine = 
              (SourceDataLine) AudioSystem.getLine(info);
          sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
          e.printStackTrace();
          return;
        }

        sourceDataLine.start();
        byte[] data = new byte[52428];// 128Kb
        try {
          int bytesRead = 0;
          while (bytesRead != -1) {
        	  System.out.println("hi");
            bytesRead = 
                audioInputStream.read(data, 0, data.length);
            if (bytesRead >= 0)
              sourceDataLine.write(data, 0, bytesRead);
            try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
        } catch (IOException e) {
          e.printStackTrace();
          return;
        } finally {
          sourceDataLine.drain();
          sourceDataLine.close();
        }
      }
    }.start();
  }

  public static void play(final String wavFile)
      throws FileNotFoundException {
    play(new FileInputStream(wavFile));
  }
  
  public static void main(String[] argv){
	  try {
		TheMusicPlayer.play("/Users/mac/Dropbox/timetolove.wav");
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}