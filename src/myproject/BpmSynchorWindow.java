package myproject;

import java.awt.EventQueue;
import java.awt.Image;
import java.text.ParseException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JFormattedTextField;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.UIManager;
import java.awt.SystemColor;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

public class BpmSynchorWindow implements KeyListener {

	private final static int	ICON_SIZE = 92;

	private final ButtonGroup btngrpQuaver = new ButtonGroup();

	private JFrame frmUkeBpmSynchronizer;
	private JTextField tfComment;
	private JTextField tfSongTitle;
	private JLabel imgAlbumImage;
	private JLabel lblWaveFilePath;
	private JSpinner spnrBpm;
	private JSpinner spnrOffset = new JSpinner();

	private WaveSynchPane waveSynchPane;
	private WavPlay player = null;
	private UkeData data = null;

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
		initialize();		// 기존에 data 객체가 존재한다면 여기에서 null 로 초기화 되므로 Garbage Collection 대상이 된다.
		makeNew();			// initalize 는 instance 생성 및 UI 객체 세팅하는 개념이고,  makeNew 함수는 값들을 초기화. 
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
		
		data = null;
		player = null;
		waveSynchPane = new WaveSynchPane();
		waveSynchPane.setBackground(UIManager.getColor("inactiveCaptionBorder"));

		imgAlbumImage = new JLabel("");
		imgAlbumImage.setBackground(SystemColor.inactiveCaptionBorder);
		imgAlbumImage.setSize(ICON_SIZE, ICON_SIZE);		// setBounds(900, 100, 128, 72);
		imgAlbumImage.setHorizontalAlignment(SwingConstants.CENTER);
		imgAlbumImage.setIcon(new ImageIcon("C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\ukulele_icon.png"));

		JPanel panelFileManager = new JPanel();
		panelFileManager.setBorder(new BevelBorder(BevelBorder.RAISED));
		frmUkeBpmSynchronizer.getContentPane().add(panelFileManager, BorderLayout.NORTH);

		JButton btnNewFile = new JButton("New File");
		btnNewFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Preparing New uke DATA");
				if (data != null) {
					int result = JOptionPane.showConfirmDialog(null, "편집중인 데이터는 모두 사라집니다. 저장하지 않고 새로 시작할까요?", "새로만들기 확인", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.CLOSED_OPTION) {
						// Dialog 창을 그냥 취소한 경우. - 아무것도 안함.
					} else if (result == JOptionPane.YES_OPTION) {
						makeNew();
					} else {
						// CANCEL 버튼인 경우 - 아무것도 안함.
					}
				} else {	// 원래 부터 null 인 상태.
					makeNew();
				}
				frmUkeBpmSynchronizer.repaint();
			}
		});

		JButton btnOpenFile = new JButton("Open File..");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if (data != null) {		// 편집 중인 데이터가 있으면  확인할 것.
				//	int result = JOptionPane.showConfirmDialog(null, "편집중인 데이터는 모두 사라집니다. 저장하지 않고 진행할까요?", "파일 열기", JOptionPane.YES_NO_OPTION);
				//	if (result != JOptionPane.YES_OPTION) {
				//		return;		// YES 가 아니면 아무것도 안하고 그냥 종료함.
				//	}
				//}

				File f = showFileDialog();
				if (f==null) {
					System.out.println("File Not specified.");
					return;
				}
				System.out.println("Selected Uke File:" + f.getPath() );
				System.out.println(" -- getParent():" + f.getParent() );

				data = new UkeData();
				data.loadFromFile(f);

				if (waveSynchPane != null) {
					waveSynchPane.setUkeData(data);
				}

				if (data.mMusicUrl != null) {
					System.out.println("\t>> File:"+data.mMusicUrl );
					File mp3file =  new File(f.getParent()+"/"+data.mMusicUrl);

					if (waveSynchPane != null) {
						setWaveData(mp3file); 		// 여기에서 player 객체를 생성하고 연주를 시작한다.
					}

					System.out.println("\tCheck the player object:" + player );
//					System.out.println("Music file path:" + f.getParent() );			// new File(f.getParent(), data.mMusicURL) );
//			    	System.out.println("Music file set:" + f.getParent()+"/"+data.mMusicUrl );			// new File(f.getParent(), data.mMusicURL) );
//			    	System.out.println("MP3 file. getPath()= " + mp3file.getPath() );
			    } else {
			    	System.out.println("No music scpecified.");
					player = null;
			    }

				lblWaveFilePath.setText(data.mMusicUrl);
				setAlbumImage(imgAlbumImage, f.getParent()+"/"+data.mThumbnailUrl );
				setStuffFromData(data);
			}
		});
		JButton btnSetWave = new JButton("Set WAVE");
		btnSetWave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = showFileDialog();
				if (f==null) {
					System.out.println("File Not specified.");
					return;
				}
				System.out.println("Selected File:" + f.getPath() );
				setWaveData(f); 
			}
		});

		JButton btnSetAlbumImage = new JButton("Set Album Image");
		btnSetAlbumImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = showFileDialog();
				if (f==null) {
					System.out.println("File not specified.");
					setAlbumImage(imgAlbumImage, ".\\src\\resource\\ukulele_icon.png");
					return;
				} else {
					if (data != null) {
						data.mThumbnailUrl = f.getName();
						System.out.println("PATH:"+f.getPath()+", fileName:"+f.getName() );
						setAlbumImage(imgAlbumImage, f.getPath() );					
					} else {
						System.out.println("Uke Data Not READY !! you must make new Uke DATA !!" );
//						makeNew();
					}
				}
			}
		});
		JButton btnWriteFile = new JButton("WriteFile");
		btnWriteFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File f = showFileDialog();
