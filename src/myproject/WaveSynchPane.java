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
	private static final int X_CELL_WHEN_60BPM = 6;		/* 60BPM, 4/4박자 일때, 16분음표 표시를 위한 셀 가로 크기 */
	
	private int canvas_width;
	private int canvas_height;

	/**		 */
	private static Font gridFont, labelFont;	
	private static Color bg_color, rulerColor, rulerFontColor, beatBgColor, beatBgColor_H, lyricAreaColor, lyricAreaColor_H, chordAreaColor, chordAreaColor_H, technicAreaColor, technicAreaColor_H;  

	//// 설정값의 정의
	private int value_meter = 3;	// '1'=2/4, '2'=3/4, '3'=4/4, '4'=6/8 
	private int value_beat = 0;	//	'0' = quaver(8분음표), '1'=semi-quaver(16분음표) 
	private int value_bpm = 80;	// beats per minute.

	private int x_grid_unit = X_CELL_WHEN_60BPM;		// GRID 1칸의 pixel 크기. -- depend on Zoom Size, Beats, BPM, etc...
			// 60 bpm, quaver(8분음표), 기본화면크기에  
	private WavPlay	player;
	private int beat_per_bar = 8;
	private int time_grid = x_grid_unit*8;		// 시간 표시를 위한 grid 간격.
	private int start_index = 0;		// 시간 표시를 위한 grid 간격.
	private int playing_position = 0;		// 시간 표시를 위한 grid 간격.
	private float wave_zoom = 0.002f;			// 최소값=최대축소율 = 0.001 까지만 할 것.==> 약 3분짜리 1곡을 1920 전체 화면에 그리는 배율.
												// default 는 0.01 이 적당한 것으로 보인다. = 코쿠리코언덕 정도의 느린 음악에 따라 가기가 딱 좋다.

	private byte[] wave_data;

	public WaveSynchPane() {
		canvas_width = getWidth();
		canvas_height = getHeight();

		gridFont = new Font("Monospaced", Font.BOLD, 9);
		labelFont =  new Font("Tahoma", Font.PLAIN, 12);

		bg_color = new Color(240, 240, 240);
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
	}

	public void paintComponent(Graphics g) {
		canvas_width = getWidth();
		canvas_height = getHeight();

		System.out.println("WaveSynchPane repainted..");
		g.setColor(bg_color);
		g.fillRect( 0, 0, canvas_width, canvas_height );

		// 상단 Ruler
		drawRuler(g, X_OFFSET, 10, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);
		// 하단 Ruler
		drawRuler(g, X_OFFSET, canvas_height-RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);

		int ypos = canvas_height-RULER_THICKNESS;
		// 연주기법, Stroke 방향, etc.. 
		drawTechnicArea(g, X_OFFSET, ypos-TECHNIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, TECHNIC_AREA_THICKNESS );
		ypos -= TECHNIC_AREA_THICKNESS;
		// TAB 악보 그리기 
		drawTABArea(g, X_OFFSET, ypos-TAB_AREA_HEIGHT, canvas_width-X_OFFSET-X_PADDING, TAB_AREA_HEIGHT );
		ypos -= TAB_AREA_HEIGHT;
		// Chord Area
		drawChordArea(g, X_OFFSET, ypos-CHORD_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, CHORD_AREA_THICKNESS);
		ypos -= CHORD_AREA_THICKNESS;
		// Lyric Area
		drawLyricArea(g, X_OFFSET, ypos-LYRIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, LYRIC_AREA_THICKNESS);
		ypos -= LYRIC_AREA_THICKNESS;

		// WAVEFORM draw
		drawWaveData(g, X_OFFSET, 10+RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, ypos-10-RULER_THICKNESS );
		g.dispose();
	}

	public void drawRuler(Graphics g, int x, int y, int w, int h) {
		int unit_width = (int)((wave_zoom*x_grid_unit)/0.002f); 
		
		g.setColor(rulerColor);
		g.fillRect( x, y, w, h );
		for (int i=0; i<w; i+= unit_width) {
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

	public void drawWaveData(Graphics g, int x, int y, int w, int h) {
		int unit_width = (int)((wave_zoom*x_grid_unit)/0.002f); 
		String label="waveform:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= unit_width) {
			if ((i/unit_width) % beat_per_bar ==0) {
				g.setColor(beatBgColor_H);
			} else {
				g.setColor(beatBgColor);
			}
			g.fillRect(x+i, y, unit_width-1, h-1);
		}

		int center_y = y;		//+h/2;			// Waveform 중심선
		int max_amplitude = h/2; 		// WINDOW SIZE에 따른 최대 진폭값 (pixel)

		g.setColor(Color.GRAY);
		if (wave_data!=null) {
			int i, j, value, max, min, prev_min, xpos;
			int num_per_px = (int)(1.0f/wave_zoom);		// 1픽셀넓이의 wave data들 갯수
//			System.out.println("num_per_px = "+num_per_px);
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

				if (player != null) {	// 플레이어가 설정 된 상태라면, 
					playing_position = (int)player.getPlayingPosition();
//					playing_position = (int)player.getPlayingPositionInMilliSecond();
					if ( (i<playing_position) && (i+num_per_px)>playing_position ) {
						System.out.println("[Drawing] playing_position = "+ playing_position);
						g.setColor(Color.RED);
						g.fillRect( xpos-1, y, 2, h-1 );
						g.setColor(Color.GRAY);
					}
				}

				// 다음픽셀로 넘어갑시다.
				i+=num_per_px;
				xpos++;
				if ( xpos >= (x+w) )
					break;
			}
		}
	}

	public void drawChordArea(Graphics g, int x, int y, int w, int h) {
		int unit_width = (int)((wave_zoom*x_grid_unit)/0.002f); 

		String label="chord:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= unit_width) {
			if ((i/unit_width) % beat_per_bar ==0) {
				g.setColor(chordAreaColor_H);
			} else {
				g.setColor(chordAreaColor);
			}
			g.fillRect(x+i, y, unit_width-1, h-1);
		}

	}

	public void drawLyricArea(Graphics g, int x, int y, int w, int h) {
		int unit_width = (int)((wave_zoom*x_grid_unit)/0.002f); 
		String label="lyric:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= unit_width) {
			if ((i/unit_width) % beat_per_bar ==0) {
				g.setColor(lyricAreaColor_H);
			} else {
				g.setColor(lyricAreaColor);
			}
			g.fillRect(x+i, y, unit_width-1, h-1);
		}

	}
	
	public void drawTABArea(Graphics g, int x, int y, int w, int h) {
		int unit_width = (int)((wave_zoom*x_grid_unit)/0.002f); 
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		g.drawString("G", x-16, y+16);
		g.drawString("C", x-16, y+16+16);
		g.drawString("E", x-16, y+16+32);
		g.drawString("A", x-16, y+16+48);

		for (int i=0; i<w; i+= unit_width) {
			if ((i/unit_width) % beat_per_bar ==0) {
				g.setColor(beatBgColor_H);
			} else {
				g.setColor(beatBgColor);
			}
			g.fillRect(x+i, y, unit_width-1, h);
		}

		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y+9   , w-1, 2);		// G
		g.fillRect(x, y+9+16, w-1, 2);		// C
		g.fillRect(x, y+9+32, w-1, 2);		// E
		g.fillRect(x, y+9+48, w-1, 2);		// A

		for (int i=0; i<w; i+= unit_width) {
			if ((i/unit_width) % beat_per_bar ==0) {
				g.fillRect(x+i-2, y+9, 2, 50);
			}
		}
	}

	public void drawTechnicArea(Graphics g, int x, int y, int w, int h) {
		int unit_width = (int)((wave_zoom*x_grid_unit)/0.002f); 
		String label="technic:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= unit_width) {
			if ((i/unit_width) % beat_per_bar ==0) {
				g.setColor(technicAreaColor_H);
			} else {
				g.setColor(technicAreaColor);
			}
			g.fillRect(x+i, y, unit_width-1, h-1);
		}

	}

	public void setWaveData(byte[] data) {
		System.out.println("WAVE DATA SET. : length=" + data.length);
		wave_data = data;
	}

	public void setBpm(int bpm) {
		System.out.println("set BPM: " + bpm);
		value_bpm = bpm;

		
		x_grid_unit = (X_CELL_WHEN_60BPM)*60/value_bpm; 	// 16분음표 기준.
		x_grid_unit *= (value_beat!=0)?1:2;			// 8분음표 기준이라면 2배 크기로 함.
		time_grid = x_grid_unit*8;		// 시간 표시를 위한 grid 간격.

		// 60bpm 4/4박자 1초 = 48*8 = 384px ==> 1beat 는 48px,		60bpm은, 1/60으로 봐야 하므로,   
		// 80bpm 4/4박자 1초 = 48*8 = 384px ==> 1beat 는 ??px,  	80bpm은 1/80으로 해서,  1/60:48px = 1/80:??px  ??=48*60/80,  즉,  48*60/bpm 으로 정한다. 
		repaint();
	}

	public void setQuaver(int isSemiQuaver) {	// 0=8분음표, 1=16분음표
		System.out.println("set Quaver: " + isSemiQuaver);
		value_beat = isSemiQuaver;
		if (isSemiQuaver==1) {
			beat_per_bar = 16;
		} else {
			beat_per_bar = 8;
		}		 
		
		x_grid_unit = (X_CELL_WHEN_60BPM)*60/value_bpm; 	// 16분음표 기준.
		x_grid_unit *= (value_beat!=0)?1:2;			// 8분음표 기준이라면 2배 크기로 함.
		time_grid = x_grid_unit*8;		// 시간 표시를 위한 grid 간격.
		repaint();
	}

	public void setMeter(int meter) {	// '0'=2/4박자, '1'=3/4박자, '2'=4/4박자, '3'=6/8박자
		value_meter = meter; 
		switch(value_meter) {
			case 0:		// 2/4박자
				System.out.println("setMeter: 2/4박자.." );
				break;
			case 1:		// 3/4박자
				System.out.println("setMeter: 3/4박자.." );
				break;
			case 4:		// 6/8박자
				System.out.println("setMeter: 6/8박자.." );
				break;
			default:	// 4/4박자
				System.out.println("setMeter: 4/4박자.." );
				break;
		}
		repaint();
	}

	public void setPlayer(WavPlay p) {
		player = p;
	}
	public void setDrawStart(int milisec) {
		start_index = milisec;		// quaver 1개 음표의 너비(시간)을 milli-second 값으로 나누어야 할 것. - Sampling 주파수를 기준으로 계산해야 함.
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
		if ( e.getWheelRotation() > 0) {	// 확대
			wave_zoom *= 1.5f;
			if (wave_zoom > ZOOM_IN_LIMIT) {			// 1.0f) {
				System.out.println("Zoom in limited.");
				wave_zoom = ZOOM_IN_LIMIT;
			}
		} else if ( e.getWheelRotation() < 0) {	// 축소
			wave_zoom *= 0.75f;
			if (wave_zoom < ZOOM_OUT_LIMIT) {			// 0.002f) {
				System.out.println("Zoom out limited.");
				wave_zoom = ZOOM_OUT_LIMIT;
			}
		}

		System.out.println("wave_zoom = " + wave_zoom);
		repaint();
	}

	private static Point mousePt;
	private static int prev_start_index;

	public void mouseDragged(MouseEvent e) {
		if (mousePt==null)
			return;
		int num_per_px = (int)(1.0f/wave_zoom);		// 1픽셀넓이의 wave data들 갯수
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
