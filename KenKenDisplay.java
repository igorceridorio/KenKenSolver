/*-----------------------------------------------------------
 * ARCHIVE: KenKenDisplay.java
 * 
 * OBJECTIVE: 
 * This class contains the implementation for the paint method.
 * It is responsible for repaint the KenKen problem instance 
 * each time it goes under some modification. 	    
 *---------------------------------------------------------*/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class KenKenDisplay extends JPanel{
	
	public KenKenPuzzle kkPuzzle;
	
	/* ----------------------------------------------------
	 * class constructor
	 * -------------------------------------------------- */
	public KenKenDisplay(int WIN_WID, int WIN_HEI, KenKenPuzzle kkPuzzle) {
		this.setSize(WIN_WID, WIN_HEI);
		this.kkPuzzle = kkPuzzle;
	}

	/* ----------------------------------------------------
	 * method responsible for painting the KenKen instance
	 * -------------------------------------------------- */
	public void paint(Graphics g) {
		
		super.paint(g);
		
		// painting the background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(kkPuzzle.n > 0) {
			int side = (this.getWidth() - 60) / kkPuzzle.n;
			g.setColor(Color.BLACK);
			
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			
			// drawing the squares
			for(int i = 0; i < kkPuzzle.n; i++) {
				for(int j = 0; j < kkPuzzle.n; j++) {
					g2.drawRect(i * side + 20, j * side, side, side);
				}
			}
			
			// drawing the labels
			g.setFont(new Font("TimesRoman", Font.PLAIN, 22)); 
			
			int offsetXLbl = 30;
			int offsetYLbl = 25;
			
			for(int i = 0; i < kkPuzzle.kkConstraints.size(); i++) {
				String label = kkPuzzle.kkConstraints.get(i).opSign + Integer.toString(kkPuzzle.kkConstraints.get(i).opResult);
				g.drawString(label, kkPuzzle.kkConstraints.get(i).kkVariablePositions.get(0).row * side + offsetXLbl,
						kkPuzzle.kkConstraints.get(i).kkVariablePositions.get(0).col * side + offsetYLbl);
			}
			
			// drawing the division lines
			g.setColor(Color.LIGHT_GRAY);
			
			for(int i = 0; i < kkPuzzle.n - 1 ; i++) {
				for(int j = 0; j < kkPuzzle.n; j++) {
					// row equality
					if((kkPuzzle.kkMatrix[i][j].opSign == kkPuzzle.kkMatrix[i+1][j].opSign) && (kkPuzzle.kkMatrix[i][j].opResult == kkPuzzle.kkMatrix[i+1][j].opResult) && (kkPuzzle.kkMatrix[i][j].consIdx == kkPuzzle.kkMatrix[i+1][j].consIdx)) {
						g.drawLine((i + 1) * side + 20, j * side + 3, (i + 1) * side + 20, j * side + side - 3);
					}
				}
			}
			
			for(int i = 0; i < kkPuzzle.n; i++) {
				for(int j = 0; j < kkPuzzle.n - 1; j++) {
					// column equality
					if((kkPuzzle.kkMatrix[i][j].opSign == kkPuzzle.kkMatrix[i][j+1].opSign) && (kkPuzzle.kkMatrix[i][j].opResult == kkPuzzle.kkMatrix[i][j+1].opResult) && (kkPuzzle.kkMatrix[i][j].consIdx == kkPuzzle.kkMatrix[i][j+1].consIdx)) {
						g.drawLine(i * side + 20 + 3, (j + 1) * side, i * side + side + 20 - 3, (j + 1) * side);
					}
				}
			}
			
			
			// drawing the assigned variables
			g.setColor(Color.BLACK);
			g.setFont(new Font("TimesRoman", Font.PLAIN, (500/kkPuzzle.n))); 
			
			for(int i = 0; i < kkPuzzle.n; i++) {
				for(int j = 0; j < kkPuzzle.n; j++) {
					if(kkPuzzle.kkMatrix[i][j].value != 0) {
						g.drawString(Integer.toString(kkPuzzle.kkMatrix[i][j].value), i * side + (side/3) + 20, j * side + (side - side/4));
					}
				}
			}
			
		}
		
	}
}
