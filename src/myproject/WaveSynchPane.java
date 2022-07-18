package myproject;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class WaveSynchPane extends Canvas
		implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7740732731922064721L;
	private static final int X_OFFSET = 80;
	private static final int X_PADDING = 16;
	private static final int Y_PADDING = 4;
	private static final int RULER_THICKNESS = 16;
	
	private int canvas_width;
	private int canvas_height;

	/**		 */
	private static Font gridFont, labelFont, contentsFont, bigFont;	
	private static Color bg_color, rulerColor, rulerFontColor, beatBgColor, beatBgColor_H, lyricAreaColor, lyricAreaColor_H, chordAreaColor, chordAreaColor_H, technicAreaColor, technicAreaColor_H;  

	private int x_grid_unit = 12;		// GRID 1칸의 pixel 크기. -- depend on Zoom Size, Beats, BPM, etc...
	private int time_grid = x_grid_unit*8;		// 시간 표시를 위한 frid 간격.
	
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
	
	public void paint(Graphics g) {
//		System.out.println("WaveSynchPane drawing...");
		int canvas_width = getWidth();
		int canvas_height = getHeight();

		// 상단 Ruler
		drawRuler(g, X_OFFSET, 10, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);
		// 하단 Ruler
		drawRuler(g, X_OFFSET, canvas_height-RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, RULER_THICKNESS);
		// WAVE form Draw (우선 배경 grid만..)
		drawWaveData(g, X_OFFSET, 10+RULER_THICKNESS, canvas_width-X_OFFSET-X_PADDING, 100 );
		drawChordArea(g, X_OFFSET, 10+RULER_THICKNESS + 100, canvas_width-X_OFFSET-X_PADDING, 28 );
		drawLyricArea(g, X_OFFSET, 10+RULER_THICKNESS + 128, canvas_width-X_OFFSET-X_PADDING, 28 );

		// TAB 악보 그리기 (우선 배경만)
		drawTABArea(g, X_OFFSET, 10+RULER_THICKNESS + 156, canvas_width-X_OFFSET-X_PADDING, 66 );
		drawTechnicArea(g, X_OFFSET, 10+RULER_THICKNESS + 236, canvas_width-X_OFFSET-X_PADDING, 28 );
		
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
			if ((i/x_grid_unit)%4==0) {
				g.setColor(beatBgColor_H);
			} else {
				g.setColor(beatBgColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h-1);
		}

	}

	public void drawChordArea(Graphics g, int x, int y, int w, int h) {
		String label="chord:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
//		int w2 = g.getFontMetrics().stringWidth(s) / 2;
//		int h2 = g.getFontMetrics().getHeight();
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit)%4==0) {
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
			if ((i/x_grid_unit)%4==0) {
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
			if ((i/x_grid_unit)%4==0) {
				g.setColor(beatBgColor_H);
			} else {
				g.setColor(beatBgColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, 68);
		}

	}

	public void drawTechnicArea(Graphics g, int x, int y, int w, int h) {
		String label="technic:";
		g.setFont(labelFont);
		g.setColor(Color.DARK_GRAY);
		int strWidth=g.getFontMetrics().stringWidth(label);
		g.drawString(label, x-strWidth, y+h/2);

		for (int i=0; i<w; i+= x_grid_unit) {
			if ((i/x_grid_unit)%4==0) {
				g.setColor(technicAreaColor_H);
			} else {
				g.setColor(technicAreaColor);
			}
			g.fillRect(x+i, y, x_grid_unit-1, h-1);
		}

	}

	public void keyTyped(KeyEvent e) {
	}
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}

}
