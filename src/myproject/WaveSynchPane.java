package myproject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import java.awt.Canvas;
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
	private static final int Y_PADDING = 4;
	private static final int FONT_HEIGHT = 28;
	private static final int RULER_THICKNESS = 16;
	
	private static final int TECHNIC_AREA_THICKNESS = FONT_HEIGHT*2;
	private static final int TAB_AREA_HEIGHT = 66;
	private static final int CHORD_AREA_THICKNESS = FONT_HEIGHT;
	private static final int LYRIC_AREA_THICKNESS = FONT_HEIGHT;
	private static final float ZOOM_IN_LIMIT = 0.5f;
	private static final float ZOOM_OUT_LIMIT = 0.002f;
	
	private int canvas_width;
	private int canvas_height;

	/**		 */
	private static Font gridFont, labelFont, contentsFont, bigFont;	
	private static Color bg_color, rulerColor, rulerFontColor, beatBgColor, beatBgColor_H, lyricAreaColor, lyricAreaColor_H, chordAreaColor, chordAreaColor_H, technicAreaColor, technicAreaColor_H;  

	private int x_grid_unit = 24;		// GRID 1ĭ�� pixel ũ��. -- depend on Zoom Size, Beats, BPM, etc...
	private int beat_per_bar = 8;
	private int time_grid = x_grid_unit*8;		// �ð� ǥ�ø� ���� grid ����.
	private int start_index = 0;		// �ð� ǥ�ø� ���� grid ����.
	private float wave_zoom = 0.0125f;			// �ּҰ�=�ִ������ = 0.001 ������ �� ��.==> �� 3��¥�� 1���� 1920 ��ü ȭ�鿡 �׸��� ����.
												// default �� 0.01 �� ������ ������ ���δ�. = �������ھ�� ������ ���� ���ǿ� ���� ���Ⱑ �� ����.

	private byte[] wave_data;

	public WaveSynchPane() {
		canvas_width = getWidth();
		canvas_height = getHeight();

		gridFont = new Font("Monospaced", Font.BOLD, 9);
		labelFont =  new Font("Tahoma", Font.PLAIN, 12);
		contentsFont = new Font("Tahoma", Font.BOLD|Font.ITALIC, 24);
		bigFont = new Font("Tahoma", Font.BOLD|Font.ITALIC, 36);

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
//		System.out.println("WaveSynchPane drawing...");

		// ��� Ruler
		drawRuler(g, X_OFFSET, 10, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);
		// �ϴ� Ruler
		drawRuler(g, X_OFFSET, canvas_height-RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);

		int ypos = canvas_height-RULER_THICKNESS;
		// ���ֱ��, Stroke ����, etc.. 
		drawTechnicArea(g, X_OFFSET, ypos-TECHNIC_AREA_THICKNESS, canvas_width-X_OFFSET-X_PADDING, TECHNIC_AREA_THICKNESS );
		ypos -= TECHNIC_AREA_THICKNESS;
		// TAB �Ǻ� �׸��� 
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

	}

	public void drawRuler(Graphics g, int x, int y, int w, int h) {
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
	
	public void drawWaveData(Graphics g, int x, int y, int w, int h) {
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
//		if (wave_data!=null) {
//			for (int i=1; i<wave_data.length; i++) {
//				if ( i >= w/wave_zoom )
//					break;
//				g.drawLine( (int)(i*wave_zoom+x-1), center_y+(wave_data[i-1]&0xFF)*max_amplitude/128, (int)(i*wave_zoom+x-1), center_y+(wave_data[i]&0xFF)*max_amplitude/128 );
////				g.drawLine(x+i-1, center_y+((wave_data[44+i/4-1]&0xFF-128))*max_amplitude/128, x+i-1, center_y+((wave_data[44+i/4]&0xFF-128))*max_amplitude/128 );
//			}
//		}

		if (wave_data!=null) {
			int i, j, value, max, min, prev_max, prev_min, xpos;
			int num_per_px = (int)(1.0f/wave_zoom);		// 1�ȼ������� wave data�� ����
//			System.out.println("num_per_px = "+num_per_px);
			xpos = x;
			i=start_index;		// start index of wave data
			max=0;
			min=255;
			while( (i+num_per_px)<wave_data.length) {
				prev_max = max;
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

	public void drawChordArea(Graphics g, int x, int y, int w, int h) {
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

	public void drawLyricArea(Graphics g, int x, int y, int w, int h) {
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
	
	public void drawTABArea(Graphics g, int x, int y, int w, int h) {
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

	public void drawTechnicArea(Graphics g, int x, int y, int w, int h) {
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

	public void setWaveData(byte[] data) {
		System.out.println("WAVE DATA SET. : length=" + data.length);
		wave_data = data;
	}

	public void setBpm(int bpm) {
		
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