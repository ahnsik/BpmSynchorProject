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

	// 지울까 생각 중인 값 들.. 
		private static final float DEFAULT_ZOOM_FACTOR = 0.002f;
		private static final int X_CELL_WHEN_60BPM = 6;


	private int canvas_width;
	private int canvas_height;

	/**		 */
	private static Font gridFont, labelFont;	
	private static Color bg_color, rulerColor, rulerFontColor, playedWaveFormColor, waveFormColor, beatBgColor, beatBgColor_H, lyricAreaColor, lyricAreaColor_H, chordAreaColor, chordAreaColor_H, technicAreaColor, technicAreaColor_H;  

	private float samples_per_quaver;		// 8분음표 1개의 길이.
	private float samples_per_pixel = 100.0f;		// 1픽셀당 audio sample 의 갯수. -->  마우스 휠에 의한 확대 축소 배율.

	private int value_meter = 3;	// '1'=2/4, '2'=3/4, '3'=4/4, '4'=6/8 
	private int value_beat = 0;	//	'0' = quaver(8����ǥ), '1'=semi-quaver(16����ǥ) 
	private int value_bpm = 80;	// beats per minute.
	private int sample_rate = 8000;

	private int x_grid_unit = X_CELL_WHEN_60BPM;		// GRID 1ĭ�� pixel ũ��. -- depend on Zoom Size, Beats, BPM, etc...
			// 60 bpm, quaver(8����ǥ), �⺻ȭ��ũ�⿡  
	private WavPlay	player;
	private int quaver_mode = 8;
	private int maximum_start_index = x_grid_unit*8;		// �ð� ǥ�ø� ���� grid ����.
	private int start_index = 0;		// �ð� ǥ�ø� ���� grid ����.
	private int playing_position = 0;		// �ð� ǥ�ø� ���� grid ����.

	private byte[] wave_data;
