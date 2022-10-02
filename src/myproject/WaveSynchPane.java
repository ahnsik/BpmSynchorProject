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

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.json.JSONArray;
import org.json.JSONException;

import myproject.UkeData.Note;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
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
	private int lyricStart_y, chordStart_y, tabStart_y, technicStart_y;

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
	// wave form 의 offset. 박자/음표에 따른 grid 와 실제 wave 파일의 연주가 시작되는 차이값.(오차보정)
	private int wave_offset = 0;
	
	/** 
	 * UKE 파일 편집에 유용하게 사용되는 변수들 
	 */
	// 박자(마디)에 따라 구분되는 색상. - 마디 첫 음은 별도의 색으로 구분해서 표시해 주어야 보기 쉽고 음표를 편집하기 좋다.
	private int quaver_mode = 8;
	// WAV 파형을 스크롤 해서 표시할 수 있는 end limit (현재는 default 로 1개 음으로 해 놓았는데...)
	private int maximum_start_index = (int)samples_per_quaver*quaver_mode;
	// 스크롤함에 따라라 WAV파형을 draw 할 첫번째 sample 인덱스.
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
			//private byte[] wave_60bpm, wave_61bpm, wave_62bpm, wave_63bpm;

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
		labelFont =  new Font("맑은고딕", Font.PLAIN, 12);
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
/*
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
*/
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
		// Stroke 방향, 트릭, 해머링,풀링 등의 기법 표시 영역.. 
		technicStart_y = ypos-TECHNIC_AREA_THICKNESS;
		drawTechnicArea(g, X_OFFSET, technicStart_y, canvas_width-X_OFFSET-X_PADDING, TECHNIC_AREA_THICKNESS );
		ypos -= TECHNIC_AREA_THICKNESS;
		// TAB 악보 표시 영역
		tabStart_y = ypos-TAB_AREA_HEIGHT;
		drawTABArea(g, X_OFFSET, tabStart_y, canvas_width-X_OFFSET-X_PADDING, TAB_AREA_HEIGHT );
		ypos -= TAB_AREA_HEIGHT;
		// Chord Area
		chordStart_y = ypos-CHORD_AREA_THICKNESS;
		drawChordArea(g, X_OFFSET, chordStart_y, canvas_width-X_OFFSET-X_PADDING, CHORD_AREA_THICKNESS);
		ypos -= CHORD_AREA_THICKNESS;
		// Lyric Area
		lyricStart_y = ypos-LYRIC_AREA_THICKNESS;
		drawLyricArea(g, X_OFFSET, lyricStart_y, canvas_width-X_OFFSET-X_PADDING, LYRIC_AREA_THICKNESS);
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