//				if (f==null) {
//					data.SaveToFile(f);
//					System.out.println("File Not specified.");
//					return;
//				}
				System.out.println("Selected Uke File:" + f.getPath() );
				System.out.println(" -- getParent():" + f.getParent() );
				data.SaveToFile(f);
			}
		});

		GroupLayout gl_panelFileManager = new GroupLayout(panelFileManager);
		gl_panelFileManager.setHorizontalGroup(
			gl_panelFileManager.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFileManager.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnOpenFile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnWriteFile)
					.addPreferredGap(ComponentPlacement.RELATED, 533, Short.MAX_VALUE)
					.addComponent(btnSetWave)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSetAlbumImage)
					.addContainerGap())
		);
		gl_panelFileManager.setVerticalGroup(
			gl_panelFileManager.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelFileManager.createParallelGroup(Alignment.BASELINE)
					.addComponent(btnOpenFile)
					.addComponent(btnNewFile)
					.addComponent(btnWriteFile))
				.addGroup(gl_panelFileManager.createParallelGroup(Alignment.BASELINE)
					.addComponent(btnSetAlbumImage)
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
		lblWaveFilePath = new JLabel("Use [Set WAVE] button to load wave file. music file name will be shown here.");
		
		JLabel lblBeats = new JLabel("Beat:");
		lblBeats.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JRadioButton rdbtnQuaver = new JRadioButton("quaver (\u266A 8\uBD84\uC74C\uD45C)");
		rdbtnQuaver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				JRadioButton btn=(JRadioButton)e.getSource();
				waveSynchPane.setQuaver( 8 );	// 1 = semi-quaver
			}
		});
		btngrpQuaver.add(rdbtnQuaver);
		
		JRadioButton rdbtnSemiQuaver = new JRadioButton("semi-quaver (\u266C 16\uBD84\uC74C\uD45C)");
		rdbtnSemiQuaver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				waveSynchPane.setQuaver( 16 );	// 1 = semi-quaver
			}
		});
		btngrpQuaver.add(rdbtnSemiQuaver);
		
		JLabel lblMeter = new JLabel("Meter:");
		lblMeter.setHorizontalAlignment(SwingConstants.RIGHT);
		
		@SuppressWarnings("rawtypes")
		JComboBox cbMeter = new JComboBox();
		cbMeter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(cbMeter.getSelectedItem().toString() ) {
					case "2/4":
						waveSynchPane.setMeter( 0 );		// 0=2/2, 1=3/4, 2=4/4, 3=6/8 박자. 
						break;
					case "3/4":
						waveSynchPane.setMeter( 1 );
						break;
					case "6/8":
						waveSynchPane.setMeter( 3 );
						break;
					default:
						waveSynchPane.setMeter( 2 );
						break;
				}
			}
		});
		cbMeter.setModel(new DefaultComboBoxModel(new String[] {"2/4", "3/4", "4/4", "6/8"}));
		cbMeter.setSelectedIndex(2);
		
		JLabel lblBpm = new JLabel("BPM:");
		lblBpm.setHorizontalAlignment(SwingConstants.RIGHT);
		
		spnrBpm = new JSpinner();
		spnrBpm.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				System.out.println("spinner Changed Handler.."+ spnrBpm.getValue() );
				//				spnrBpm.getNumber();
				waveSynchPane.setBpm( Float.parseFloat(""+spnrBpm.getValue()) );
			}
		});
		spnrBpm.setModel(new SpinnerNumberModel(60.0f, 20.0f, 280.0f, 1.0f));
		
		JLabel lblCategory = new JLabel("Category:");
		
		JComboBox cbCategory = new JComboBox();
		cbCategory.setModel(new DefaultComboBoxModel(new String[] {"Stroke (Rhythm)", "Melody", "FingerStyle"}));
		
		JLabel lblLevel = new JLabel("Level:");
		
		JComboBox cbLevel = new JComboBox();
		cbLevel.setModel(new DefaultComboBoxModel(new String[] {"\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF"}));

		JButton btnToStart = new JButton("Move to Start");
		btnToStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("start Button Clicked.");
				player.pause();
				player.setPlayingPositionWithMilliSecond(0);
			}
		});

		JButton btnPlay = new JButton("Play");
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Play Button Clicked.");
				if (player == null) {
					System.out.println("Player is null.");
					return;
				}
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
		tfJumpToFormatted.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("[tfJumpTo] mouseWheelMoved() called");
			}
		});
		tfJumpToFormatted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("[tfJumpTo] actionPerformed() called : " + tfJumpToFormatted.getText());
				String jumpToMsecStr = tfJumpToFormatted.getText();
				try {
					int msec = Integer.parseInt(jumpToMsecStr);
					System.out.println("Jump to " + msec + " milli-second");
					waveSynchPane.setDrawStart( msec );
				} catch (NumberFormatException e1) {
//					System.err.println("not a milli-second. need to convert with Format.");
					//Pattern p = Pattern.compile("\\d{2}:\\d{2}.\\d{3}"); 	// for Detail,  refer: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
					//p.matcher(jumpToMsecStr).matches();

					try (Scanner timeStr = new Scanner(jumpToMsecStr)) {
						timeStr.useDelimiter(":");
						int min = timeStr.nextInt();
						int sec = timeStr.nextInt();
						int msec = timeStr.nextInt();
						System.out.println("min: "+ min + " minutes "+sec+" seconds "+msec+" milli-sec.");
						msec = min*60000+sec*1000+msec;
						waveSynchPane.setDrawStart( msec );

					} catch (NoSuchElementException e3) {
						int result = JOptionPane.showConfirmDialog(null, "이동위치를 다음과 같은 형식으로 입력해 주세요.\n MM : SS : msec", "입력형식 오류", JOptionPane.OK_OPTION);
					}
				}
			}
		});
		tfJumpToFormatted.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent event) {
				System.out.println("[tfJumpTo] caretPositionChanged() called");
			}
			public void inputMethodTextChanged(InputMethodEvent event) {
				System.out.println("[tfJumpTo] inputMethodTextChanged() called");
//				waveSynchPane.setDrawStart( Integer.parseInt(""+spnrOffset.getValue()) );
			}
		});
		tfJumpToFormatted.setText("00:00:000");
		tfJumpToFormatted.setFocusLostBehavior(JFormattedTextField.COMMIT);
		try {
			MaskFormatter  formatter1 = new MaskFormatter("##:##.###");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JLabel lblPlayingOffset = new JLabel("WAV offset");
		JSpinner spnrOffset = new JSpinner();
		spnrOffset.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				System.out.println("spnrOffset Changed Handler.."+ spnrOffset.getValue() );
//				waveSynchPane.setDrawStart( Integer.parseInt(""+spnrOffset.getValue()) );
				waveSynchPane.setWaveOffset( Integer.parseInt(""+spnrOffset.getValue()) );
			}
		});

		JButton btnZoomIn = new JButton("");
		btnZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				waveSynchPane.viewZoom(-1);		// 확대
			}
		});
		btnZoomIn.setIcon(new ImageIcon(new ImageIcon( "C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\zoom_in.png" ).getImage().getScaledInstance( 16,16, Image.SCALE_DEFAULT)) );

		JButton btnZoomOut = new JButton("");
		btnZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				waveSynchPane.viewZoom(1);		// 축소
			}
		});
		btnZoomOut.setIcon(new ImageIcon(new ImageIcon( "C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\zoom_out.png" ).getImage().getScaledInstance( 16,16, Image.SCALE_DEFAULT)) );

		GroupLayout gl_panelValueSetting = new GroupLayout(panelValueSetting);
		gl_panelValueSetting.setHorizontalGroup(
			gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelValueSetting.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelValueSetting.createSequentialGroup()
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblSongTitle)
								.addComponent(lblMeter, GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
								.addComponent(lblBeats, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING, false)
								.addComponent(tfSongTitle)
								.addGroup(gl_panelValueSetting.createSequentialGroup()
									.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelValueSetting.createSequentialGroup()
											.addComponent(rdbtnQuaver)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(rdbtnSemiQuaver)
											.addGap(18)
											.addComponent(lblCategory))
										.addGroup(gl_panelValueSetting.createSequentialGroup()
											.addComponent(cbMeter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(lblBpm, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spnrBpm, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(lblPlayingOffset)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(spnrOffset, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addComponent(lblLevel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING, false)
										.addComponent(cbLevel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(cbCategory, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblWaveFileName))
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
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnZoomIn)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnZoomOut)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblWaveFilePath, GroupLayout.PREFERRED_SIZE, 234, Short.MAX_VALUE)
						.addComponent(imgAlbumImage, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panelValueSetting.setVerticalGroup(
			gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelValueSetting.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSongTitle)
						.addComponent(tfSongTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblWaveFileName)
						.addComponent(lblWaveFilePath))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelValueSetting.createSequentialGroup()
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblBeats)
								.addComponent(rdbtnQuaver)
								.addComponent(rdbtnSemiQuaver)
								.addComponent(lblCategory)
								.addComponent(cbCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMeter)
								.addComponent(cbMeter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblBpm)
								.addComponent(spnrBpm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPlayingOffset)
								.addComponent(spnrOffset, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblLevel)
								.addComponent(cbLevel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelValueSetting.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnToStart)
								.addComponent(btnPlay)
								.addComponent(btnPause)
								.addComponent(lblJumpTo)
								.addComponent(tfJumpToFormatted, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnZoomIn)
								.addComponent(btnZoomOut)))
						.addComponent(imgAlbumImage, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
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

	/*  refer : https://stackoverflow.com/questions/14085199/mp3-to-wav-conversion-in-java  */
	public static byte [] getAudioDataBytes(byte [] sourceBytes, AudioFormat audioFormat) throws UnsupportedAudioFileException, IllegalArgumentException, Exception{
        if(sourceBytes == null || sourceBytes.length == 0 || audioFormat == null){
            throw new IllegalArgumentException("Illegal Argument passed to this method");
        }

        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = null;
        AudioInputStream sourceAIS = null;
        AudioInputStream convert1AIS = null;
        AudioInputStream convert2AIS = null;

        try{
            bais = new ByteArrayInputStream(sourceBytes);
            sourceAIS = AudioSystem.getAudioInputStream(bais);
            AudioFormat sourceFormat = sourceAIS.getFormat();
            AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(), sourceFormat.getChannels()*2, sourceFormat.getSampleRate(), false);
            convert1AIS = AudioSystem.getAudioInputStream(convertFormat, sourceAIS);
            convert2AIS = AudioSystem.getAudioInputStream(audioFormat, convert1AIS);

            baos = new ByteArrayOutputStream();

            byte [] buffer = new byte[8192];
            while(true){
                int readCount = convert2AIS.read(buffer, 0, buffer.length);
                if(readCount == -1){
                    break;
                }
                baos.write(buffer, 0, readCount);
            }
            return baos.toByteArray();
        } catch(UnsupportedAudioFileException uafe){
            //uafe.printStackTrace();
            throw uafe;
        } catch(IOException ioe){
            //ioe.printStackTrace();
            throw ioe;
        } catch(IllegalArgumentException iae){
            //iae.printStackTrace();
            throw iae;
        } catch (Exception e) {
            //e.printStackTrace();
            throw e;
        }finally{
            if(baos != null){
                try{
                    baos.close();
                }catch(Exception e){
                }
            }
            if(convert2AIS != null){
                try{
                    convert2AIS.close();
                }catch(Exception e){
                }
            }
            if(convert1AIS != null){
                try{
                    convert1AIS.close();
                }catch(Exception e){
                }
            }
            if(sourceAIS != null){
                try{
                    sourceAIS.close();
                }catch(Exception e){
                }
            }
            if(bais != null){
                try{
                    bais.close();
                }catch(Exception e){
                }
            }
        }
    }

	protected void setMp3Data(File f) {
//		setWaveData(f);
        try {
//			AudioFileFormat inputFileFormat = AudioSystem.getAudioFileFormat(f);
//        	InputStream inputStream = getClass().getClassLoader().getResourceAsStream("C:\\Users\\as.choi\\AndroidStudioProjects\\ukulele\\tools_n_data\\itsumonandodemo.mp3");
//        	if (inputStream == null) {
//        		System.out.println("\t------------\n\t Error..  inputStream is NULL ----------\n\n");
//        		return ;
//        	}
//        	AudioInputStream ais = AudioSystem.getAudioInputStream(inputStream);
        	AudioInputStream ais = AudioSystem.getAudioInputStream(f);
	        AudioFormat audioFormat = ais.getFormat();

			byte[] Buffer = new byte[(int)f.length()];
			FileInputStream is;
			is = new FileInputStream( f );
			int byteRead = -1;
			byteRead = is.read(Buffer);
			is.close();
			
			byte[] WavBuffer = null;
			try {
				WavBuffer = getAudioDataBytes(Buffer, audioFormat );
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("WAVFILE:"+ WavBuffer[0]+" "+ WavBuffer[1]+" "+ WavBuffer[2]+" "+ WavBuffer[3]+" "+ WavBuffer[4]+" "+ WavBuffer[5]+" "+ WavBuffer[6]+" "+ WavBuffer[7]+" "
					+ WavBuffer[8]+" "+ WavBuffer[9]+" "+ WavBuffer[10]+" "+ WavBuffer[11]+" "+ WavBuffer[12]+" "+ WavBuffer[13]+" "+ WavBuffer[14]+" "+ WavBuffer[15]+" ");

			byte[] rawBuffer;
//			if (num_of_channel==1) {		// mono ä��
//				if (num_bits_of_sample ==8 ) {	// 8bit ����
//					waveSynchPane.setWaveData(Buffer);
//					frmUkeBpmSynchronizer.repaint();		// ���۸� �״�� �׳� �����ص� ��.
//					return;
//				} else {					// 16bit �����̸�, ���� 8��Ʈ�� ó��.
//					rawBuffer = new byte[(byteRead/block_align)];
//					for (int i=0; i<byteRead-block_align; i+= block_align ) {
//						rawBuffer[i/block_align] = (byte) (Buffer[i+1]-128);		//	Buffer[i+1] �� �Ŵ�, 16bit ������ 8��Ʈ ���÷� ó���ϱ� ����. 
//					}
//				}
//			} else {		// stereo ä�� �Ǵ� ��ä��.	// ������ ���⼭�� 16bit �������� �Ǵ��ؾ� ������, �׳� 8bit �� ��쿡�� 1����Ʈ ���غ� ���̹Ƿ� �׳� �Ѿ��.
				int block_align = 4;//((Header[33]&0xFF)<<8)+(Header[32]&0xFF);		// 1�� Sample �� byte ��. (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel
				rawBuffer = new byte[((WavBuffer.length)/block_align)];
				for (int i=0; i<(WavBuffer.length)-block_align; i+= block_align ) {
					rawBuffer[i/block_align] = (byte) (Buffer[i+1]-128);		//	Buffer[i+1] �� �Ŵ�, 16bit ������ 8��Ʈ ���÷� ó���ϱ� ����. 
				}
//			}

			waveSynchPane.setWaveData(rawBuffer);		//WavBuffer);
        } catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
        	AudioFileFormat.Type types[] = AudioSystem.getAudioFileTypes();
    		System.out.println("System supported audio type is ..");
        	for (int i=0; i<types.length; i++) {
        		System.out.println("\t"+types[i]);
        	}
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	protected void setWaveData(File f) {
		byte[] Header = new byte[44];
		byte[] Buffer = new byte[(int)f.length()];

		try {
			FileInputStream is;
			is = new FileInputStream( f );

			int byteRead = -1;
			byteRead = is.read(Header);
//			System.out.println("Header Chunk Size:"+( ((Header[17]&0xFF)<<8)+(Header[16]&0xFF)) );	// = 4����Ʈ ������ Header ũ��� �׸� ũ�� �����Ƿ�, ���� 2����Ʈ��. (little endian)
//			System.out.println("num of Channel:"+( ((Header[23]&0xFF)<<8)+(Header[22]&0xFF)) );	// ä�� �� : 1=Mono, 2=Stereo, 5:4channel. 6:6channel, etc..
//			System.out.println("Sample Rate:"+ ((Header[24]&0xFF)+((Header[25]&0xFF)<<8)) );				//( ((long)(Header[25]&0xFF)<<8)+Header[24]) );	// = 4����Ʈ little endian
//			System.out.println("byte rate =1�ʴ� byte ��:"+( ((Header[31]&0xFF)<<24)+((Header[30]&0xFF)<<16)+((Header[29]&0xFF)<<8)+(Header[28]&0xFF)) );	
//			System.out.println("num bits per Sample :"+( ((Header[35]&0xFF)<<8)+(Header[34]&0xFF)) );	
//			System.out.println("Block Align:"+( ((Header[33]&0xFF)<<8)+(Header[32]&0xFF)) );		// 	

			byteRead = is.read(Buffer);
			is.close();

			if (player != null) {
				System.out.println("need to terminate thread "); 	
				player.stop();	//pause();
				player = null;
			}
			player = new WavPlay(Header, Buffer);
			if (waveSynchPane != null) {
				player.setView(waveSynchPane);
				waveSynchPane.setPlayer(player);
			}
			player.play();
			player.start();

			int num_of_channel = (Header[22]&0xFF)+((Header[23]&0xFF)<<8);
			int num_bits_of_sample = (Header[34]&0xFF)+((Header[35]&0xFF)<<8);
			int SampleRate = (Header[24]&0xFF)+((Header[25]&0xFF)<<8);
			int block_align = ((Header[33]&0xFF)<<8)+(Header[32]&0xFF);		// (= num_bytes_of_sample * num_of_channel )		//   ( num_of_channel * num_bits_of_sample/8 );			// num_of_channel
			System.out.println("sample stripe = "+block_align );	

			byte[] rawBuffer;
			if (num_of_channel==1) {
				if (num_bits_of_sample ==8 ) {
					waveSynchPane.setWaveData(Buffer);
					frmUkeBpmSynchronizer.repaint();
					return;
				} else {
					rawBuffer = new byte[(byteRead/block_align)];
					System.out.println("num_bits_of_sample is not 8 " );
					for (int i=0; i<byteRead-block_align; i+= block_align ) {
						rawBuffer[i/block_align] = (byte) (Buffer[i+1]-128);
					}
				}
			} else {
				rawBuffer = new byte[(byteRead/block_align)];
				System.out.println("Not a mono channel " );
				for (int i=0; i<byteRead-block_align; i+= block_align ) {
					rawBuffer[i/block_align] = (byte) (Buffer[i+1]-128);
				}
			}
			if (waveSynchPane != null) {
				waveSynchPane.setWaveData(rawBuffer);
				System.out.println("setWaveData anyway" );
				frmUkeBpmSynchronizer.repaint();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public File showFileDialog() {
		final JFileChooser fc = new JFileChooser("C:\\\\Users\\\\as.choi\\\\eclipse-workspace\\\\BpmSynchorProject\\\\src\\\\resource");
		fc.showOpenDialog(null);
		return  fc.getSelectedFile();
	}

	private void makeNew() {
		// 초기화 할 항목들 (중복 제거)
		data = new UkeData();
		player = null;
		imgAlbumImage.setIcon(new ImageIcon("C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\ukulele_icon.png"));
		setAlbumImage(imgAlbumImage, ".\\src\\resource\\ukulele_icon.png");

		if (waveSynchPane != null) {
			waveSynchPane.setUkeData(data);
			waveSynchPane.setWaveData(null);
		}
	}

	private void setAlbumImage(JLabel imageLabel, String filePath ) {
		ImageIcon loadedIcon = new ImageIcon( filePath );
		int w = loadedIcon.getIconWidth();
		int h = loadedIcon.getIconHeight();
		if (w>h) {
			h = ICON_SIZE *h / w;		w = ICON_SIZE ;
		} else {
			w = ICON_SIZE *w / h;		h = ICON_SIZE ;
		}
		ImageIcon scaledIcon = new ImageIcon(loadedIcon.getImage().getScaledInstance( w, h, Image.SCALE_DEFAULT));
		imageLabel.setIcon(scaledIcon);
	}

	private void setStuffFromData(UkeData ukedata) {
		tfSongTitle.setText(ukedata.getSongTitle() );
		tfComment.setText(ukedata.getComment() );
		spnrBpm.setValue(Float.valueOf(ukedata.mBpm));
		spnrOffset.setValue(Float.valueOf(ukedata.mStartOffset));
		waveSynchPane.setWaveOffset(ukedata.mStartOffset);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("KeyTyped : "+ e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.err.println("KeyPressed : "+ e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
