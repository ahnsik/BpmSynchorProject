package myproject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WavPlay extends Thread { //implements Runnable {

	private final static int PLAYING_BUFFER_SIZE = 512;
	private AudioFormat audioFormat = null;
	private AudioInputStream audioStream = null;
	private SourceDataLine sourceLine = null;

	private byte[] wavBuffer = null;

	public WavPlay() {
		wavBuffer = null;
	}
	public WavPlay(byte[] buffer) {
		wavBuffer = buffer;
	}
	public WavPlay(byte[] headerBuffer, byte[] wavDataBuffer) {
		wavBuffer = ByteBuffer.allocate(headerBuffer.length + wavDataBuffer.length)
	            .put(headerBuffer)
	            .put(wavDataBuffer)
	            .array();
	}

//	public void run() {
//		// TODO Auto-generated method stub
//	}

	public void play() {
		if (wavBuffer==null)
			return;
		
		System.out.println("WAVE Playing...");
		try {
			audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(wavBuffer));
			audioFormat = audioStream.getFormat();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	    if(audioStream == null || audioStream.equals(null) ) {
	        System.out.println("WARNING: Stream read by byte array is null!");
	    }

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
           sourceLine = (SourceDataLine) AudioSystem.getLine(info);
           sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
           e.printStackTrace();
           System.exit(1);
        } catch (Exception e) {
           e.printStackTrace();
           System.exit(1);
        }

       if (sourceLine==null)
    	   return;
       sourceLine.start();

       System.out.println("Ready.");

       int nBytesRead = 0;
       long totalRead = 0;
       int playing_sec = 0;
       int sample_rate = (wavBuffer[24]&0xFF)+((wavBuffer[25]&0xFF)<<8);				//( ((long)(Header[25]&0xFF)<<8)+Header[24]) );	// = 4바이트 little endian

//		int num_of_channel = (wavBuffer[22]&0xFF)+((wavBuffer[23]&0xFF)<<8);
//		int num_bits_of_sample = (wavBuffer[34]&0xFF)+((wavBuffer[35]&0xFF)<<8);
//		int SampleRate = (wavBuffer[24]&0xFF)+((wavBuffer[25]&0xFF)<<8);
//		int block_align = ((wavBuffer[33]&0xFF)<<8)+(wavBuffer[32]&0xFF);		// 1개 Sample 당 byte 수. (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel

       byte[] abData = new byte[PLAYING_BUFFER_SIZE];
       while (nBytesRead != -1) {
           try {
               nBytesRead = audioStream.read(abData, 0, abData.length);
           } catch (IOException e) {
               e.printStackTrace();
           }
           if (nBytesRead >= 0) {
               int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
           }
	   		totalRead+=nBytesRead;
	   		playing_sec = (int)((totalRead*1000) / (sample_rate*4));
	   		System.out.println("Playing.."+totalRead + "= " + (playing_sec/60000) + ":" + (playing_sec%60000)/1000 + "." + (playing_sec%1000) );
       }
		System.out.println("WAVE Done");

       sourceLine.drain();
       sourceLine.close();
	}

}