//		drawWave(g, x, y, w, h, wave_63bpm, Color.BLUE );
//		drawWave(g, x, y, w, h, wave_62bpm, Color.CYAN );
//		drawWave(g, x, y, w, h, wave_61bpm, Color.GREEN );
//		drawWave(g, x, y, w, h, wave_60bpm, Color.MAGENTA );
		
		drawWave(g, x, y, w, h, wave_data, waveFormColor );
	}

	private void drawWave(Graphics g, int x, int y, int w, int h, byte[] data, Color c) {
		g.setColor(c);
		int j, value, max, min, prev_min, start, end;
		int offset = wave_offset * sample_rate/1000;		// msec 를 index 로 변환, sample_rate/1000 은 msec 당 sample 갯수.
		int center_y = y;
		int max_amplitude = h/2;
		if (data!=null) {
			max=0;
			min=255;
			for (int i=0; i<w; i++) {
				start = ((samples_per_pixel*i)+offset+start_index );		// offset 값이 적용되었는데도 bpm-grid 하고 wave 모양이 동일하다고 ?? 
				if (start<0) continue;
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

		int i, j, start, end;
		if (uke_data == null)
			return;
		if (uke_data.notes != null) {
			for (i=0; i<w; i++) {
				start = ((samples_per_pixel*i)+start_index);
				end = (int)(start+samples_per_quaver);	
//				if(end >= wave_data.length )		// msec 시간 계산을 위한 중간 값이므로, wave_data 배열의 index값의 overflow 를 체크할 필요가 없다.  
//					end = wave_data.length;
				if ( (start%(int)samples_per_quaver) < samples_per_pixel) {			// grid 경계부분 판단. -		//  if ((start/(int)samples_per_quaver)!=(end/(int)samples_per_quaver)) {
					int start_msec = start*1000 / sample_rate, end_msec = end*1000 / sample_rate;
//					System.err.println(  "quaver_start:"+start_msec+"~"+end_msec );
					for (j=0; j<uke_data.notes.length; j++) {
						int timeStamp = (int) uke_data.notes[j].timeStamp;
//						System.out.println(  ",\t index:"+j + ", TS:"+timeStamp + ", lyric:"+uke_data.notes[j].lyric );
						if ((start_msec <= timeStamp) && (end_msec > timeStamp) ) {
//							System.err.println("TS:"+timeStamp +", grid:" + start_msec + ", lyric:"+uke_data.notes[j].lyric + ", index:" + j);
							g.drawRect( x+i, y, 12, FONT_HEIGHT );
							g.drawString( ""+uke_data.notes[j].lyric, x+i, y+FONT_HEIGHT );
						}
					}
				}
			}
		//} else {
		//	System.out.println("잘못된 길을 들으셨네. - notes 데이터가 존재하지 않음." + uke_data.getSize() );
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

	}

	/**
	 * *.wav 파일에서 읽어 온 오디오 데이터를 설정 - WAV 파형을 그릴 데이터를 지정.
	 * @param data	*.wav에서 읽어 온 음성데이터 (8bit unsigned, mono 데이터만 가능함)
	 */
	public void setWaveData(byte[] data) {
		if (data != null)
			System.out.println("WAVE DATA SET. : length=" + data.length);
		wave_data = data;
	}

	/**
	 * *.uke 파일에서 읽어 온 연주할 데이터를 설정 - TAB악보에 그릴 데이터를 지정함.
	 * @param readData  UkeData 객체. - 기본정보 및 악보데이터 -
	 */
	public void setUkeData(UkeData data) {
		System.out.println("Note Data SET : length=" + data.getSize() );
		uke_data = data;
	}
	
	/**
	 * 연주되는 음악의 BPM 값 설정.
	 * @param bpm  - beat per minute. 1분당 4분음표의 갯수. meter(박자)정보와 함께 마디를 구분할 수 있도록 grid 를 표시하는데 사용됨.
	 */
	public void setBpm(float bpm) {
		System.out.println("set BPM: " + bpm);
		value_bpm = bpm;
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
		maximum_start_index = (int)samples_per_quaver*quaver_mode;
		repaint();
	}

	/**
	 * 음악 파일의 기본 음표 크기 ( grid 1개 칸의 음표의 길이)
	 * @param isSemiQuaver	기본음표 크기 (문자열로 지정: default='quaver', 16분음표='semi-quaver') 
	 */
	public void setQuaver(int isSemiQuaver) {
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

	/** 
	 * 음악파일의 박자 지정 (2/4, 3/4, 4/4, 6/8)
	 * @param meter	박자 값 (문자열로 지정)
	 */
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

	/**
	 * WAVE파일의 파형을 마디 (박자,bpm) 의 간격 (grid) 에 맞춰 표시하기 위한 위치보정 값 설정.
	 * @param msec	오프셋 값(msec단위)
	 */
	public void setWaveOffset(int msec) {
		System.out.println("WAVE Offset:" + msec );
		wave_offset = msec;		
		repaint();
	}

	/**
	 * 음원파일을 재생할 플레이어 설정. - 재생 및 재생위치 확인 
	 * @param p  플레이어
	 */
	public void setPlayer(WavPlay p) {
		player = p;
		sample_rate = player.getSampleRate();
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
	}
	
	/**
	 * 
	 * @param milisec
	 */
	public void setDrawStart(int milisec) {
		start_index = milisec;
		repaint();
	}
	public void setPlayingPosition(int milisec) {
		playing_position = milisec;
	}


	public int findIndexWithTimestamp(int msec) {
		if (uke_data.notes == null) {
			return -1;
		}
		Note	notes[] = uke_data.notes;
		int 	timestamp;
		int		start_index = (int) ((int) ((msec*sample_rate/1000)/samples_per_quaver)*samples_per_quaver);	

		int		start = ((start_index*1000)/sample_rate); 
		int		end = start+ (int)(samples_per_quaver*1000/sample_rate);
		System.out.println(" CLICKED :  --  start="+start+", en="+end +"=== msec:" + msec + "samples_per_quaver:"+ samples_per_quaver  );
		System.out.println("  음표 갯수 : "+ notes.length + ", notes[0]="+notes );
		for (int i=0; i<notes.length; i++) {
			timestamp = (int) notes[i].timeStamp;
//			System.out.println("searching.. i=:"+i +", timestamp="+ timestamp );
			if ( (start <= timestamp) && (timestamp < end) ) {
				return i;
			}
		}
		return -1;
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
		viewZoom( e.getWheelRotation() );		// e.getWheelRotation() 이 1 이면 증가, -1 이면 감소.
		repaint();
	}

	private static Point mousePt;
	private static int prev_start_index;

	public void mouseDragged(MouseEvent e) {
		if (mousePt==null)
			return;
		if (wave_data==null)
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
		int sample_index, xs;
		int note_index = -1;
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
		samples_per_pixel = 100;		// 24는 8분음표 1개에 해당하는 grid 크기.
		int x = e.getX()-X_OFFSET;
		int y = e.getY();
		System.out.println("clicked=("+x+", "+y+")" );

		sample_index = (int)((samples_per_pixel*x)+start_index);
		xs = (int)((float)sample_index/samples_per_quaver);
		if (uke_data.notes == null) {
			uke_data.notes = new Note[0];
			note_index = -1;
			System.err.println("uke_data.notes  is null");
		} else {
			note_index = findIndexWithTimestamp(sample_index*1000/sample_rate);		// index to msec 공식 : index*1000/sample_rate
		}
		System.out.println("Clicked quaver_index:("+xs+"), " + (int)(xs*samples_per_quaver*1000/sample_rate) + "msec" + ", index=" + note_index );

		NoteInputDlg  editNote = new NoteInputDlg( null, "연주음 편집");
		
		if (note_index < 0 ) {
			note_index = uke_data.appendNote( (int)((xs*samples_per_quaver*1000)/sample_rate) );		// msec 위치를 계산해서 새로운 노드 추가.
		}
		editNote.setData(uke_data.notes[note_index]);
		editNote.setSize(352, 356);

		int result = editNote.showDialog();
		if (result == NoteInputDlg.OK_OPTION ) {
			Note temp = editNote.getData();
			System.out.println("Note:"+temp+", lyric:" + temp + ", TS:"+temp.timeStamp + ", chord:" + temp.chordName + ", tab:" + temp.tab );
			//uke_data.notes[0] = editNote.getData();
			// TODO:  delete this note 체크했을 때에는 해당 노드를 삭제 해야 한다.
		} else {
			
		}
		System.err.println("showDialog returns ." + result );
		repaint();


//		} else {
//			sample_index = (int)((samples_per_pixel*x)+start_index);
//			xs = (int)((float)sample_index/samples_per_quaver);
//			int index = findIndexWithTimestamp(sample_index*1000/sample_rate);		// index to msec 공식 : index*1000/sample_rate
//			if (index < 0) {
//            	int new_index = uke_data.appendNote( (int)((xs*samples_per_quaver*1000)/sample_rate) );		// msec 위치를 계산해서 새로운 노드 추가.
//            	uke_data.notes[new_index].lyric = lyricInput;		// 새로운 가사를 넣어 줌. 
//    			repaint();
//			} else {
//			}
//		}
/*		
		if (uke_data.notes == null) {
			System.err.println("No Notes array exist. - make new array." );
			uke_data.notes = new Note[0];
		}  
		if ( (y>lyricStart_y)&&(y<=(lyricStart_y+LYRIC_AREA_THICKNESS)) ) {
			sample_index = (int)((samples_per_pixel*x)+start_index);
			xs = (int)((float)sample_index/samples_per_quaver);
			int index = findIndexWithTimestamp(sample_index*1000/sample_rate);		// index to msec 공식 : index*1000/sample_rate
			System.err.println("lyric display Area Clicked :("+xs+"), " + (int)(xs*samples_per_quaver*1000/sample_rate) + "msec" + ", index="+index );
			if (index < 0) {
				System.err.println("No note data. wanna new ?? : msec=" + (xs*samples_per_quaver*1000)/sample_rate );

				String lyricInput = null;
				lyricInput = JOptionPane.showInputDialog(null, "새로운 가사입력", lyricInput );
	            if (lyricInput != null) {
//	    			System.out.println("\"" + lyricInput + "\"" + "을 입력하였습니다.");
	            	int new_index = uke_data.appendNote( (int)((xs*samples_per_quaver*1000)/sample_rate) );		// msec 위치를 계산해서 새로운 노드 추가.
	            	uke_data.notes[new_index].lyric = lyricInput;		// 새로운 가사를 넣어 줌. 
	    			repaint();
	            }
			} else {
				String lyricInput = uke_data.notes[index].lyric;
				lyricInput = JOptionPane.showInputDialog(null, "가사입력", lyricInput );
	            if (lyricInput != null) {
	    			System.out.println("\"" + lyricInput + "\"" + "을 입력하였습니다.");
	    			uke_data.notes[index].lyric = lyricInput;
	    			repaint();
	            }
			}
		} else if ( (y>chordStart_y)&&(y<=(chordStart_y+CHORD_AREA_THICKNESS)) ) {
			System.err.println("chord display Area Clicked. !!!" );
		} else if ( (y>tabStart_y)&&(y<=(tabStart_y+TAB_AREA_HEIGHT)) ) {
			System.err.println("TAB edit Area Clicked. !!!" );
		} else if ( (y>technicStart_y)&&(y<=(technicStart_y+TECHNIC_AREA_THICKNESS)) ) {
			System.err.println("technic edit Area Clicked. !!!" );
		} else {
			sample_index = (int)((samples_per_pixel*x)+start_index);
			xs = (int)((float)sample_index/samples_per_quaver);
			System.out.println("start="+sample_index+", XS="+(int)(xs)+", WIDTH="+(int)(samples_per_quaver/samples_per_pixel)+"px" );
		}
*/
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
