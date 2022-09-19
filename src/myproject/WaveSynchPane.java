package myproject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

public class WaveSynchPane extends JPanel
		implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7740732731922064721L;
	private static final int X_OFFSET = 80;
	private static final int X_PADDING = 16;
	private static final int FONT_HEIGHT = 28;
	private static final int RULER_THICKNESS = 16;
	
	private static final int TECHNIC_AREA_THICKNESS = FONT_HEIGHT*2;
	private static final int TAB_AREA_HEIGHT = 66;
	private static final int CHORD_AREA_THICKNESS = FONT_HEIGHT;
	private static final int LYRIC_AREA_THICKNESS = FONT_HEIGHT;
	private static final float ZOOM_IN_LIMIT = 0.5f;
	private static final float ZOOM_OUT_LIMIT = 0.002f;
	private static final int X_CELL_WHEN_60BPM = 24;
	
	private int canvas_width;
	private int canvas_height;

	private static Font gridFont, labelFont;	
	private static Color rulerColor, rulerFontColor, beatBgColor, beatBgColor_H, lyricAreaColor, lyricAreaColor_H, chordAreaColor, chordAreaColor_H, technicAreaColor, technicAreaColor_H;  

	// 아래의 3개의 변수는 어느 하나가 바뀌면 모두 quaver 간격(samples_per_quaver) 를 새로 계산해야 함.
	private String value_meter = "4/4";	// '1'=2/4, '2'=3/4, '3'=4/4, '4'=6/8 
