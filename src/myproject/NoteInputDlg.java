package myproject;

import java.awt.Dialog;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

public class NoteInputDlg extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfLyric;
	private JTextField tf_E;
	private JTextField tf_A;
	private JTextField tf_C;
	private JTextField tc_G;
	private JTextField tfTechnics;

	public NoteInputDlg(Dialog owner, String title) {
		setTitle("연주음 입력");
		
		JLabel lblTimeStamp = new JLabel("timeStamp");
		
		JSpinner spinner = new JSpinner();
		
		JLabel lbllyric = new JLabel("가사");
		
		tfLyric = new JTextField();
		tfLyric.setColumns(10);
		
		JLabel lblChord = new JLabel("코드");
		
		JComboBox cbChords = new JComboBox();
		
		JLabel lbl_E = new JLabel("     E");
		lbl_E.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblMsec = new JLabel("msec");
		
		tf_E = new JTextField();
		tf_E.setColumns(10);
		
		JLabel lbl_A = new JLabel("    A");
		lbl_A.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tf_A = new JTextField();
		tf_A.setColumns(10);
		
		JLabel lbl_C = new JLabel("    C");
		lbl_C.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tf_C = new JTextField();
		tf_C.setColumns(10);
		
		JLabel lbl_G = new JLabel("    G");
		lbl_G.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tc_G = new JTextField();
		tc_G.setColumns(10);
		
		JLabel lblTechnics = new JLabel("technics");
		
		tfTechnics = new JTextField();
		tfTechnics.setColumns(10);
		
		JLabel lblSound_E = new JLabel("New label");
		
		JLabel lblSound_A = new JLabel("New label");
		
		JLabel lblSound_C = new JLabel("New label");
		
		JLabel lblSound_G = new JLabel("New label");
		
		JButton btnOK = new JButton("입력 확인");
		
		JButton btnCancel = new JButton("편집취소");
		
		JCheckBox chkDelete = new JCheckBox("이 음을 삭제");
		
		JButton btnChordAuto = new JButton("자동음지정");
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lbl_A)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tf_A, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
										.addComponent(tc_G, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lbl_E, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tf_E, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(lbllyric)
										.addComponent(lblChord))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(cbChords, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tfLyric))))
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblTimeStamp)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSound_C)
								.addComponent(lblSound_A)
								.addComponent(lblSound_G)
								.addComponent(lblSound_E)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblMsec, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(chkDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
						.addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbllyric)
						.addComponent(tfLyric, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cbChords, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblChord)
						.addComponent(btnChordAuto))
					.addGap(8)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tf_E, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl_E)
						.addComponent(lblSound_E))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lbl_A)
						.addComponent(lblSound_A)
						.addComponent(tf_A, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tf_C, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl_C)
						.addComponent(lblSound_C))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tc_G, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl_G)
						.addComponent(lblSound_G))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTechnics)
						.addComponent(tfTechnics, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
					.addComponent(chkDelete)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnOK)
						.addComponent(btnCancel))
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
//		super(owner, title);
	}
}
