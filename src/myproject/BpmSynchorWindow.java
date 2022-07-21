package myproject;

import java.awt.EventQueue;
import java.text.ParseException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.JFileChooser;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class BpmSynchorWindow {

	private JFrame frmUkeBpmSynchronizer;
	private JTextField tfComment;
	private JTextField tfSongTitle;
	private final ButtonGroup btngrpQuaver = new ButtonGroup();
	private WaveSynchPane waveSynchPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BpmSynchorWindow window = new BpmSynchorWindow();
					window.frmUkeBpmSynchronizer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BpmSynchorWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUkeBpmSynchronizer = new JFrame();
		frmUkeBpmSynchronizer.setTitle("UKE Bpm Synchronizer");
		frmUkeBpmSynchronizer.setBounds(100, 100, 1018, 580);
		frmUkeBpmSynchronizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUkeBpmSynchronizer.setMinimumSize(new Dimension(935, 580));
		
		waveSynchPane = new WaveSynchPane();
		
		JPanel panelFileManager = new JPanel();
		panelFileManager.setBorder(new BevelBorder(BevelBorder.RAISED));
		frmUkeBpmSynchronizer.getContentPane().add(panelFileManager, BorderLayout.NORTH);
		
		JButton btnNewFile = new JButton("New File");
		JButton btnOpenFile = new JButton("Open File..");
		JButton btnSetWave = new JButton("Set WAVE");
		btnSetWave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				System.out.println("button [Set WAVE] was clicked.");

				File f = showFileDialog();
				if (f==null) {
					System.out.println("File Not specified.");
					return;
				}
				
				byte[] Header = new byte[44];
				byte[] Buffer = new byte[(int)f.length()];
				try {
					FileInputStream is;
					is = new FileInputStream( f );
					int byteRead = -1;
					byteRead = is.read(Header);
					byteRead = is.read(Buffer);
					is.close();

//					System.out.println( "Number of byteRead="+ byteRead + " : " + Buffer[0]+"," + Buffer[1]+"," + Buffer[2]+"," + Buffer[3]+"," + Buffer[4]+"," + Buffer[5]+"," + Buffer[6]+"," + Buffer[7]+"," + Buffer[8]+"," + Buffer[9]+"," + Buffer[10]+"," + Buffer[11]+"," + Buffer[12] );
					if (waveSynchPane != null) {
						waveSynchPane.setWaveData(Buffer);
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		JButton btnSetAlbumImage = new JButton("Set Album Image");
		JButton btnWriteFile = new JButton("WriteFile");
		GroupLayout gl_panelFileManager = new GroupLayout(panelFileManager);
		gl_panelFileManager.setHorizontalGroup(
			gl_panelFileManager.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFileManager.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOpenFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSetWave)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSetAlbumImage)
					.addPreferredGap(ComponentPlacement.RELATED, 348, Short.MAX_VALUE)
					.addComponent(btnWriteFile)
					.addContainerGap())
		);
		gl_panelFileManager.setVerticalGroup(
			gl_panelFileManager.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFileManager.createParallelGroup(Alignment.BASELINE)
					.addComponent(btnOpenFile)
					.addComponent(btnNewFile))
				.addGroup(gl_panelFileManager.createParallelGroup(Alignment.BASELINE)
					.addComponent(btnSetAlbumImage)
					.addComponent(btnWriteFile)
					.addComponent(btnSetWave))
		);
		panelFileManager.setLayout(gl_panelFileManager);
		
		JPanel panelStatusBar = new JPanel();
		panelStatusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		frmUkeBpmSynchronizer.getContentPane().add(panelStatusBar, BorderLayout.SOUTH);
		
		JLabel lblStatus = new JLabel("Status...");
		
		JLabel lblStatusCurrentSetting = new JLabel("Current setting...");
		lblStatusCurrentSetting.setHorizontalAlignment(SwingConstants.RIGHT);
		GroupLayout gl_panelStatusBar = new GroupLayout(panelStatusBar);
		gl_panelStatusBar.setHorizontalGroup(
			gl_panelStatusBar.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelStatusBar.createSequentialGroup()
					.addComponent(lblStatus, GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStatusCurrentSetting, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE))
		);
		gl_panelStatusBar.setVerticalGroup(
			gl_panelStatusBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelStatusBar.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panelStatusBar.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblStatus)
						.addComponent(lblStatusCurrentSetting)))
		);
		panelStatusBar.setLayout(gl_panelStatusBar);
		
		JPanel panelEditArea = new JPanel();
		frmUkeBpmSynchronizer.getContentPane().add(panelEditArea, BorderLayout.CENTER);
		panelEditArea.setLayout(new BorderLayout(0, 0));
		
		JPanel panelComments = new JPanel();
		panelEditArea.add(panelComments, BorderLayout.SOUTH);
		
		JLabel lblComment = new JLabel("Comment");
		
		tfComment = new JTextField();
		tfComment.setColumns(10);
		GroupLayout gl_panelComments = new GroupLayout(panelComments);
		gl_panelComments.setHorizontalGroup(
			gl_panelComments.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelComments.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblComment, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfComment, GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panelComments.setVerticalGroup(
			gl_panelComments.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panelComments.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_panelComments.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblComment, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(tfComment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
		);
		panelComments.setLayout(gl_panelComments);
		
		JPanel panelValueSetting = new JPanel();
		panelValueSetting.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panelEditArea.add(panelValueSetting, BorderLayout.NORTH);
		
		JLabel lblSongTitle = new JLabel("Song Title");
		
		tfSongTitle = new JTextField();
		tfSongTitle.setColumns(10);
		
		JLabel lblWaveFileName = new JLabel("WAVE File:");
		
		JLabel lblWaveFilePath = new JLabel("Use [Set WAVE] button.");
		
		JLabel lblAlbumImage = new JLabel("Album Image:");
		lblAlbumImage.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel imgAlbumImage = new JLabel("");
		imgAlbumImage.setSize(128,72);		// setBounds(900, 100, 128, 72);
		imgAlbumImage.setHorizontalAlignment(SwingConstants.CENTER);
		imgAlbumImage.setIcon(new ImageIcon("C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\ukulele_icon.png"));
		
		JLabel lblBeats = new JLabel("Beat:");
		lblBeats.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JRadioButton rdbtnQuaver = new JRadioButton("quaver (\u266A 8\uBD84\uC74C\uD45C)");
		btngrpQuaver.add(rdbtnQuaver);
		
		JRadioButton rdbtnSemiQuaver = new JRadioButton("semi-quaver (\u266C 16\uBD84\uC74C\uD45C)");
		btngrpQuaver.add(rdbtnSemiQuaver);
		
		JLabel lblMeter = new JLabel("Meter:");
		lblMeter.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JComboBox cbMeter = new JComboBox();
		cbMeter.setModel(new DefaultComboBoxModel(new String[] {"2/4", "3/4", "4/4", "6/8"}));
		
		JLabel lblBpm = new JLabel("BPM:");
		lblBpm.setHorizontalAlignment(SwingConstants.RIGHT);
		
		final JSpinner spnrBpm = new JSpinner();
		spnrBpm.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				System.out.println("spinner Changed Handler.."+ spnrBpm.getValue() );
				//				spnrBpm.getNumber();
				waveSynchPane.setBpm( Integer.parseInt(""+spnrBpm.getValue()) );
			}
		});
		spnrBpm.setModel(new SpinnerNumberModel(80, 20, 280, 1));
		
		JLabel lblCategory = new JLabel("Category:");
		
		JComboBox cbCategory = new JComboBox();
		cbCategory.setModel(new DefaultComboBoxModel(new String[] {"Stroke (Rhythm)", "Melody", "FingerStyle"}));
		
		JLabel lblLevel = new JLabel("Level:");
		
		JComboBox cbLevel = new JComboBox();
		cbLevel.setModel(new DefaultComboBoxModel(new String[] {"\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF"}));
		
		JButton btnToStart = new JButton("Move to Start");
		
		JButton btnPlay = new JButton("Play");
		
		JButton btnPause = new JButton("Pause");
		
		JLabel lblJumpTo = new JLabel("Jump to");
		
		JFormattedTextField tfJumpToFormatted = new JFormattedTextField();
		tfJumpToFormatted.setText("##:##.###");
		tfJumpToFormatted.setFocusLostBehavior(JFormattedTextField.COMMIT);
		try {
			MaskFormatter  formatter1 = new MaskFormatter("##:##.###");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JLabel lblPlayingOffset = new JLabel("playing offset");
		
		JFormattedTextField formattedTextField = new JFormattedTextField();
		formattedTextField.setText("##:##.###");
		GroupLayout gl_panelValueSetting = new GroupLayout(panelValueSetting);
		gl_panelValueSetting.setHorizontalGroup(
			gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelValueSetting.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelValueSetting.createSequentialGroup()
							.addComponent(btnToStart)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPlay)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPause)
							.addGap(18)
							.addComponent(lblJumpTo)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfJumpToFormatted, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(lblPlayingOffset)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(formattedTextField, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelValueSetting.createSequentialGroup()
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblSongTitle)
								.addComponent(lblMeter, GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
								.addComponent(lblBeats, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelValueSetting.createSequentialGroup()
									.addComponent(rdbtnQuaver)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnSemiQuaver))
								.addComponent(tfSongTitle, GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
								.addGroup(gl_panelValueSetting.createSequentialGroup()
									.addComponent(cbMeter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblBpm, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spnrBpm, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panelValueSetting.createSequentialGroup()
									.addComponent(lblCategory)
									.addGap(4)
									.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panelValueSetting.createSequentialGroup()
											.addComponent(cbCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addComponent(lblLevel)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(cbLevel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(3))
										.addGroup(gl_panelValueSetting.createSequentialGroup()
											.addComponent(lblWaveFileName)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lblWaveFilePath, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE))))
								.addComponent(lblAlbumImage, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(imgAlbumImage, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGap(0))
		);
		gl_panelValueSetting.setVerticalGroup(
			gl_panelValueSetting.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelValueSetting.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.TRAILING)
						.addComponent(imgAlbumImage, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
						.addGroup(gl_panelValueSetting.createSequentialGroup()
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSongTitle)
								.addComponent(lblAlbumImage)
								.addComponent(tfSongTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblWaveFilePath)
								.addComponent(lblWaveFileName)
								.addComponent(lblBeats)
								.addComponent(rdbtnQuaver)
								.addComponent(rdbtnSemiQuaver))
							.addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMeter)
								.addComponent(lblCategory)
								.addComponent(cbCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblLevel)
								.addComponent(cbLevel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(cbMeter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblBpm)
								.addComponent(spnrBpm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnToStart)
								.addComponent(btnPlay)
								.addComponent(btnPause)
								.addComponent(lblJumpTo)
								.addComponent(tfJumpToFormatted, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPlayingOffset)
								.addComponent(formattedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		panelValueSetting.setLayout(gl_panelValueSetting);

		waveSynchPane.setVisible(true);
		waveSynchPane.setFocusable(true);
		waveSynchPane.setRequestFocusEnabled(true);
		waveSynchPane.grabFocus();

		waveSynchPane.addMouseListener(waveSynchPane);
		waveSynchPane.addMouseWheelListener(waveSynchPane);
		waveSynchPane.addMouseMotionListener(waveSynchPane);
		waveSynchPane.addKeyListener(waveSynchPane);		// KeyListener
		panelEditArea.add(waveSynchPane, BorderLayout.CENTER);
		
	}

	public File showFileDialog() {
		final JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		return  fc.getSelectedFile();
	}
}