//	private byte[] wave_60bpm, wave_61bpm, wave_62bpm, wave_63bpm;
	private NoteData note_data = null;

	public WaveSynchPane() {
		canvas_width = getWidth();
		canvas_height = getHeight();

		gridFont = new Font("Monospaced", Font.BOLD, 9);
		labelFont =  new Font("Tahoma", Font.PLAIN, 12);

		bg_color = new Color(240, 240, 240);
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
		samples_per_pixel = 100.0f;		// 24는 8분음표 1개에 해당하는 grid 크기.
		value_meter = 3;	// '1'=2/4, '2'=3/4, '3'=4/4, '4'=6/8 
		value_beat = 0;	//	'0' = quaver(8����ǥ), '1'=semi-quaver(16����ǥ) 
		value_bpm = 80;	// beats per minute.
		sample_rate = 8000;
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
		playing_position = 0;
	}

	protected void ReadWaveData(File f) {
		// TODO Auto-generated method stub
		/*	-- WAV ������ ���.	*/
		byte[] Header = new byte[44];
		byte[] Buffer = new byte[(int)f.length()];

		try {
			FileInputStream is;

			is = new FileInputStream( f );

			int byteRead = -1;
			byteRead = is.read(Header);			// *.wav���� ������� https://anythingcafe.tistory.com/2
//			System.out.println("Header Chunk Size:"+( ((Header[17]&0xFF)<<8)+(Header[16]&0xFF)) );	// = 4����Ʈ ������ Header ũ��� �׸� ũ�� �����Ƿ�, ���� 2����Ʈ��. (little endian)
//			System.out.println("num of Channel:"+( ((Header[23]&0xFF)<<8)+(Header[22]&0xFF)) );	// ä�� �� : 1=Mono, 2=Stereo, 5:4channel. 6:6channel, etc..
//			System.out.println("Sample Rate:"+ ((Header[24]&0xFF)+((Header[25]&0xFF)<<8)) );				//( ((long)(Header[25]&0xFF)<<8)+Header[24]) );	// = 4����Ʈ little endian
//			System.out.println("byte rate =1�ʴ� byte ��:"+( ((Header[31]&0xFF)<<24)+((Header[30]&0xFF)<<16)+((Header[29]&0xFF)<<8)+(Header[28]&0xFF)) );	
//			System.out.println("num bits per Sample :"+( ((Header[35]&0xFF)<<8)+(Header[34]&0xFF)) );	
//			System.out.println("Block Align:"+( ((Header[33]&0xFF)<<8)+(Header[32]&0xFF)) );		// 	

			byteRead = is.read(Buffer);
			is.close();

//			int num_of_channel = (Header[22]&0xFF)+((Header[23]&0xFF)<<8);
//			int num_bits_of_sample = (Header[34]&0xFF)+((Header[35]&0xFF)<<8);
//			int SampleRate = (Header[24]&0xFF)+((Header[25]&0xFF)<<8);
			int block_align = ((Header[33]&0xFF)<<8)+(Header[32]&0xFF);		// 1�� Sample �� byte ��. (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel
			System.out.println("sample stripe = "+block_align );	

			wave_data = new byte[byteRead];
			for (int i=0; i<byteRead; i++) {
				wave_data[i] = Buffer[i];
			}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void paintComponent(Graphics g) {
		canvas_width = getWidth();
		canvas_height = getHeight();

		g.setColor(bg_color);
		g.fillRect( 0, 0, canvas_width, canvas_height );

		drawBGGrid(g, X_OFFSET, Y_PADDING, canvas_width-X_OFFSET-X_PADDING, canvas_height-Y_PADDING );

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

	
	public void drawBGGrid(Graphics g, int x, int y, int w, int h) {
		g.setColor(rulerColor);
		g.fillRect(x, y, w-1, RULER_THICKNESS);
		g.fillRect(x, y+h-RULER_THICKNESS, w-1, RULER_THICKNESS);

		int i, j;
		boolean grid = false, quaver_grid = false;
		
		for (i=0; i<w; i++) {
			int start = (int)((samples_per_pixel*i)+start_index);
			int end = (int)((samples_per_pixel*(i+1))+start_index);
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
			drawSingleLine(g, x+i, y, h, grid, quaver_grid);
		}

	}

	private void drawSingleLine(Graphics g, int x, int y, int h, boolean isGrid, boolean isQuaver) {
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
		int i, j;
		boolean grid;	

		String time_string= "";
		int time_msec = 0, prev_t = 0;
		int devider = 1000;
		
		if (samples_per_pixel <= 50.0f) {
			devider = 500;
		} else if (samples_per_pixel <= 100.0f) {
			devider = 1000;
		} else if (samples_per_pixel <= 200.0f) {
			devider = 2000;
		} else {
			devider = 4000;
		}

		g.setFont(gridFont);
		g.setColor(rulerFontColor);
		for ( i=0; i<w; i++) {
			int start = (int)((samples_per_pixel*i)+start_index);
			int end = (int)((samples_per_pixel*(i+1))+start_index);

			time_msec = (start*1000/sample_rate);			

			grid=false;
//			for (j=start; j<end; j++) {
//				if (j% (int)samples_per_quaver==0)
//					grid=true;
//			}
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

		int i, j;
		int center_y = y;
		int max_amplitude = h/2;

		//////////
		int value, max, min, prev_min;

		if (wave_data!=null) {
			max=0;
			min=255;
			for (i=0; i<w; i++) {
				int start = (int)((samples_per_pixel*i)+start_index);
				int end = (int)((samples_per_pixel*(i+1))+start_index);
				if ( end < playing_position ) {
					g.setColor(playedWaveFormColor);		// WaveForm Color.
				} else {
					g.setColor(waveFormColor);		// WaveForm Color.
				}

				if(end >= wave_data.length ) 
					end = wave_data.length;
				prev_min = min;
				max=0;
				min=255;
				for (j=start; j<end; j++) {
					value = wave_data[j]&0xFF;
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

		int i, j;
		if (note_data == null)
			return;
		for (i=0; i<w; i++) {
			int start = (int)((samples_per_pixel*i)+start_index);
			int end = (int)((samples_per_pixel*(i+1))+start_index);

			for (j=0; j<note_data.notes.length; j++) {
				if ( (note_data.notes[j].timeStamp*sample_rate/1000 >= start)&&(note_data.notes[j].timeStamp*sample_rate/1000 < end)) {
					g.setColor(Color.BLACK);
					g.drawString(note_data.notes[j].chordName, x+i, y+FONT_HEIGHT);
				}
			}
		}

	}

	public void drawLyricArea(Graphics g, int x, int y, int w, int h) {
		String label="lyric:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		int i, j;
		if (note_data == null)
			return;
		for (i=0; i<w; i++) {
			int start = (int)((samples_per_pixel*i)+start_index);
			int end = (int)((samples_per_pixel*(i+1))+start_index);

			for (j=0; j<note_data.notes.length; j++) {
				if ( (note_data.notes[j].timeStamp*sample_rate/1000 >= start)&&(note_data.notes[j].timeStamp*sample_rate/1000 < end)) {
					g.setColor(Color.BLACK);
					g.drawString(note_data.notes[j].lyric, x+i, y+FONT_HEIGHT);
//					System.out.println("TS:"+note_data.notes[j].timeStamp+"  "+note_data.notes[j].lyric );
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
/*
		for (int i=0; i<w; i++) {
			int index = (int)( (((samples_per_pixel*i)+start_index)*value_bpm)/60);
			if ( ( ((int)(index))%(samples_per_quaver)) != 0 ) {
//					g.setColor(beatBgColor_H);
//					g.setColor(beatBgColor);
			}
		}

		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y+9   , w-1, 2);		// G
		g.fillRect(x, y+9+16, w-1, 2);		// C
		g.fillRect(x, y+9+32, w-1, 2);		// E
		g.fillRect(x, y+9+48, w-1, 2);		// A

		for (int i=0; i<w; i++) {
			int index = (int)( (((samples_per_pixel*i)+start_index)*value_bpm)/60);
			if ( (index)%(quaver_mode*samples_per_quaver) == 0 ) {
				g.fillRect(x+i-2, y+9, 2, 50);
			}
		}
*/
		int i, j;
		boolean grid, quaver_grid;

		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y+9   , w-1, 2);		// G
		g.fillRect(x, y+9+16, w-1, 2);		// C
		g.fillRect(x, y+9+32, w-1, 2);		// E
		g.fillRect(x, y+9+48, w-1, 2);		// A

		for (i=0; i<w; i++) {
			int start = (int)((samples_per_pixel*i)+start_index);
			int end = (int)((samples_per_pixel*(i+1))+start_index);
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

//					g.setColor(technicAreaColor_H);
//					g.setColor(technicAreaColor);
		int i, j;
		if (note_data == null)
			return;
		for (i=0; i<w; i++) {
			int start = (int)((samples_per_pixel*i)+start_index);
			int end = (int)((samples_per_pixel*(i+1))+start_index);

			for (j=0; j<note_data.notes.length; j++) {
				if ( (note_data.notes[j].timeStamp*sample_rate/1000 >= start)&&(note_data.notes[j].timeStamp*sample_rate/1000 < end)) {
					g.setColor(Color.BLACK);
					g.drawString(note_data.notes[j].technic, x+i, y+FONT_HEIGHT);
//					System.out.println("TS:"+note_data.notes[j].timeStamp+"  "+note_data.notes[j].technic);
				}
			}
		}

	}

	public void setWaveData(byte[] data) {
		System.out.println("WAVE DATA SET. : length=" + data.length);
		wave_data = data;
	}

	public void setNoteData(NoteData data) {
		System.out.println("Note Data SET : length=" + data.getSize() );
		note_data = data;
	}
	
	public void setBpm(int bpm) {
		System.out.println("set BPM: " + bpm);
		value_bpm = bpm;
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.

		x_grid_unit = (X_CELL_WHEN_60BPM)*60/value_bpm; 	// 16����ǥ ����.
		x_grid_unit *= (value_beat!=0)?1:2;			// 8����ǥ �����̶�� 2�� ũ��� ��.
		maximum_start_index = x_grid_unit*8;		// �ð� ǥ�ø� ���� grid ����.

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
			case 0:		// 2/4����
				quaver_mode = (value_beat==0)?4:8;
				break;
			case 1:		// 3/4����
				quaver_mode = (value_beat==0)?6:12;
				break;
			case 3:		// 6/8����
				quaver_mode = (value_beat==0)?6:12;
				break;
			default:	// 4/4����
				quaver_mode = (value_beat==0)?8:16;
				break;
		}
		
		x_grid_unit = (X_CELL_WHEN_60BPM)*60/value_bpm; 	// 16����ǥ ����.
		x_grid_unit *= (value_beat!=0)?1:2;			// 8����ǥ �����̶�� 2�� ũ��� ��.
		maximum_start_index = x_grid_unit*8;		// �ð� ǥ�ø� ���� grid ����.
		repaint();
	}

	public void setMeter(int meter) {	// '0'=2/4����, '1'=3/4����, '2'=4/4����, '3'=6/8����
		value_meter = meter; 
		switch(value_meter) {
			case 0:		// 2/4����
				System.out.println("setMeter: 2/4����.." );
				quaver_mode = (value_beat==0)?4:8;
				break;
			case 1:		// 3/4����
				System.out.println("setMeter: 3/4����.." );
				quaver_mode = (value_beat==0)?6:12;
				break;
			case 3:		// 6/8����
				System.out.println("setMeter: 6/8����.." );
				quaver_mode = (value_beat==0)?6:12;
				break;
			default:	// 4/4����
				System.out.println("setMeter: 4/4����.." );
				quaver_mode = (value_beat==0)?8:16;
				break;
		}
		repaint();
	}

	public void setPlayer(WavPlay p) {
		player = p;
		sample_rate = player.getSampleRate();
		samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
	}
	public void setDrawStart(int milisec) {
		start_index = milisec;		// quaver 1�� ��ǥ�� �ʺ�(�ð�)�� milli-second ������ ������� �� ��. - Sampling ���ļ��� �������� ����ؾ� ��.
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
//		System.out.println("Mouse Wheel listener:" + e.getWheelRotation() + ", Amount:"+e.getScrollAmount() + ", type:"+e.getScrollType() );
		if ( e.getWheelRotation() > 0) {	// Ȯ��
			if (samples_per_pixel < 280.0f )
				samples_per_pixel += 10.0f;
		} else if ( e.getWheelRotation() < 0) {	// ���
			if (samples_per_pixel > 10.0f )
				samples_per_pixel -= 10.0f;
		}

//		System.out.println("samples_per_pixel = " + samples_per_pixel);
		repaint();
	}

	private static Point mousePt;
	private static int prev_start_index;

	public void mouseDragged(MouseEvent e) {
		if (mousePt==null)
			return;
		int num_per_px = (int)(1.0f/DEFAULT_ZOOM_FACTOR);
		start_index = prev_start_index - (e.getX()-mousePt.x)*num_per_px ;
		if (start_index < 0)
			start_index = 0;
		if (start_index >= wave_data.length-maximum_start_index)
			start_index = wave_data.length-maximum_start_index;
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
		float samples_per_quaver = (float)(sample_rate*60) / (float)(2*value_bpm);		// 8분음표 1개의 길이.
		float samples_per_pixel = 100.0f;		// 24는 8분음표 1개에 해당하는 grid 크기.
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

}
