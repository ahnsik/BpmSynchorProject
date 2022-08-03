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

public class WavPlay extends Thread {

	private final static int PLAYING_BUFFER_SIZE = 1024;
	private AudioFormat audioFormat = null;
	private AudioInputStream audioStream = null;
	private SourceDataLine sourceLine = null;

	private boolean running_state = false;
	private boolean pause_state = false;

	private byte[] wavBuffer = null;

	public WavPlay() {
		running_state = false;
		pause_state = false;
		wavBuffer = null;
	}
	public WavPlay(byte[] buffer) {
		running_state = false;
		pause_state = false;
		wavBuffer = buffer;
	}
	public WavPlay(byte[] headerBuffer, byte[] wavDataBuffer) {
		running_state = false;
		pause_state = false;
		wavBuffer = ByteBuffer.allocate(headerBuffer.length + wavDataBuffer.length)
	            .put(headerBuffer)
	            .put(wavDataBuffer)
	            .array();
	}


	public void run() {
		// TODO Auto-generated method stub
		System.out.println("before running.");
	    int nBytesRead = 0;
	    int nBytesWritten = 0;
	    long totalRead = 0;
	    int playing_sec = 0;
	    int sample_rate = (wavBuffer[24]&0xFF)+((wavBuffer[25]&0xFF)<<8);		//( ((long)(Header[25]&0xFF)<<8)+Header[24]) );	// = 4바이트 little endian

	    byte[] abData = new byte[PLAYING_BUFFER_SIZE];
	    
		while (running_state) {

			if ( !pause_state ) {
				try {
					nBytesRead = audioStream.read(abData, 0, abData.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (nBytesRead > 0) {
					nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
				} else {			//	if (nBytesRead <= 0) {
					break;
				}
				totalRead+=nBytesRead;
				playing_sec = (int)((totalRead*1000) / (sample_rate*4));
				System.out.println("Playing.."+totalRead + "= " + (playing_sec/60000) + ":" + (playing_sec%60000)/1000 + "." + (playing_sec%1000) );
			} else {		// Pause 상태에서는 Thread 가 무한 loop 로 잡고 있으면 안되니까,  sleep 처리
				try {
					System.out.println("Thread sleeping...");
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("After running.");
		running_state = false;

		sourceLine.drain();
		sourceLine.close();
	}

	public void pause() {
//		running_state = true;
		pause_state = true;
	}

	public void restart() {
//		running_state = true;
		pause_state = false;
	}

	public void play() {
		if (wavBuffer==null)
			return;

		try {
			audioStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(wavBuffer));
			audioFormat = audioStream.getFormat();
			
			System.out.println("isBigEndian="+audioFormat.isBigEndian()+", channels="+audioFormat.getChannels()+", encoding="+audioFormat.getEncoding());
			System.out.println("   , frameRate="+audioFormat.getFrameRate()+", frameSize="+audioFormat.getFrameSize()+", sampleRate="+audioFormat.getSampleRate()+", sampleSizeInBits="+audioFormat.getSampleSizeInBits());
//			audioFormat.bigEndian			//	Indicates whether the audio data is stored in big-endian or little-endian order.
//			audioFormat.channels			// The number of audio channels in this format (1 for mono, 2 for stereo).
//			audioFormat.encoding		// The audio encoding technique used by this format.
//			audioFormat.frameRate		//	The number of frames played or recorded per second, for sounds that have this format.
//			audioFormat.frameSize		//The number of bytes in each frame of a sound that has this format.
//			audioFormat.sampleRate		//	The number of samples played or recorded per second, for sounds that have this format.
//			audioFormat.sampleSizeInBits	//	The number of bits in each sample of a sound that has this format.
			
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

//		int num_of_channel = (wavBuffer[22]&0xFF)+((wavBuffer[23]&0xFF)<<8);
//		int num_bits_of_sample = (wavBuffer[34]&0xFF)+((wavBuffer[35]&0xFF)<<8);
//		int SampleRate = (wavBuffer[24]&0xFF)+((wavBuffer[25]&0xFF)<<8);
//		int block_align = ((wavBuffer[33]&0xFF)<<8)+(wavBuffer[32]&0xFF);		// 1개 Sample 당 byte 수. (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel

		running_state = true;
		pause_state = false;
		System.out.println("WAVE Playing...");

	}

}
