package com.musichub.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.convert.DecodedMpegAudioInputStream;

public class TestMp3Player {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String mp3File = "/Users/mhlee/Dropbox/class/2015_spring_cs244/code/data/ratherbe.mp3";
		TestMp3Player player = new TestMp3Player();
		try {
			byte[] buffer = player.testPlay(mp3File);
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] testPlay(String filename) throws UnsupportedAudioFileException, IOException {
		SourceDataLine sourceDataLine = null;
		
		ByteArrayOutputStream f = new ByteArrayOutputStream();
		File file = new File(filename);
		AudioInputStream in = AudioSystem.getAudioInputStream(file);
		AudioFormat baseFormat = in.getFormat();
//		AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
//				baseFormat.getSampleRate(), 16,
//				baseFormat.getChannels(), baseFormat.getChannels() * 2,
//				baseFormat.getSampleRate(), true);
//		System.out.println(baseFormat);
		
		AudioFormat playFormat = new AudioFormat(baseFormat.getSampleRate(), 16, 2, true, false);
		
		//44100.0, 16, 2, false
		try {
			DataLine.Info info = new DataLine.Info(
					SourceDataLine.class, playFormat);
			sourceDataLine = 
					(SourceDataLine) AudioSystem.getLine(info);
			sourceDataLine.open(playFormat);
			sourceDataLine.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		
		}
		
		//if(1==1) return null;
		
		DecodedMpegAudioInputStream decoder = new DecodedMpegAudioInputStream(playFormat, in);
		try {
			byte[] byteData = new byte[1024];
			int nBytesRead = 0;
			int offset = 0;
			while (nBytesRead != -1) {
				System.out.println(offset);
				nBytesRead = decoder.read(byteData, offset, byteData.length);
				
				sourceDataLine.write(byteData, 0, nBytesRead);
				
				if (nBytesRead != -1) {
					int numShorts = nBytesRead >> 1;
					for (int j = 0; j < numShorts; j++) {
						f.write(byteData[j]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			decoder.close();
		}
		byte[] buffer = new byte[f.size()];
		buffer = f.toByteArray();
		f.close();
		return buffer;
	}

}
