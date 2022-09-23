package myproject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

/**
 *  WAVE 데이터를 draw 하고, 또한 편집할 수 있는 Pane 객체.
 *  - 마우스 휠에 의해 확대/축소, 마우스 드래그에 의해 Scroll, 키 입력으로 데이터 입력 등...
 */
public class WaveSynchPane extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 7740732731922064721L;
	private static final int X_OFFSET = 80;
	private static final int X_PADDING = 16;
	private static final int Y_PADDING = 10;
	private static final int FONT_HEIGHT = 28;
	private static final int RULER_THICKNESS = 16;
	
	private static final int TECHNIC_AREA_THICKNESS = FONT_HEIGHT*2;
	private static final int TAB_AREA_HEIGHT = 66;
	private static final int CHORD_AREA_THICKNESS = FONT_HEIGHT;
	private static final int LYRIC_AREA_THICKNESS = FONT_HEIGHT;

	/** drawing canvas 의 전체크기 */
	private int canvas_width;
	private int canvas_height;

	/**	기본적으로 사용될 폰트 및 각종 색상들 배경색 포함 */
	private static Font gridFont, labelFont, tabFont;	
	private static Color bg_color, rulerColor, rulerFontColor, playedWaveFormColor, waveFormColor, beatBgColor, beatBgColor_H, lyricAreaColor, lyricAreaColor_H, chordAreaColor, chordAreaColor_H, technicAreaColor, technicAreaColor_H;  

	/** WAVE 파형을 그리는 데 사용될 기준 값들. WAVE 파형을 기준으로 삼아 msec 단위 (samples 갯수) 로 시간 계산을 하여 grid 크기를 결정한다.   
	 * 	매우 중요한 변수samples_per_quaver, samples_per_pixel   
	 */
	private float	samples_per_quaver;			// 8분음표 1개의 길이 (sample 갯수 --> 시간계산에 매우 중요)
	private int		samples_per_pixel;	// 1픽셀당 audio sample 의 갯수 --> 확대 축소 표시에 따라 달라질 것. WAVE 파형 그리는 것을 기준으로 시간계산에 유용하게 사용 됨.

	/** 아래의 4개 변수들은 값이 바뀌게 되면 grid 간격을 다시 계산해야 하므로, 항상 연동하여 변경되거나 계산되어야 한다. 
	 *  따라서 반드시 private 로 선언하고 함수를 통해 값을 변경하며, 변경될 때 마다 관련 값들을 재계산 해 주어야 함.
	 */ 
	private String value_meter;		// = "4/4";	// '1'=2/4, '2'=3/4, '3'=4/4, '4'=6/8 
	// 8분음표 기준인지, 16분음표 기준인지 ... <--  이거 없애고, TODO: 1 마디당 음표 갯수를 표시하는 다른 변수를 사용하도록 고칠 것.
	private int value_beat = 0;	//	'0' = quaver(8분음표), '1'=semi-quaver(16분음표) 
	// 연주할 음악의 Beat Per Munite 값. 값이 크면 빠르고 반응이 좋겠지만 어렵고, 값이 작으면 노래중 위치 찾기가 어렵다.
	private float value_bpm = 80.0f;	// beats per minute.
	// WAV파일 데이터의 녹음된 SampleRate ==>> 현재는 load 할 때 기본으로 8000Hz 로 변환해서 설정해 주고 있다. 나중엔 다양한 SampleRate 에 대응하도록 고려할 것.
	private int sample_rate = 8000;

	/** 
	 * UKE 파일 편집에 유용하게 사용되는 변수들 
	 */
	// 박자(마디)에 따라 구분되는 색상. - 마디 첫 음은 별도의 색으로 구분해서 표시해 주어야 보기 쉽고 음표를 편집하기 좋다.
	private int quaver_mode = 8;
	// WAV 파형을 스크롤 해서 표시할 수 있는 end limit (현재는 default 로 1개 음으로 해 놓았는데...)
	private int maximum_start_index = (int)samples_per_quaver*quaver_mode;
	// 스크롤함에 다라 WAV파형을 draw 할 첫번째 sample 인덱스.
	private int start_index = 0;
	// 연주가 시작되고 나면, 현재까지 연주된 위치 == 앞으로 연주될 Sample의 index. (지금은 구현 안되어 있음)
	private int playing_position = 0;

	/**
	 *  음악 파일의 WAVE 데이터
	 */
	private byte[] wave_data;
	// wav데이터를 연주할 플레이어. 
	private WavPlay	player;
			// BPM 설정을 확인해 보려고 하는 임시 Sample 들..
			private byte[] wave_60bpm, wave_61bpm, wave_62bpm, wave_63bpm;

	/**
	 *  편집할 uke 데이터.  
	 */
	private UkeData uke_data = null;


	/**
	 * 생성자 - WAVE 데이터를 draw 하고, 또한 편집할 수 있는 Pane 객체.
	 */
	public WaveSynchPane() {
		canvas_width = getWidth();
		canvas_height = getHeight();

		gridFont = new Font("Monospaced", Font.BOLD, 9);
		labelFont =  new Font("Tahoma", Font.PLAIN, 12);
		tabFont =  new Font("Tahoma", Font.BOLD, 12);

		bg_color = new Color(232, 240, 248);
		rulerColor = Color.LIGHT_GRAY;
		rulerFontColor = Color.GRAY;
		playedWaveFormColor = Color.LIGHT_GRAY;
		waveFormColor = Color.GRAY;
		beatBgColor = new Color(220,220,220);
		beatBgColor_H = new Color(220,230,246);
		lyricAreaColor = new Color(240,208,208);
		lyricAreaColor_H = new Color(248,216,216);
		chordAreaColor = new Color(240,240,180);
		chordAreaColor_H = new Color(240,250,180);
		technicAreaColor = new Color(200,240,220);
		technicAreaColor_H = new Color(200,250,230);  

		// 임시로, BPM 값이 정상으로 반영되어 동작하는지 확인하기 위한  임시 Sample 데이터들을 준비.
		File file60bpm = new File("C:\\Users\\\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\60BPM_Drum_Beat_3min_8000hz.wav");
		ReadWaveData(file60bpm);
		wave_60bpm = new byte[wave_data.length];
		System.arraycopy(wave_data, 0, wave_60bpm, 0, wave_data.length);
		System.out.println("60bpm : " + wave_60bpm[0]);
		File file61bpm = new File("C:\\Users\\\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\61bpm_metronome_drum_8000hz_8bitMono.wav");
		ReadWaveData(file61bpm);
		wave_61bpm = new byte[wave_data.length];
		System.arraycopy(wave_data, 0, wave_61bpm, 0, wave_data.length);
		System.out.println("61bpm : " + wave_61bpm[0]);
		File file62bpm = new File("C:\\Users\\\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\62bpm_Simple_Rock_Drum_Groove_8000hz_8bitMono.wav");
		ReadWaveData(file62bpm);
		wave_62bpm = new byte[wave_data.length];
		System.arraycopy(wave_data, 0, wave_62bpm, 0, wave_data.length);
		System.out.println("62bpm : " + wave_62bpm[0]);
		File file63bpm = new File("C:\\Users\\\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\63 bpm metronome drum.wav");
		ReadWaveData(file63bpm);
		wave_63bpm = new byte[wave_data.length];
		System.arraycopy(wave_data, 0, wave_63bpm, 0, wave_data.length);
		System.out.println("63bpm : " + wave_63bpm[0]);

		// set variables to default values.
		samples_per_pixel = 100;		// 마우스 휠 등에 의해 확대/축소되는 기준.  1픽셀 당 100개 sample로 표시.  8000Hz 샘플이므로, 1초=80 pixel 간격.
		// 박자 값. string 으로 바꿔서 사용할 예정
		value_meter = "4/4"; 
		value_beat = 0;	//	나중에 삭제할 예정인 변수. 8분음표 기준인지, 16분음표 기준인지 판단용. 
		// 본격 BPM 값 
		value_bpm = 80.0f;	// beats per minute.
		sample_rate = 8000;
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
		playing_position = 0;
	}

	/**
	 * *.wav파일로 부터 직접 읽어서 데이터를 준비함.	- 임시로 사용 ?? -  
	 * @param f	 파일 핸들러 (*.wav 파일만 사용함)
	 */
	protected void ReadWaveData(File f) {
		// TODO Auto-generated method stub
		byte[] Header = new byte[44];
		byte[] Buffer = new byte[(int)f.length()];

		try {
			FileInputStream is;
			is = new FileInputStream( f );

			int byteRead = -1;
			byteRead = is.read(Header);			// *.wav���� ������� https://anythingcafe.tistory.com/2
			//System.out.println("Header Chunk Size:"+( ((Header[17]&0xFF)<<8)+(Header[16]&0xFF)) );	// = 4����Ʈ ������ Header ũ��� �׸� ũ�� �����Ƿ�, ���� 2����Ʈ��. (little endian)
			//System.out.println("num of Channel:"+( ((Header[23]&0xFF)<<8)+(Header[22]&0xFF)) );	// ä�� �� : 1=Mono, 2=Stereo, 5:4channel. 6:6channel, etc..
			//System.out.println("Sample Rate:"+ ((Header[24]&0xFF)+((Header[25]&0xFF)<<8)) );				//( ((long)(Header[25]&0xFF)<<8)+Header[24]) );	// = 4����Ʈ little endian
			//System.out.println("byte rate =1�ʴ� byte ��:"+( ((Header[31]&0xFF)<<24)+((Header[30]&0xFF)<<16)+((Header[29]&0xFF)<<8)+(Header[28]&0xFF)) );	
			//System.out.println("num bits per Sample :"+( ((Header[35]&0xFF)<<8)+(Header[34]&0xFF)) );	
			//System.out.println("Block Align:"+( ((Header[33]&0xFF)<<8)+(Header[32]&0xFF)) );		// 	

			byteRead = is.read(Buffer);
			is.close();

			//int num_of_channel = (Header[22]&0xFF)+((Header[23]&0xFF)<<8);
			//int num_bits_of_sample = (Header[34]&0xFF)+((Header[35]&0xFF)<<8);
			//int SampleRate = (Header[24]&0xFF)+((Header[25]&0xFF)<<8);
			int block_align = ((Header[33]&0xFF)<<8)+(Header[32]&0xFF);		// 1�� Sample �� byte ��. (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel
			System.out.println("sample stripe = "+block_align );		// 16비트 Sampling, 또는 Stereo 녹음데이터 인 경우 등에 고려됨.  	

			wave_data = new byte[byteRead];
			for (int i=0; i<byteRead; i++) {		// 지금은 임의로 무조건 8bit Mono, 8000Hz 가 아니면 안된다. 
				wave_data[i] = Buffer[i];
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * PANE 에 WAV 파형과, grid 및 UKE 데이터 정보를 Draw 함.
	 */
	public void paintComponent(Graphics g) {
		canvas_width = getWidth();
		canvas_height = getHeight();

		g.setColor(bg_color);
		g.fillRect( 0, 0, canvas_width, canvas_height );

		drawBGGrid(g, X_OFFSET, Y_PADDING, canvas_width-X_OFFSET-X_PADDING, canvas_height-Y_PADDING-Y_PADDING );

		// ��� Ruler
		drawRuler(g, X_OFFSET, Y_PADDING/2, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);
		// �ϴ� Ruler
		drawRuler(g, X_OFFSET, canvas_height-RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);

		int ypos = canvas_height-RULER_THICKNESS;
		// ���ֱ��, Stroke ����, etc.. 
		drawTechnicArea(g, X_OFFSET, ypos-TECHNIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, TECHNIC_AREA_THICKNESS );
		ypos -= TECHNIC_AREA_THICKNESS;
		// TAB �Ǻ� �׸��� -
		drawTABArea(g, X_OFFSET, ypos-TAB_AREA_HEIGHT, canvas_width-X_OFFSET-X_PADDING, TAB_AREA_HEIGHT );
		ypos -= TAB_AREA_HEIGHT;
		// Chord Area
		drawChordArea(g, X_OFFSET, ypos-CHORD_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, CHORD_AREA_THICKNESS);
		ypos -= CHORD_AREA_THICKNESS;
		// Lyric Area
		drawLyricArea(g, X_OFFSET, ypos-LYRIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, LYRIC_AREA_THICKNESS);
		ypos -= LYRIC_AREA_THICKNESS;

		// WAVEFORM draw
		drawWaveData(g, X_OFFSET, 10+RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, ypos-10-RULER_THICKNESS*2 );

		g.dispose();
	}

	/**
	 * 페인의 배경을 그림. grid 눈금도 그림.
	 * @param g	
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void drawBGGrid(Graphics g, int x, int y, int w, int h) {
		g.setColor(rulerColor);
		g.fillRect(x, y, w-1, RULER_THICKNESS);						// 윗쪽 Ruler 배경
		g.fillRect(x, y+h-RULER_THICKNESS, w-1, RULER_THICKNESS);	// 아랫쪽 Ruler 배경

		int i, start;		//, j, end;
		boolean grid = false, quaver_grid = false;		// grid : 눈금 경계인지 아닌지,  quaver_grid : 마디의 첫음인지 아닌지.

		for (i=0; i<w; i++) {
			start = ((samples_per_pixel*i)+start_index);
//			end = start + samples_per_pixel;		// (int)((samples_per_pixel*(i+1))+start_index);
			grid=false;
			quaver_grid = false;
//			for (j=start; j<end; j++) {
//				if (j% (int)samples_per_quaver==0)
//					grid=true;
//				if ( (int)(j/samples_per_quaver)%quaver_mode == 0)
//					quaver_grid = true;
//			}

			if ( (start%(int)samples_per_quaver) < samples_per_pixel) {			//  if ((start/(int)samples_per_quaver)!=(end/(int)samples_per_quaver)) {
				grid=true;
			}
			if ( ((start/(int)samples_per_quaver)%quaver_mode) == 0 ) {
				quaver_grid = true;
			}
			drawSingleVerticalLine(g, x+i, y, h, grid, quaver_grid);
		}

	}

	private void drawSingleVerticalLine(Graphics g, int x, int y, int h, boolean isGrid, boolean isQuaver) {
		if (isGrid) {
			// time_bar (ruler)
			g.setColor(Color.DARK_GRAY);	// 눈금 색깔
			if (isQuaver) {
				g.drawLine(x, y+2, x, y+RULER_THICKNESS);
				g.drawLine(x, y+h-RULER_THICKNESS, x, y+h-RULER_THICKNESS+16);
			} else {
				g.drawLine(x, y+8, x, y+RULER_THICKNESS);
				g.drawLine(x, y+h-RULER_THICKNESS, x, y+h-RULER_THICKNESS+8);
			}
			// waveform BG
			g.setColor(Color.WHITE);
			g.drawLine(x, y+RULER_THICKNESS, x, y+h-RULER_THICKNESS);
			// 
		} else {
			// waveform BG
			if (isQuaver) {
				int yend = y+h-RULER_THICKNESS;
				g.setColor(technicAreaColor_H);
				g.drawLine(x, yend-TECHNIC_AREA_THICKNESS, x, yend);
				yend -= TECHNIC_AREA_THICKNESS;
				g.setColor(beatBgColor_H);
				g.drawLine(x, yend-TAB_AREA_HEIGHT, x, yend);
				yend -= TAB_AREA_HEIGHT;
				g.setColor(chordAreaColor_H);
				g.drawLine(x, yend-CHORD_AREA_THICKNESS, x, yend);
				yend -= CHORD_AREA_THICKNESS;
				g.setColor(lyricAreaColor_H);
				g.drawLine(x, yend-LYRIC_AREA_THICKNESS, x, yend);
				yend -= LYRIC_AREA_THICKNESS;
				g.setColor(beatBgColor_H);
				g.drawLine(x, y+RULER_THICKNESS, x, yend);
			} else {
				int yend = y+h-RULER_THICKNESS;
				g.setColor(technicAreaColor);
				g.drawLine(x, yend-TECHNIC_AREA_THICKNESS, x, yend);
				yend -= TECHNIC_AREA_THICKNESS;
				g.setColor(beatBgColor);
				g.drawLine(x, yend-TAB_AREA_HEIGHT, x, yend);
				yend -= TAB_AREA_HEIGHT;
				g.setColor(chordAreaColor);
				g.drawLine(x, yend-CHORD_AREA_THICKNESS, x, yend);
			yend -= CHORD_AREA_THICKNESS;
				g.setColor(lyricAreaColor);
				g.drawLine(x, yend-LYRIC_AREA_THICKNESS, x, yend);
				yend -= LYRIC_AREA_THICKNESS;
				g.setColor(beatBgColor);
				g.drawLine(x, y+RULER_THICKNESS, x, yend);
			}
		}
	}
	

	public void drawRuler(Graphics g, int x, int y, int w, int h) {
		int i;
		boolean grid;	

		String time_string= "";
		int time_msec = 0, prev_t = 0;
		int devider = 1000;
		
		if (samples_per_pixel <= 50) {
			devider = 500;
		} else if (samples_per_pixel <= 100) {
			devider = 1000;
		} else if (samples_per_pixel <= 200) {
			devider = 2000;
		} else {
			devider = 4000;
		}

		g.setFont(gridFont);
		g.setColor(rulerFontColor);
		for ( i=0; i<w; i++) {
			int start = ((samples_per_pixel*i)+start_index);
			int end = ((samples_per_pixel*(i+1))+start_index);

			time_msec = (start*1000/sample_rate);			

			grid=false;
			if ((start/(int)samples_per_quaver)!=(end/(int)samples_per_quaver)) {
				grid=true;
			}
			if (grid) {
				if ((prev_t/devider) != (time_msec/devider)) {
					time_string = String.format("%d:%02d.%03d", (time_msec/60000), (time_msec/1000)%60, time_msec%1000 );
					g.drawLine(x+i, y, x+i, y+h-1);
					g.drawString( time_string, 4+x+i, y+h-2 );
					prev_t = time_msec;
				}
			}
		}

	}

	public void drawWaveData(Graphics g, int x, int y, int w, int h) {
		String label="waveform:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		drawWave(g, x, y, w, h, wave_63bpm, Color.BLUE );
		drawWave(g, x, y, w, h, wave_62bpm, Color.CYAN );
		drawWave(g, x, y, w, h, wave_61bpm, Color.GREEN );
		drawWave(g, x, y, w, h, wave_60bpm, Color.MAGENTA );
		
		//drawWave(g, x, y, w, h, wave_data, waveFormColor );
	}

	private void drawWave(Graphics g, int x, int y, int w, int h, byte[] data, Color c) {
		g.setColor(c);
		int j, value, max, min, prev_min, start, end ;
		int center_y = y;
		int max_amplitude = h/2;
		if (data!=null) {
			max=0;
			min=255;
			for (int i=0; i<w; i++) {
				start = ((samples_per_pixel*i)+start_index);
				end = start+samples_per_pixel;
				if(end >= data.length ) 
					end = data.length;
				prev_min = min;
				max=0;
				min=255;
				for (j=start; j<end; j++) {
					value = data[j]&0xFF;
					max=(max<value)?value:max;
					min=(min>value)?value:min;
				}
				g.drawLine( (x+i), center_y+ min*max_amplitude/128, (x+i), center_y+max*max_amplitude/128 );
				g.drawLine( (x+i), center_y+ prev_min*max_amplitude/128, (x+i), center_y+max*max_amplitude/128 );
			}
		}

	}

	public void drawChordArea(Graphics g, int x, int y, int w, int h) {

		String label="chord:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);
/*
		int i, j;
		if (uke_data == null)
			return;
		for (i=0; i<w; i++) {
			int start = ((samples_per_pixel*i)+start_index);
			int end = ((samples_per_pixel*(i+1))+start_index);

			for (j=0; j<uke_data.notes.length; j++) {
				if ( (uke_data.notes[j].timeStamp*sample_rate/1000 >= start)&&(uke_data.notes[j].timeStamp*sample_rate/1000 < end)) {
					g.setColor(Color.BLACK);
					g.drawString(uke_data.notes[j].chordName, x+i, y+FONT_HEIGHT);
				}
			}
		}
*/
	}

	public void drawLyricArea(Graphics g, int x, int y, int w, int h) {
		String label="lyric:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		int i, j;
		if (uke_data == null)
			return;
		for (i=0; i<w; i++) {
			int start = ((samples_per_pixel*i)+start_index);
			int end = ((samples_per_pixel*(i+1))+start_index);

			if (uke_data.notes==null) {
				continue;
			}
			for (j=0; j<uke_data.notes.length; j++) {
				if ( (uke_data.notes[j].timeStamp*sample_rate/1000 >= start)&&(uke_data.notes[j].timeStamp*sample_rate/1000 < end)) {
					g.setColor(Color.BLACK);
					g.drawString(uke_data.notes[j].lyric, x+i, y+FONT_HEIGHT);
//					System.out.println("TS:"+uke_data.notes[j].timeStamp+"  "+uke_data.notes[j].lyric );
				}
			}
		}

	}
	
	public void drawTABArea(Graphics g, int x, int y, int w, int h) {
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		g.drawString("G", x-16, y+16);
		g.drawString("C", x-16, y+16+16);
		g.drawString("E", x-16, y+16+32);
		g.drawString("A", x-16, y+16+48);

		int i, j;
		boolean grid, quaver_grid;

		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y+9   , w-1, 2);		// G
		g.fillRect(x, y+9+16, w-1, 2);		// C
		g.fillRect(x, y+9+32, w-1, 2);		// E
		g.fillRect(x, y+9+48, w-1, 2);		// A

		for (i=0; i<w; i++) {
			int start = ((samples_per_pixel*i)+start_index);
			int end = ((samples_per_pixel*(i+1))+start_index);
			grid=false;
			quaver_grid = false;
//			for (j=start; j<end; j++) {
//				if (j% (int)samples_per_quaver==0)
//					grid=true;
//				if ( (int)(j/samples_per_quaver)%quaver_mode == 0)
//					quaver_grid = true;
//			}
			if ((start/(int)samples_per_quaver)!=(end/(int)samples_per_quaver)) {
				grid=true;
			}
			if (((end/(int)samples_per_quaver)%quaver_mode)==0 ) {
				quaver_grid = true;
			}
			
			if (grid && quaver_grid) {
				g.fillRect(x+i-2, y+9, 2, 50);
			}
		}

	}

	public void drawTechnicArea(Graphics g, int x, int y, int w, int h) {
		String label="technic:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);
/*
//					g.setColor(technicAreaColor_H);
//					g.setColor(technicAreaColor);
		int i, j;
		if (uke_data == null)
			return;
		for (i=0; i<w; i++) {
			int start = ((samples_per_pixel*i)+start_index);
			int end = ((samples_per_pixel*(i+1))+start_index);

			for (j=0; j<uke_data.notes.length; j++) {
				if ( (uke_data.notes[j].timeStamp*sample_rate/1000 >= start)&&(uke_data.notes[j].timeStamp*sample_rate/1000 < end)) {
					g.setColor(Color.BLACK);
					g.drawString(uke_data.notes[j].technic, x+i, y+FONT_HEIGHT);
//					System.out.println("TS:"+uke_data.notes[j].timeStamp+"  "+uke_data.notes[j].technic);
				}
			}
		}
*/
	}

	public void setWaveData(byte[] data) {
		System.out.println("WAVE DATA SET. : length=" + data.length);
		wave_data = data;
	}

	public void setUkeData(UkeData data) {
		System.out.println("Note Data SET : length=" + data.getSize() );
		uke_data = data;
	}
	
	public void setBpm(int bpm) {
		System.out.println("set BPM: " + bpm);
		value_bpm = bpm;
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.

		maximum_start_index = (int)samples_per_quaver*quaver_mode;

		// 60bpm 4/4���� 1�� = 48*8 = 384px ==> 1beat �� 48px,		60bpm��, 1/60���� ���� �ϹǷ�,   
		// 80bpm 4/4���� 1�� = 48*8 = 384px ==> 1beat �� ??px,  	80bpm�� 1/80���� �ؼ�,  1/60:48px = 1/80:??px  ??=48*60/80,  ��,  48*60/bpm ���� ���Ѵ�. 
		repaint();
	}

	public void setQuaver(int isSemiQuaver) {	// 0=8����ǥ, 1=16����ǥ
		System.out.println("set Quaver: " + isSemiQuaver);
		value_beat = isSemiQuaver;

		if (value_beat==0) {	//	'0' = quaver(8음표단위), '1'=semi-quaver(16분음표단위)
			samples_per_quaver = ( ((sample_rate*60)/value_bpm) /4)*2;
		} else {
			samples_per_quaver = ( ((sample_rate*60)/value_bpm) /4);
		}

		switch(value_meter) {
			case "2/4":		// 2/4박자
				quaver_mode = (value_beat==0)?4:8;
				break;
			case "3/4":		// 3/4박자
			case "6/8":		// 3/4박자
				quaver_mode = (value_beat==0)?6:12;
				break;
			default:	// 기본은 4/4 박자.
				quaver_mode = (value_beat==0)?8:16;
				break;
		}
		
		maximum_start_index = (int)samples_per_quaver*quaver_mode;
		repaint();
	}

	public void setMeter(int meter) {	// '0'=2/4����, '1'=3/4����, '2'=4/4����, '3'=6/8����
		switch(meter) {
			case 0:		// 2/4박자
				value_meter = "2/4";
				System.out.println("setMeter: 2/4박자.." );
				quaver_mode = (value_beat==0)?4:8;
				break;
			case 1:		// 3/4박자
				value_meter = "3/4";
				System.out.println("setMeter: 3/4박자.." );
				quaver_mode = (value_beat==0)?6:12;
				break;
			case 3:		// 6/8박자
				value_meter = "6/8";
				System.out.println("setMeter: 6/8박자.." );
				quaver_mode = (value_beat==0)?6:12;
				break;
			default:	// 4/4박자
				value_meter = "4/4";
				System.out.println("setMeter: 4/4박자.." );
				quaver_mode = (value_beat==0)?8:16;
				break;
		}
		maximum_start_index = (int)samples_per_quaver*quaver_mode;
		repaint();
	}

	public void setPlayer(WavPlay p) {
		player = p;
		sample_rate = player.getSampleRate();
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
	}
	public void setDrawStart(int milisec) {
		start_index = milisec;		// quaver 1�� ��ǥ�� �ʺ�(�ð�)�� milli-second ������ ������� �� ��. - Sampling ���ļ��� �������� ����ؾ� ��.
		repaint();
	}
	public void setPlayingPosition(int milisec) {
		playing_position = milisec;
	}

	public void keyTyped(KeyEvent e) {
		System.out.println("WaveSynchPane KeyTyped:" + e.getKeyCode() );
	}
	public void keyPressed(KeyEvent e) {
		System.out.println("WaveSynchPane KeyPressed:" + e.getKeyCode() );
	}
	public void keyReleased(KeyEvent e) {
		System.out.println("WaveSynchPane KeyReleased:" + e.getKeyCode() );
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("Mouse Wheel listener:" + e.getWheelRotation() + ", Amount:"+e.getScrollAmount() + ", type:"+e.getScrollType() );
//		if ( e.getWheelRotation() > 0) {
//			if (samples_per_pixel < 280 )
//				samples_per_pixel += 10;
//		} else if ( e.getWheelRotation() < 0) {
//			if (samples_per_pixel > 10 )
//				samples_per_pixel -= 10;
//		}
//		System.out.println("samples_per_pixel = " + samples_per_pixel);

		viewZoom( e.getWheelRotation() );
		repaint();
	}

	private static Point mousePt;
	private static int prev_start_index;

	public void mouseDragged(MouseEvent e) {
		if (mousePt==null)
			return;
		start_index = prev_start_index - (e.getX()-mousePt.x)*samples_per_pixel ;
		if (start_index < 0)
			start_index = 0;
		if (start_index >= wave_data.length-maximum_start_index)
			start_index = wave_data.length-maximum_start_index;
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
		samples_per_pixel = 100;		// 24는 8분음표 1개에 해당하는 grid 크기.
		int x = e.getX()-X_OFFSET;
		int y = e.getY();
		System.out.println("clicked=("+x+", "+y+")" );

		int start = (int)((samples_per_pixel*x)+start_index);
		int xs = (int)((float)start/samples_per_quaver);
		System.out.println("start="+start+", XS="+(int)(xs)+", WIDTH="+(int)(samples_per_quaver/samples_per_pixel)+"px" );
	}

	public void mousePressed(MouseEvent e) {
		mousePt = e.getPoint();
		prev_start_index = start_index;
		repaint();
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

////////////	
	public void viewZoom(int zoom_factor) {
//		System.out.println("Mouse Wheel listener:" + zoom_factor + ", Amount:"+e.getScrollAmount() + ", type:"+e.getScrollType() );
		if ( zoom_factor > 0) {
			if (samples_per_pixel < 280 )
				samples_per_pixel += 10;
		} else if ( zoom_factor < 0) {
			if (samples_per_pixel > 10 )
				samples_per_pixel -= 10;
		}
//		System.out.println("samples_per_pixel = " + samples_per_pixel);
		repaint();
	}

}
