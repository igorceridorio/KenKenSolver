/*-----------------------------------------------------------
 * ARCHIVE: KenKenDriver.java
 * 
 * OBJECTIVE: 
 * This class creates the graphical window of the application.
 * It has the declaration of the components and the organization
 * of the layout. 	    
 *---------------------------------------------------------*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class KenKenDriver extends JFrame {
	
	KenKenDisplay kkDisplay;
	KenKenPuzzle kkPuzzle = new KenKenPuzzle();
	
	//boolean procConsistency = false;
	
	// consistency flags
	boolean nodeConsistency = false;
	boolean arcConsistency = false;
	boolean twoConsistency = false;
	boolean threeConsistency = false;
	boolean triggerThreeConsistency = false;
	boolean singleDomainConsistency = false;
	
	// search flag
	boolean search = false;
	boolean triggerSearch = false;
	
	int row = 0;
	int col = 0;
	
	int WIN_WID = 1100;
	int WIN_HEI = 825;
	
	public JLabel lblBlank = new JLabel(" ");
	public JLabel lblPuzzleInput = new JLabel("Puzzle input:");
	public JLabel lblPuzzleSolver = new JLabel("Puzzle solver:");
	public JLabel lblSearchSpaceTitle = new JLabel("Search space:");
	public JLabel lblLogMesssageTitle = new JLabel("Log message:");
	public JLabel lblSearchSpace= new JLabel(" ");
	public JLabel lblLogMesssage = new JLabel(" ");
	public JButton btnSolveStep = new JButton("Solve step");
	public JButton btnPuzzleInput = new JButton("Select a file");
	public JButton btnPerformSearch = new JButton("Perform search");
	
	/* ----------------------------------------------------
	 * class constructor
	 * -------------------------------------------------- */
	public KenKenDriver() {
	
		// define the window configuration
		super("KenKen Puzzle Solver!");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(WIN_WID, WIN_HEI);
		this.setLocationRelativeTo(null);
		kkDisplay = new KenKenDisplay(WIN_WID, WIN_HEI, kkPuzzle);
		add(kkDisplay, BorderLayout.CENTER);
		
		// defining the window interface
		
		// define the top pane
		JPanel top = new JPanel();
		top.setOpaque(true);
		top.setBackground(Color.WHITE);
		top.setPreferredSize(new Dimension(50, 50));
		add(top, BorderLayout.NORTH);
		
		GroupLayout layoutTop = new GroupLayout(top);
		top.setLayout(layoutTop);
		layoutTop.setAutoCreateGaps(true);
		layoutTop.setAutoCreateContainerGaps(true);
		
		// create a sequential group for the horizontal axis
		GroupLayout.SequentialGroup hGroupTop = layoutTop.createSequentialGroup();
		
		// create a sequential group for the vertical axis
		GroupLayout.SequentialGroup vGroupTop = layoutTop.createSequentialGroup();
		
		// add the components to the group
		hGroupTop.addGroup(layoutTop.createParallelGroup()
				.addComponent(lblSearchSpaceTitle));
		hGroupTop.addGroup(layoutTop.createParallelGroup()
				.addComponent(lblSearchSpace));
		layoutTop.setHorizontalGroup(hGroupTop);
		
		// align the contents of the group along the baseline
		vGroupTop.addGroup(layoutTop.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblSearchSpaceTitle).addComponent(lblSearchSpace));
		layoutTop.setVerticalGroup(vGroupTop);
		
		lblSearchSpace.setForeground(Color.RED);
		
		// --------------------------------------------------------------------
		
		// define the side pane and group layout
		JPanel panel = new JPanel();
		panel.setOpaque(true);
		panel.setBackground(Color.WHITE);
		panel.setPreferredSize(new Dimension(300, 300));
		add(panel, BorderLayout.EAST);
		
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// create a sequential group for the horizontal axis
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		// create a sequential group for the vertical axis
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		// add the components to the group
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(lblPuzzleInput)
				.addComponent(btnPuzzleInput).addComponent(lblBlank)
				.addComponent(lblPuzzleSolver).addComponent(btnSolveStep)
				.addComponent(btnPerformSearch).addComponent(lblBlank)
				.addComponent(lblLogMesssageTitle).addComponent(lblLogMesssage));
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(lblBlank)
				.addComponent(lblBlank).addComponent(lblBlank)
				.addComponent(lblBlank).addComponent(lblBlank)
				.addComponent(lblBlank).addComponent(lblBlank)
				.addComponent(lblBlank).addComponent(lblBlank));
		layout.setHorizontalGroup(hGroup);
		
		// align the contents of the group along the baseline
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblPuzzleInput).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(btnPuzzleInput).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblBlank).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblPuzzleSolver).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(btnSolveStep).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(btnPerformSearch).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblBlank).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblLogMesssageTitle).addComponent(lblBlank));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(lblLogMesssage).addComponent(lblBlank));
		layout.setVerticalGroup(vGroup);
		
		// get the file selected by the user
		btnPuzzleInput.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fcPuzzleInput = new JFileChooser();
				int returnValue = fcPuzzleInput.showOpenDialog(null);
				if(returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fcPuzzleInput.getSelectedFile();
					// set the filePath for this puzzle
					kkPuzzle.filePath = selectedFile.getAbsolutePath();
					// call the method to load the file given by the user
					kkPuzzle.loadKenKenPuzzle();
					repaint();
					// reset the log message
					lblLogMesssage.setText("");
					// calculate the initial search space
					kkPuzzle.calcSearchSpace();
					lblSearchSpace.setText(String.valueOf(kkPuzzle.searchSpace));
				}
				
				// resets the search boolean value
				search = false;
				triggerSearch = false;
				
				// resets the consistency flags
				nodeConsistency = false;
				arcConsistency = false;
				twoConsistency = false;
				threeConsistency = false;
				triggerThreeConsistency = false;
				singleDomainConsistency = false;
				
				// resets the buttons
				btnSolveStep.setEnabled(true);
				btnPerformSearch.setEnabled(true);
				
			}
		});
		
		// solves the problem directly by search
		btnPerformSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(kkPuzzle.n > 0 && !kkPuzzle.solvedKenKen()) {
				
					// disables the solve by step button
					btnSolveStep.setEnabled(false);
					
					lblLogMesssage.setForeground(Color.BLUE);
					lblLogMesssage.setText("Executing search");
					search = kkPuzzle.searchKenKenPuzzle();
					
					// repaints the KenKen board
					repaint();
					
				} else if (kkPuzzle.n == 0){
					JOptionPane.showMessageDialog(new JFrame(), "You must select a KenKen file first", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
		});
		
		// calls the solve step for the current KenKenPuzzle
		btnSolveStep.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(kkPuzzle.n > 0 && !kkPuzzle.solvedKenKen()) {
				
					// disables the perform search button
					btnPerformSearch.setEnabled(false);
					
					singleDomainConsistency = kkPuzzle.applySingleDomainKenKenPuzzle();
					if(singleDomainConsistency) {
						lblLogMesssage.setForeground(Color.BLUE);
						lblLogMesssage.setText("Applying one value domains");
					}
					if(!nodeConsistency) {
						kkPuzzle.nodeConsistencyKenKenPuzzle();
						lblLogMesssage.setForeground(Color.BLUE);
						lblLogMesssage.setText("Applying node consistency");
						nodeConsistency = true;
					} else {
						
						if(triggerSearch) {
							lblLogMesssage.setForeground(Color.BLUE);
							lblLogMesssage.setText("Executing search");
							search = kkPuzzle.searchKenKenPuzzle();
						}
						
						if(triggerThreeConsistency && !singleDomainConsistency && !triggerSearch) {
							threeConsistency = kkPuzzle.threeConsistencyKenKenPuzzle();
							
							if(threeConsistency) {
								lblLogMesssage.setForeground(Color.BLUE);
								lblLogMesssage.setText("Applying three consistency");
								triggerSearch = true;
							} else {
								lblLogMesssage.setForeground(Color.RED);
								lblLogMesssage.setText("No three consistency to apply");
								triggerSearch = true;
							}
						}
						
						twoConsistency = kkPuzzle.twoConsistencyKenKenPuzzle();
						if(twoConsistency && !singleDomainConsistency && !triggerSearch) {
							lblLogMesssage.setForeground(Color.BLUE);
							lblLogMesssage.setText("Applying two consistency");
							triggerThreeConsistency = true;
						} else {
							triggerThreeConsistency = true;
						}
						
					}
					
					// recalculate the initial search space
					kkPuzzle.calcSearchSpace();
					lblSearchSpace.setText(String.valueOf(kkPuzzle.searchSpace));
					
					// repaints the KenKen board
					repaint();
					
				} else if (kkPuzzle.n == 0){
					JOptionPane.showMessageDialog(new JFrame(), "You must select a KenKen file first", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// set the window as visible and focusable
		this.setVisible(true);
		this.setFocusable(true);
		
		// disable the resize for the window
		this.setResizable(false);
		
	}
	
	/* ----------------------------------------------------
	 * main method
	 * -------------------------------------------------- */
	public static void main(String[] args) {
		new KenKenDriver();
	}
	
}
