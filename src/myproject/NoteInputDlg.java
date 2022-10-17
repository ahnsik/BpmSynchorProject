package myproject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import myproject.UkeData.Note;

import javax.swing.DefaultComboBoxModel;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.Color;
import java.awt.event.ActionEvent;

public class NoteInputDlg extends JDialog implements ActionListener,PropertyChangeListener, KeyListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int KEYCODE_ESC = 27;
	public static final int OK_OPTION = 0;
	public static final int CANCEL_OPTION = -2;
	public static final int DELETE_OPTION = -3;

	private JTextField tfLyric;
	private JTextField tf_A;
	private JTextField tf_E;
	private JTextField tf_C;
	private JTextField tf_G;
	private JTextField tfTechnics;

	private JSpinner spnrTimeStamp; 

	private static Note		originData;
	private int retValue = CANCEL_OPTION;
	private JTextField tfChordName;
	private JCheckBox chkDelete; 

	public NoteInputDlg(JFrame owner, String title) {
		super(owner, title);

		setModal(true);
		setType(Type.POPUP);
		setResizable(false);
		setSize(352, 356);
		setTitle("연주음 입력");
		//setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); 	- 이걸 사용하면, Close 버튼이 무반응  - 프로그램 종료 안됨.

		JLabel lblTimeStamp = new JLabel("timeStamp");
		
		spnrTimeStamp = new JSpinner();
		
		JLabel lbllyric = new JLabel("가사");
		
		tfLyric = new JTextField();
		tfLyric.setColumns(10);
		
		JLabel lblChord = new JLabel("코드");

		JLabel lbl_A = new JLabel("A");
		lbl_A.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblMsec = new JLabel("msec");
		
		tf_A = new JTextField();
		tf_A.setColumns(10);
		
		JLabel lbl_E = new JLabel("E");
		lbl_E.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tf_E = new JTextField();
		tf_E.setColumns(10);
		
		JLabel lbl_C = new JLabel("C");
		lbl_C.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tf_C = new JTextField();
		tf_C.setColumns(10);
		
		JLabel lbl_G = new JLabel("G");
		lbl_G.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tf_G = new JTextField();
		tf_G.setColumns(10);
		
		JLabel lblTechnics = new JLabel("technics");
		
		tfTechnics = new JTextField();
		tfTechnics.setColumns(10);
		
		JLabel lblSound_A = new JLabel("New label");
		JLabel lblSound_E = new JLabel("New label");
		JLabel lblSound_C = new JLabel("New label");
		JLabel lblSound_G = new JLabel("New label");
		
		JButton btnOK = new JButton("입력 확인");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Ok_And_Dispose();
			}
		});
		
		JButton btnCancel = new JButton("편집취소");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				retValue = CANCEL_OPTION;
				dispose();
			}
		});
		
		chkDelete = new JCheckBox("이 음을 삭제");
		
		JButton btnChordAuto = new JButton("자동음지정");
		btnChordAuto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//String chord = tfChordName.getText();
				//if ()
				////// TODO: 코드 테이블을 만들고 테이블을 검색하여 G, C, E, A 의 연주값을 자동으로 입력하게 해 줄 것.
			}
		});
		
		tfChordName = new JTextField();
		tfChordName.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lbl_E)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tf_E, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lbl_C)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tf_C, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblTechnics)
										.addComponent(lbl_G))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(tfTechnics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(tf_G, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lbl_A, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tf_A, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(lbllyric)
										.addComponent(lblChord))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(tfChordName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(tfLyric, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
							.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblTimeStamp)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSound_C)
								.addComponent(lblSound_E)
								.addComponent(lblSound_G)
								.addComponent(lblSound_A)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(spnrTimeStamp, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblMsec, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(chkDelete, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)))
							.addGap(12))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnChordAuto)
							.addContainerGap())))
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnOK)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel)
					.addGap(35))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTimeStamp)
						.addComponent(lblMsec)
						.addComponent(spnrTimeStamp, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbllyric)
						.addComponent(tfLyric, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblChord)
						.addComponent(btnChordAuto)
						.addComponent(tfChordName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tf_A, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl_A)
						.addComponent(lblSound_A))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_E)
						.addComponent(lblSound_E)
						.addComponent(tf_E, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tf_C, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl_C)
						.addComponent(lblSound_C))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tf_G, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl_G)
						.addComponent(lblSound_G))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTechnics)
						.addComponent(tfTechnics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
					.addComponent(chkDelete)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOK)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);

		getContentPane().addKeyListener(this);

		/* ESC 키 처리 할 수 있게..  TODO: 기왕이면 Ctrl+ENTER 키에 의해 입력완료 할 수 있도록. */
		spnrTimeStamp.addKeyListener(this);
		tfLyric.addKeyListener(this);
		tf_A.addKeyListener(this);
		tf_E.addKeyListener(this);
		tf_C.addKeyListener(this);
		tf_G.addKeyListener(this);
		tfTechnics.addKeyListener(this);
		btnOK.addKeyListener(this);
		btnCancel.addKeyListener(this);

	    /*  this.getRootPane().registerKeyboardAction(this,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);
	    */
	    this.getRootPane().registerKeyboardAction(this,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	    addWindowListener(new WindowAdapter(){ 
    	  public void windowOpened( WindowEvent e){ 
    	    tfLyric.requestFocus();
    	  } 
	  	});     
	}

	private void Ok_And_Dispose() {
		if (chkDelete.isSelected() ) {
			retValue = DELETE_OPTION;
		} else {
			retValue = OK_OPTION;
		}
		dispose();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	/*	if (e.getSource() instanceof JButton) {
            JButton button = (JButton) e.getSource();
            if(button.getText() != "OK"){
				Ok_And_Dispose();
            } else if(button.getText() != "CANCEL") {
        		retValue = CANCEL_OPTION;
            }
    		dispose();
        }
		/////////// or Other UI components...
	 */
	}

	public void setData(Note data) {
		System.out.println("NoteInputDlg. setData() with" + data );

		System.out.println("- TS:"+ data.timeStamp+ ", chord:" + data.chordName + ", tab:"+data.tab );
		if (data.tab != null) {
			System.out.print("\t tab:");
			for (int i=0; i<data.tab.length; i++) {
				System.out.print(", "+data.tab[i] );
			}
			System.out.println("\t");
		}		
		
//		if (data==null) {
//			originData.timeStamp = 0;
//			originData.chordName = "";
//			originData.technic = "";
//			originData.tab = new String[4]; 
//			originData.note = new String[4];
//			originData.lyric = "";
//			System.out.println("originData-> TS:" + originData.timeStamp + ", lyric:" + originData.lyric + ", chord:" + originData.chordName + ", tab:" + originData.tab );
//		} else {
			originData = data;

			spnrTimeStamp.setValue( originData.timeStamp );
			tfLyric.setText(originData.lyric);
			tfChordName.setText(originData.chordName);
			if (originData.tab == null) {
				System.out.println("tab is NULL" );
				originData.tab = new String[4];
				tf_A.setText("");
				tf_E.setText("");
				tf_C.setText("");
				tf_G.setText("");
			} else {
				for (int i=0; i<originData.tab.length; i++) {
					System.out.println("tab["+i+"]="+ originData.tab[i] );
					if (originData.tab[i]==null) {
						continue;
					}
					if (originData.tab[i].indexOf("G") >= 0) {
						tf_G.setText(originData.tab[i].substring(1) );
					} else if (originData.tab[i].indexOf("C") >= 0) {
						tf_C.setText(originData.tab[i].substring(1) );
					} else if (originData.tab[i].indexOf("E") >= 0) {
						tf_E.setText(originData.tab[i].substring(1) );
					} else if (originData.tab[i].indexOf("A") >= 0) {
						tf_A.setText(originData.tab[i].substring(1) );
					} else {
						System.err.println("Something Wrong in 'originData.tab[]' " + originData.tab[i] );
					}
				}
			}
			tfTechnics.setText(originData.technic);
			System.out.println("originData-> TS:" + originData.timeStamp + ", lyric:" + originData.lyric + ", chord:" + originData.chordName + ", tab:" + originData.tab );
//		}
		repaint();
	}

	public Note getData() {
		originData.timeStamp = (int)spnrTimeStamp.getValue();
		originData.lyric = tfLyric.getText();
		originData.chordName = tfChordName.getText();
//		if (originData.tab ==null) {
		originData.tab = new String[4];
//		}
		originData.tab[0] = "A"+tf_A.getText();
		originData.tab[1] = "E"+tf_E.getText();
		originData.tab[2] = "C"+tf_C.getText();
		originData.tab[3] = "G"+tf_G.getText();
		originData.technic = tfTechnics.getText();

		System.out.println("originData-> TS:" + originData.timeStamp + ", lyric:" + originData.lyric + ", chord:" + originData.chordName + ", tab:" + originData.tab );
		return originData;
	}

	public int showDialog() {
		setModal(true);
		setVisible(true);
		return 	retValue;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("KeyTyped : keyCode="+ e.getKeyCode()  + ", e="+e);
		//if (e.getKeyCode() == KEYCODE_ESC ) {
		//	dispose();
		//}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
	     //String s = e.getKeyText(e.getKeyCode()); // 키값
	     //System.out.println("입력된 키 :" + s + "키가 눌렸습니다.");
	     if(e.getKeyChar() == KeyEvent.VK_ESCAPE ){ 
			retValue = CANCEL_OPTION;
			dispose();
	     } else if (e.getKeyChar() == KeyEvent.VK_ENTER ){
	    	 Ok_And_Dispose();
	     }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