//	private String value_quaverMode = "quaver";  
	private int	value_quaver = 2;			// 1비트를 쪼개는 값, 2 = quaver_mode ==> 8분음표 기준,  4 = semi-quaver_mode ==> 16분음표 기준
	private float value_bpm = 60.0f;	// beats per minute.


	private int x_grid_unit = X_CELL_WHEN_60BPM;
	private int beat_per_bar = 8;
	private int time_grid = x_grid_unit*8;
	private int start_index = 0;		
	private float wave_zoom = 0.0125f;	

	private byte[] wave_data;
	private UkeData data = null;

	
	public WaveSynchPane() {
		canvas_width = getWidth();
		canvas_height = getHeight();

		gridFont = new Font("Monospaced", Font.BOLD, 9);
		labelFont =  new Font("Tahoma", Font.PLAIN, 12);

		rulerColor = Color.LIGHT_GRAY;
		rulerFontColor = Color.GRAY;
		beatBgColor = new Color(220,220,220);
		beatBgColor_H = new Color(220,230,246);
		lyricAreaColor = new Color(240,208,208);
		lyricAreaColor_H = new Color(248,216,216);
		chordAreaColor = new Color(240,240,180);
		chordAreaColor_H = new Color(240,250,180);
		technicAreaColor = new Color(200,240,220);
		technicAreaColor_H = new Color(200,250,230);  
		data = null;

		value_meter = "4/4";
		value_quaver = 2;
		value_bpm = 60.0f;
	}

	public void paintComponent(Graphics g) {
		canvas_width = getWidth();
		canvas_height = getHeight();
//		System.out.println("WaveSynchPane drawing...");

		drawRuler(g, X_OFFSET, 10, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);
		drawRuler(g, X_OFFSET, canvas_height-RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);

		int ypos = canvas_height-RULER_THICKNESS;
		drawTechnicArea(g, X_OFFSET, ypos-TECHNIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, TECHNIC_AREA_THICKNESS );
		ypos -= TECHNIC_AREA_THICKNESS;
		drawTABArea(g, X_OFFSET, ypos-TAB_AREA_HEIGHT, canvas_width-X_OFFSET-X_PADDING, TAB_AREA_HEIGHT );
		ypos -= TAB_AREA_HEIGHT;
		drawChordArea(g, X_OFFSET, ypos-CHORD_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, CHORD_AREA_THICKNESS);
		ypos -= CHORD_AREA_THICKNESS;
		drawLyricArea(g, X_OFFSET, ypos-LYRIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, LYRIC_AREA_THICKNESS);
		ypos -= LYRIC_AREA_THICKNESS;

		// WAVEFORM draw
		drawWaveData(g, X_OFFSET, 10+RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, ypos-10-RULER_THICKNESS );
		g.dispose();
	}

	private void drawRuler(Graphics g, int x, int y, int w, int h) {
		g.setColor(rulerColor);
		g.fillRect( x, y, w, h );
		for (int i=0; i<w; i+= x_grid_unit) {
			g.setFont(gridFont);
			g.setColor(rulerFontColor);
			if (i%time_grid==0) {
				g.drawString( ""+i, 4+x+i, y+h-8 );
				g.drawLine(x+i, y+h-8, x+i, y+h-1);
			} else {
				g.drawLine(x+i, y+h-4, x+i, y+h-1);
			}
		}
	}
	
	private void drawWaveData(Graphics g, int x, int y, int w, int h) {
		String label="waveform:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit) % beat_per_bar ==0) {
				g.setColor(beatBgColor_H);
			} else {
				g.setColor(beatBgColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h-1);
		}

		int center_y = y;		//+h/2;			// Waveform �߽ɼ�
		int max_amplitude = h/2; 		// WINDOW SIZE�� ���� �ִ� ������ (pixel)

		g.setColor(Color.GRAY);

		if (wave_data!=null) {
			int i, j, value, max, min, prev_min, xpos;
			int num_per_px = (int)(1.0f/wave_zoom);		// 1�ȼ������� wave data�� ����
			xpos = x;
			i=start_index;		// start index of wave data
			max=0;
			min=255;
			while( (i+num_per_px)<wave_data.length) {
				prev_min = min;
				max=0;
				min=255;
				for (j=0; j<num_per_px; j++) {
					value = wave_data[i+j]&0xFF;
					max=(max<value)?value:max;
					min=(min>value)?value:min;
				}
				g.drawLine( xpos, center_y+ min*max_amplitude/128, xpos, center_y+max*max_amplitude/128 );
				g.drawLine( xpos, center_y+ prev_min*max_amplitude/128, xpos, center_y+max*max_amplitude/128 );
				// �����ȼ��� �Ѿ�ô�.
				i+=num_per_px;
				xpos++;
				if ( xpos >= (x+w) )
					break;
			}
		}
	}

	private void drawChordArea(Graphics g, int x, int y, int w, int h) {
		System.out.println("x_grid_unit="+x_grid_unit+", value_bpm="+value_bpm );

		String label="chord:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit) % beat_per_bar ==0) {
				g.setColor(chordAreaColor_H);
			} else {
				g.setColor(chordAreaColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h-1);
		}

	}

	private void drawLyricArea(Graphics g, int x, int y, int w, int h) {
		String label="lyric:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit) % beat_per_bar ==0) {
				g.setColor(lyricAreaColor_H);
			} else {
				g.setColor(lyricAreaColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h-1);
		}

	}
	
	private void drawTABArea(Graphics g, int x, int y, int w, int h) {
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		g.drawString("G", x-16, y+16);
		g.drawString("C", x-16, y+16+16);
		g.drawString("E", x-16, y+16+32);
		g.drawString("A", x-16, y+16+48);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit) % beat_per_bar ==0) {
				g.setColor(beatBgColor_H);
			} else {
				g.setColor(beatBgColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h);
		}

		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y+9   , w-1, 2);		// G
		g.fillRect(x, y+9+16, w-1, 2);		// C
		g.fillRect(x, y+9+32, w-1, 2);		// E
		g.fillRect(x, y+9+48, w-1, 2);		// A

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit) % beat_per_bar ==0) {
				g.fillRect(x+i-2, y+9, 2, 50);
			}
		}
	}

	private void drawTechnicArea(Graphics g, int x, int y, int w, int h) {
		String label="technic:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit) % beat_per_bar ==0) {
				g.setColor(technicAreaColor_H);
			} else {
				g.setColor(technicAreaColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h-1);
		}

	}

	/**
	 * *.wav 파일에서 읽어 온 오디오 데이터를 설정 - WAV 파형을 그릴 데이터를 지정.
	 * @param data	*.wav에서 읽어 온 음성데이터 (8bit unsigned, mono 데이터만 가능함)
	 */
	public void setWaveData(byte[] data) {
		System.out.println("WAVE DATA SET. : length=" + data.length);
		wave_data = data;
	}

	/**
	 * *.uke 파일에서 읽어 온 연주할 데이터를 설정 - TAB악보에 그릴 데이터를 지정함.
	 * @param readData  UkeData 객체. - 기본정보 및 악보데이터 -
	 */
	public void setNoteData(UkeData readData) {
		System.out.println("Note Data SET : length="+readData.getSize() );
		data = readData;
	}
	
	/**
	 * 연주되는 음악의 BPM 값 설정.
	 * @param bpm  - beat per minute. 1분당 4분음표의 갯수. meter(박자)정보와 함께 마디를 구분할 수 있도록 grid 를 표시하는데 사용됨.
	 */
	public void setBpm(float bpm) {
		System.out.println("set BPM: " + bpm);
		value_bpm = bpm;

		x_grid_unit = (int)((float)(X_CELL_WHEN_60BPM)*60/value_bpm);
		x_grid_unit *= (value_quaver!=0)?2:1;

		repaint();
	}

	/**
	 * 음악 파일의 기본 음표 크기 ( grid 1개 칸의 음표의 길이)
	 * @param isSemiQuaver	기본음표 크기 (문자열로 지정: default='quaver', 16분음표='semi-quaver') 
	 */
	public void setQuaver(String quaver_mode) {	// 0=8����ǥ, 1=16����ǥ
		System.out.println("set Quaver Mode: " + quaver_mode);
		value_quaver = (quaver_mode=="quaver_mode") ? 2 : 4;

		x_grid_unit = (int)((float)(X_CELL_WHEN_60BPM)*60/value_bpm);
		x_grid_unit *= (value_quaver!=0)?2:1;			// 8����ǥ �����̶�� 2�� ũ��� ��.
		repaint();
	}

	/** 
	 * 음악파일의 박자 지정 (2/4, 3/4, 4/4, 6/8)
	 * @param meter	박자 값 (문자열로 지정)
	 */
	public void setMeter(int meter) {	// '0'=2/4����, '1'=3/4����, '2'=4/4����, '3'=6/8����
		value_meter = "2/4";
		switch(meter) {
			case 0:		// 2/4����
				System.out.println("setMeter: 2/4����.." );
				break;
			case 1:		// 3/4����
				System.out.println("setMeter: 3/4����.." );
				break;
			case 4:		// 6/8����
				System.out.println("setMeter: 6/8����.." );
				break;
			default:	// 4/4����
				System.out.println("setMeter: 4/4����.." );
				break;
		}
//		x_grid_unit = (X_CELL_WHEN_60BPM)*60/value_bpm; 	// 16����ǥ ����.
//		x_grid_unit *= (value_quaver!=0)?2:1;			// 8����ǥ �����̶�� 2�� ũ��� ��.
		repaint();
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
		if ( e.getWheelRotation() > 0) {	// Ȯ��
			wave_zoom *= 1.5f;
//			x_grid_unit *= 1.5f;
			if (wave_zoom > ZOOM_IN_LIMIT) {			// 1.0f) {
				System.out.println("Zoom in limited.");
				wave_zoom = ZOOM_IN_LIMIT;
			}
		} else if ( e.getWheelRotation() < 0) {	// ���
			wave_zoom *= 0.75f;
//			x_grid_unit *= 0.75f;
			if (wave_zoom < ZOOM_OUT_LIMIT) {			// 0.002f) {
				System.out.println("Zoom out limited.");
				wave_zoom = ZOOM_OUT_LIMIT;
			}
		}
		System.out.println("wave_zoom = " + wave_zoom);
//		super.invalidate();
		repaint();
	}

	private static Point mousePt;
	private static int prev_start_index;

	public void mouseDragged(MouseEvent e) {
		if (mousePt==null)
			return;
		int num_per_px = (int)(1.0f/wave_zoom);		// 1�ȼ������� wave data�� ����
		start_index = prev_start_index - (e.getX()-mousePt.x)*num_per_px ;
		if (start_index < 0)
			start_index = 0;
		if (start_index >= wave_data.length-time_grid)
			start_index = wave_data.length-time_grid;
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
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
