package myproject;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import net.miginfocom.swing.MigLayout;

public class BpmSynchorWindow {

	private JFrame frmUkeBpmSynchronizer;
	private JTextField tfComment;
	private JTextField textField_1;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JTextField tfJumpTo;

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
		frmUkeBpmSynchronizer.setBounds(100, 100, 935, 499);
		frmUkeBpmSynchronizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panelFileManager = new JPanel();
		panelFileManager.setBorder(new BevelBorder(BevelBorder.RAISED));
		frmUkeBpmSynchronizer.getContentPane().add(panelFileManager, BorderLayout.NORTH);
		
		JButton btnNewFile = new JButton("New File");
		JButton btnOpenFile = new JButton("Open File..");
		JButton btnSetMp3 = new JButton("Set MP3");
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
					.addComponent(btnSetMp3)
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
					.addComponent(btnSetMp3))
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
		
		JPanel panel_2 = new JPanel();
		frmUkeBpmSynchronizer.getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panelComments = new JPanel();
		panel_2.add(panelComments, BorderLayout.SOUTH);
		
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
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel_2.add(panel_4, BorderLayout.NORTH);
		
		JLabel lblSongTitle = new JLabel("Song Title");
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		
		JLabel lblMp3FileName = new JLabel("MP3 File:");
		
		JLabel lblNewLabel_5 = new JLabel("Use [Set MP3] button.");
		
		JLabel lblAlbumImage = new JLabel("Album Image:");
		lblAlbumImage.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel imgAlbumImage = new JLabel("");
		imgAlbumImage.setHorizontalAlignment(SwingConstants.CENTER);
		imgAlbumImage.setIcon(new ImageIcon("C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\ukulele_icon.png"));
		
		JLabel lblNewLabel_2 = new JLabel("Unit Note:");
		
		JRadioButton rdbtnQuaver = new JRadioButton("quaver (\u266A 8\uBD84\uC74C\uD45C)");
		buttonGroup.add(rdbtnQuaver);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("seni-quaver (\u266C 16\uBD84\uC74C\uD45C)");
		buttonGroup.add(rdbtnNewRadioButton_1);
		
		JLabel lblBeat = new JLabel("Beats:");
		lblBeat.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"2/4", "3/4", "4/4", "6/8"}));
		
		JLabel lblBpm = new JLabel("BPM:");
		lblBpm.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JSpinner spnrBpm = new JSpinner();
		spnrBpm.setModel(new SpinnerNumberModel(80, 20, 280, 1));
		
		JLabel lblCategory = new JLabel("Category:");
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Stroke (Rhythm)", "Melody", "FingerStyle"}));
		
		JLabel lblLevel = new JLabel("Level:");
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF"}));
		
		JButton btnToStart = new JButton("Move to Start");
		
		JButton btnPlay = new JButton("Play");
		
		JButton btnPause = new JButton("Pause");
		
		JLabel lblJumpTo = new JLabel("Jump to");
		
		tfJumpTo = new JTextField();
		tfJumpTo.setColumns(10);
		GroupLayout gl_panel_4 = new GroupLayout(panel_4);
		gl_panel_4.setHorizontalGroup(
			gl_panel_4.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addComponent(btnToStart)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPlay)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPause)
							.addGap(18)
							.addComponent(lblJumpTo)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfJumpTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_4.createSequentialGroup()
									.addComponent(lblBeat, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(21)
									.addComponent(lblBpm, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spnrBpm, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel_4.createSequentialGroup()
									.addComponent(lblSongTitle)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
								.addGroup(gl_panel_4.createSequentialGroup()
									.addComponent(lblNewLabel_2)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnQuaver)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnNewRadioButton_1)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel_4.createSequentialGroup()
									.addComponent(lblCategory)
									.addGap(4)
									.addGroup(gl_panel_4.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_panel_4.createSequentialGroup()
											.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addComponent(lblLevel)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(3))
										.addGroup(gl_panel_4.createSequentialGroup()
											.addComponent(lblMp3FileName)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lblNewLabel_5, GroupLayout.PREFERRED_SIZE, 168, GroupLayout.PREFERRED_SIZE))))
								.addComponent(lblAlbumImage, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addComponent(imgAlbumImage)
							.addGap(12)))
					.addGap(0))
		);
		gl_panel_4.setVerticalGroup(
			gl_panel_4.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_4.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
						.addComponent(imgAlbumImage)
						.addGroup(gl_panel_4.createSequentialGroup()
							.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
								.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblSongTitle)
								.addComponent(lblAlbumImage))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_2)
								.addComponent(rdbtnQuaver)
								.addComponent(rdbtnNewRadioButton_1)
								.addComponent(lblNewLabel_5)
								.addComponent(lblMp3FileName))
							.addPreferredGap(ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblBeat)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblBpm)
								.addComponent(spnrBpm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCategory)
								.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblLevel)
								.addComponent(comboBox_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnToStart)
								.addComponent(btnPlay)
								.addComponent(btnPause)
								.addComponent(lblJumpTo)
								.addComponent(tfJumpTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		panel_4.setLayout(gl_panel_4);
		
		JPanel panelWaveDraw = new JPanel();
		panel_2.add(panelWaveDraw, BorderLayout.CENTER);
	}

}
