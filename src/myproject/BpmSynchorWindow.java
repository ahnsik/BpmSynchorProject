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
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class BpmSynchorWindow {

	private JFrame frmUkeBpmSynchronizer;
	private JTextField tfComment;
	private JTextField tfSongTitle;
	private final ButtonGroup btngrpQuaver = new ButtonGroup();
	private WaveSynchPane waveSynchPane;
	private WavPlay player = null;

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

				File f = showFileDialog();
				if (f==null) {
					System.out.println("File Not specified.");
					return;
				}

				System.out.println("Selected File:" + f.getPath() );

				/*	-- WAV 파일인 경우.	*/
				byte[] Header = new byte[44];
				byte[] Buffer = new byte[(int)f.length()];

				try {
					FileInputStream is;

					is = new FileInputStream( f );

					int byteRead = -1;
					byteRead = is.read(Header);			// *.wav파일 헤더포맷 https://anythingcafe.tistory.com/2
					System.out.println("Header Chunk Size:"+( ((Header[17]&0xFF)<<8)+(Header[16]&0xFF)) );	// = 4바이트 이지만 Header 크기는 그리 크지 않으므로, 하위 2바이트만. (little endian)
					System.out.println("num of Channel:"+( ((Header[23]&0xFF)<<8)+(Header[22]&0xFF)) );	// 채널 수 : 1=Mono, 2=Stereo, 5:4channel. 6:6channel, etc..
					System.out.println("Sample Rate:"+ ((Header[24]&0xFF)+((Header[25]&0xFF)<<8)) );				//( ((long)(Header[25]&0xFF)<<8)+Header[24]) );	// = 4바이트 little endian
					System.out.println("byte rate =1초당 byte 수:"+( ((Header[31]&0xFF)<<24)+((Header[30]&0xFF)<<16)+((Header[29]&0xFF)<<8)+(Header[28]&0xFF)) );	
					System.out.println("Sample당 bit수:"+( ((Header[35]&0xFF)<<8)+(Header[34]&0xFF)) );	
					System.out.println("Block Align:"+( ((Header[33]&0xFF)<<8)+(Header[32]&0xFF)) );		// 	

					byteRead = is.read(Buffer);
					is.close();

					if (player != null) {		// 기존에 존재하는 Thread 는 제거해야 하는데... 제거 방법을 아직 모르겠다. 
						player.pause();
						player.interrupt();
//						try {
//							player.join();
//						} catch (InterruptedException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}

						player = null;
					}
					player = new WavPlay(Header, Buffer);
					player.play();
					player.start();

					int num_of_channel = (Header[22]&0xFF)+((Header[23]&0xFF)<<8);
					int num_bits_of_sample = (Header[34]&0xFF)+((Header[35]&0xFF)<<8);
					int SampleRate = (Header[24]&0xFF)+((Header[25]&0xFF)<<8);
					int block_align = ((Header[33]&0xFF)<<8)+(Header[32]&0xFF);		// 1개 Sample 당 byte 수. (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel
					System.out.println("sample stripe = "+block_align );	

					byte[] rawBuffer;
					if (num_of_channel==1) {		// mono 채널
						if (num_bits_of_sample ==8 ) {	// 8bit 샘플
							waveSynchPane.setWaveData(Buffer);
							frmUkeBpmSynchronizer.repaint();		// 버퍼를 그대로 그냥 전달해도 됨.
							return;
						} else {					// 16bit 샘플이면, 상위 8비트만 처리.
							rawBuffer = new byte[(byteRead/block_align)];
							for (int i=0; i<byteRead-block_align; i+= block_align ) {
								rawBuffer[i/block_align] = (byte) (Buffer[i+1]-128);		//	Buffer[i+1] 한 거는, 16bit 샘플을 8비트 샘플로 처리하기 위함. 
							}
						}
					} else {		// stereo 채널 또는 다채널.	// 원래는 여기서도 16bit 샘플인지 판단해야 하지만, 그냥 8bit 인 경우에도 1바이트 손해볼 뿐이므로 그냥 넘어가자.
						rawBuffer = new byte[(byteRead/block_align)];
						for (int i=0; i<byteRead-block_align; i+= block_align ) {
							rawBuffer[i/block_align] = (byte) (Buffer[i+1]-128);		//	Buffer[i+1] 한 거는, 16bit 샘플을 8비트 샘플로 처리하기 위함. 
						}
					}

					System.out.println( "Number of byteRead="+ byteRead + " : " + Buffer[0]+"," + Buffer[4]+"," + Buffer[8]+"," + Buffer[12]+"," + Buffer[16]+"," + Buffer[20]+"," + Buffer[24]+"," + Buffer[28]+"," + Buffer[32]+"," + Buffer[36]+"," + Buffer[40]+"," + Buffer[44]+"," + Buffer[48] );
					System.out.println( "Number of byteRead="+ (byteRead/block_align) + " : " + rawBuffer[0]+"," + rawBuffer[1]+"," + rawBuffer[2]+"," + rawBuffer[3]+"," + rawBuffer[4]+"," + rawBuffer[5]+"," + rawBuffer[6]+"," + rawBuffer[7]+"," + rawBuffer[8]+"," + rawBuffer[9]+"," + rawBuffer[10]+"," + rawBuffer[11]+"," + rawBuffer[12] );

					if (waveSynchPane != null) {
						waveSynchPane.setWaveData(rawBuffer);
						frmUkeBpmSynchronizer.repaint();
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
		rdbtnQuaver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				JRadioButton btn=(JRadioButton)e.getSource();
				waveSynchPane.setQuaver( 0 );	// 1 = semi-quaver
			}
		});
		btngrpQuaver.add(rdbtnQuaver);
		
		JRadioButton rdbtnSemiQuaver = new JRadioButton("semi-quaver (\u266C 16\uBD84\uC74C\uD45C)");
		rdbtnSemiQuaver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				waveSynchPane.setQuaver( 1 );	// 1 = semi-quaver
			}
		});
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
		spnrBpm.setModel(new SpinnerNumberModel(60, 20, 280, 1));
		
		JLabel lblCategory = new JLabel("Category:");
		
		JComboBox cbCategory = new JComboBox();
		cbCategory.setModel(new DefaultComboBoxModel(new String[] {"Stroke (Rhythm)", "Melody", "FingerStyle"}));
		
		JLabel lblLevel = new JLabel("Level:");
		
		JComboBox cbLevel = new JComboBox();
		cbLevel.setModel(new DefaultComboBoxModel(new String[] {"\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF"}));
		
		JButton btnToStart = new JButton("Move to Start");
		
		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Play Button Clicked.");
				if (player == null)
					return;
				player.restart();
			}
		});
		
		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("[Pause].");
				if (player == null)
					return;
				player.pause();
			}
		});
		
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
