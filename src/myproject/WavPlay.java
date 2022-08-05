package myproject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

public class WavPlay extends Thread {

	private final static int PLAYING_BUFFER_SIZE = 1024;
	private AudioFormat audioFormat = null;
	private AudioInputStream audioStream = null;
	private SourceDataLine sourceLine = null;

	private boolean running_state = false;
	private boolean pause_state = false;

	private JPanel viewToDraw = null;
	private byte[] wavBuffer = null;
	private int playing_position = 0;
	private int sample_rate = 0;
	private int numChannels = 0;
	private int numBitsInSample = 0;
/*
	public WavPlay() {
		running_state = false;
		pause_state = false;
		wavBuffer = null;
		numChannels= 0;
		numBitsInSample = 0;
		sample_rate = 0;
	}
	public WavPlay(byte[] buffer) {
		running_state = false;
		pause_state = false;
		wavBuffer = buffer;
		numChannels= ((wavBuffer[23]&0xFF)<<8)+(wavBuffer[22]&0xFF);
		numBitsInSample = ((wavBuffer[35]&0xFF)<<8)+(wavBuffer[34]&0xFF);
		sample_rate = (wavBuffer[24]&0xFF)+((wavBuffer[25]&0xFF)<<8);
	}	
	public WavPlay(File f) {
		FileInputStream is;
		is = new FileInputStream( f );
		int byteRead = -1;
		byteRead = is.read(wavBuffer);
		is.close();
		numChannels= ((wavBuffer[23]&0xFF)<<8)+(wavBuffer[22]&0xFF);
		numBitsInSample = ((wavBuffer[35]&0xFF)<<8)+(wavBuffer[34]&0xFF);
		sample_rate = (wavBuffer[24]&0xFF)+((wavBuffer[25]&0xFF)<<8);
	}	*/
	public WavPlay(byte[] headerBuffer, byte[] wavDataBuffer) {
		running_state = false;
		pause_state = false;

		numChannels= ((headerBuffer[23]&0xFF)<<8)+(headerBuffer[22]&0xFF);
		numBitsInSample = ((headerBuffer[35]&0xFF)<<8)+(headerBuffer[34]&0xFF);
		sample_rate = (headerBuffer[24]&0xFF)+((headerBuffer[25]&0xFF)<<8);

		wavBuffer = ByteBuffer.allocate(headerBuffer.length + wavDataBuffer.length)
	            .put(headerBuffer)
	            .put(wavDataBuffer)
	            .array();
	}


	public void run() {
		// TODO Auto-generated method stub
		System.out.println("before running.");
	    int nBytesWritten = 0;
	    long playing_sec = 0;

	    byte[] abData = new byte[PLAYING_BUFFER_SIZE];
	    
		while (running_state) {

			if ( !pause_state ) {
				// 원래는..	nBytesRead = audioStream.read(abData, 0, abData.length);
				System.arraycopy(wavBuffer, playing_position+44, abData, 0, PLAYING_BUFFER_SIZE);
				if ( (playing_position+PLAYING_BUFFER_SIZE) < wavBuffer.length ) {
					nBytesWritten = sourceLine.write(abData, 0, PLAYING_BUFFER_SIZE);
					playing_position += nBytesWritten;	//PLAYING_BUFFER_SIZE;
				} else {			//	if (nBytesRead <= 0) {
					break;
				}
				playing_sec = getPlayingPositionInMilliSecond();
				System.out.println("Playing.."+playing_position+ "= " + (playing_sec/60000) + ":" + (playing_sec%60000)/1000 + "." + (playing_sec%1000) );

				if (viewToDraw!=null) {
					viewToDraw.repaint();
				}
				
			} else {		// Pause ���¿����� Thread �� ���� loop �� ��� ������ �ȵǴϱ�,  sleep ó��
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

		running_state = true;
		pause_state = false;
		System.out.println("WAVE Playing...");

	}
	
	public void setView(JPanel v) {
		System.out.println("view set done..");
		viewToDraw = v;	
	}
	
	public void setPlayingPosition(int position) {
		// TODO Auto-generated method stub
			System.out.println("Ready 안됐나 보네.. 아마도 평생 그럴꺼야.. ㅏ바하ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ");
		playing_position /= 2;
	}

	public void setPlayingPositionWithMilliSecond(int milliSec) {
		playing_position = (milliSec * sample_rate * (numChannels*(numBitsInSample/8)) ) / 1000;
	}

	public int getPlayingPosition() {
		return playing_position;
	}

	public long getPlayingPositionInMilliSecond() {
		return (((long)playing_position*1000)/(numChannels*(numBitsInSample/8)) / (sample_rate));
//		return ((playing_position*1000) / ((numChannels*(numBitsInSample/8))*(sample_rate)) );
	}
}
