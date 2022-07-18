package myproject;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;

public class WindowFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4487049321844340049L;
	private JPanel contentPane;
	private JTextField tfSongTitle;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WindowFrame frame = new WindowFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WindowFrame() {
		setTitle("BPMSynchorMain");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 994, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblSongTitle = new JLabel("Song Title");
		
		JButton btnNewFile = new JButton("New File");
		
		JButton btnOpenFile = new JButton("Open...");
		
		JButton btnSetMP3 = new JButton("Set MP3");
		
		JButton btnSetAlbumImage = new JButton("Set Album Image");
		
		JButton btnWriteFile = new JButton("Write File");
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setIcon(new ImageIcon("C:\\Users\\as.choi\\eclipse-workspace\\BpmSynchorProject\\src\\resource\\ukulele_icon.png"));
		
		tfSongTitle = new JTextField();
		tfSongTitle.setColumns(10);
		
		JLabel lblMp3 = new JLabel("MP3:");
		lblMp3.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblMP3FileName = new JLabel("no mp3 file is set.");
		
		JLabel lblQuaver = new JLabel("BaseNote");
		
		JRadioButton rdbtnQuaver = new JRadioButton("quaver(\u266A 8\uBD84\uC74C\uD45C)");
		buttonGroup.add(rdbtnQuaver);
		
		JRadioButton rdbtnSemiQuaver = new JRadioButton("semi-quaver(\u266C 16\uBD84\uC74C\uD45C)");
		buttonGroup.add(rdbtnSemiQuaver);
		
		JLabel lblBeat = new JLabel("Beat");
		
		JComboBox cbBeats = new JComboBox();
		cbBeats.setModel(new DefaultComboBoxModel(new String[] {"2/4", "3/4", "4/4", "6/8"}));
		
		JLabel lblBPM = new JLabel("BPM");
		
		JSpinner spnBpmSpinner = new JSpinner();
		
		JLabel lblCategory = new JLabel("Category");
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Stroke (rhythm)", "Melody", "Finger style", ""}));
		
		JLabel lblLevel = new JLabel("Level");
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"\u25CB\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CB\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CB\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CB\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CB\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CB", "\u25CF\u25CF\u25CF\u25CF\u25CF\u25CF"}));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnNewFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnOpenFile)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSetMP3)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSetAlbumImage))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSongTitle)
								.addComponent(lblQuaver))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(rdbtnQuaver)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnSemiQuaver)
									.addGap(18)
									.addComponent(lblBeat)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(cbBeats, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblBPM)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(spnBpmSpinner, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
								.addComponent(tfSongTitle, GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(58)
							.addComponent(lblMp3)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblMP3FileName, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(18)
							.addComponent(lblCategory)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(16)
							.addComponent(lblLevel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnWriteFile))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblNewLabel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnNewFile)
								.addComponent(btnOpenFile)
								.addComponent(btnSetMP3)
								.addComponent(btnSetAlbumImage))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSongTitle)
								.addComponent(tfSongTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMP3FileName)
								.addComponent(lblMp3))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblQuaver)
								.addComponent(rdbtnQuaver)
								.addComponent(rdbtnSemiQuaver)
								.addComponent(lblBeat)
								.addComponent(cbBeats, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblBPM)
								.addComponent(spnBpmSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblCategory)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblLevel)
								.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
						.addComponent(btnWriteFile, Alignment.LEADING))
					.addContainerGap(179, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
