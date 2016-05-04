/*-----------------------------------------------------------
 * ARCHIVE: KenKenPuzzle.java
 * 
 * OBJECTIVE:
 * This class is responsible for solving a KenKen problem
 * instance.It is responsible for reading a puzzle 
 * from a file and solving the node, 2 and 3 arc consistency 
 * for the problem. It also implements backtracking search
 * to solve the problem after all the consistencies have been
 * applied.
 *---------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class KenKenPuzzle {

	public String filePath;
	public KenKenVariable[][] kkMatrix;
	public ArrayList<KenKenConstraint> kkConstraints = new ArrayList<KenKenConstraint>();
	public int n;
	public BigInteger searchSpace;
	
	/* ----------------------------------------------------
	 * class constructor
	 * -------------------------------------------------- */
	public KenKenPuzzle() {
		this.filePath = "";
		this.n = 0;
	}
	
	/* ----------------------------------------------------
	 * method responsible for loading a puzzle from a file
	 * -------------------------------------------------- */
	public void loadKenKenPuzzle() {

		this.kkConstraints.clear();
		BufferedReader br = null;
		
		try {
			String sLine;
			br = new BufferedReader(new FileReader(filePath));
			
			// getting the n value for the problem and initializing the matrix
			sLine = br.readLine();
			StringTokenizer st = new StringTokenizer(sLine);
			this.n = Integer.parseInt(st.nextToken());
			
			// initializing the matrix
			kkMatrix = new KenKenVariable[this.n][this.n];
			for(int i = 0; i < this.n; i++) {
				for(int j = 0; j < this.n; j++) {
					kkMatrix[i][j] = new KenKenVariable(this.n);
				}
			}
			
			while((sLine = br.readLine()) != null) {
				
				KenKenConstraint kkConstraint = new KenKenConstraint();
				List<Integer> rowsCols = new ArrayList<Integer>();
				char opSign = '\0';
				String opResult = "";
				ArrayList<KenKenVariablePosition> arrayKkvp = new ArrayList<KenKenVariablePosition>();
				
				char[] cLine = sLine.toCharArray();
				
				// obtaining all the data from the line
				for(int i = 0; i < cLine.length; i++) {
					if((cLine[i] != ':') && (cLine[i] != ' ')) {
						rowsCols.add(Character.getNumericValue(cLine[i]));
					} else if(cLine[i] == ':') {
						for(int j = i++; j < cLine.length; j++) {
							if(cLine[j] >= '0' && cLine[j] <= '9')
								opResult += cLine[j];
							else
								opSign = cLine[j];
							i = j;
						}
					}
				}
				
				// filling up the variables with the retrieved data 
				kkConstraint.opSign = Character.toLowerCase(opSign);
				kkConstraint.opResult = Integer.parseInt(opResult);
				
				for(int i = 0; i < rowsCols.size(); i++) {
					KenKenVariablePosition kkvp = new KenKenVariablePosition();
					kkvp.col = rowsCols.get(i);
					i++;
					kkvp.row = rowsCols.get(i);
					arrayKkvp.add(kkvp);
				}
				kkConstraint.kkVariablePositions = arrayKkvp;
				
				// adding the constraint to the puzzle
				kkConstraints.add(kkConstraint);
				
			}
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(new JFrame(), e, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		// sort the constraints by the number of variables involved
		Collections.sort(kkConstraints, new KenKenConstraintComparator());
		
		// adding the constraint information to each variable
		for(int i =0; i < kkConstraints.size(); i++) {
			for(int j = 0; j < kkConstraints.get(i).kkVariablePositions.size(); j++) {
				kkMatrix[kkConstraints.get(i).kkVariablePositions.get(j).row][kkConstraints.get(i).kkVariablePositions.get(j).col].opSign = kkConstraints.get(i).opSign;
				kkMatrix[kkConstraints.get(i).kkVariablePositions.get(j).row][kkConstraints.get(i).kkVariablePositions.get(j).col].opResult = kkConstraints.get(i).opResult;
				kkMatrix[kkConstraints.get(i).kkVariablePositions.get(j).row][kkConstraints.get(i).kkVariablePositions.get(j).col].consIdx = i;
			}
		}
		
		// calling the method to print this instance of KenKenPuzzle
		printKenKenPuzzle();
		
	}
	
	/* ----------------------------------------------------
	 * method used to print a KenKenPuzzle in the console 
	 * for debugging purposes
	 * -------------------------------------------------- */
	public void printKenKenPuzzle() {
		
		System.out.println("KenKenPuzzle Instance");
		System.out.println("Number of n: " + this.n);
		System.out.println("Constraints: ");
		
		for(int i = 0; i < this.kkConstraints.size(); i++){
			System.out.println("\tOp sign: " + this.kkConstraints.get(i).opSign);
			System.out.println("\tOp result: " + this.kkConstraints.get(i).opResult);
			System.out.print("\tVariables: ");
			for(int j = 0; j < this.kkConstraints.get(i).kkVariablePositions.size(); j++) {
				System.out.print(this.kkConstraints.get(i).kkVariablePositions.get(j).row + " " + this.kkConstraints.get(i).kkVariablePositions.get(j).col + " ");
			}
			System.out.println("");
		}
		
		System.out.println("");
		
	}

	/* ----------------------------------------------------
	 * method used to apply node consistency to 
	 * the KenKenPuzzle instance
	 * -------------------------------------------------- */
	public boolean nodeConsistencyKenKenPuzzle() {
		
		boolean foundNode = false;
		
		// searches and apply the initial unary constraints
		for(int i = 0; i < kkConstraints.size(); i++) {
			if((kkConstraints.get(i).kkVariablePositions.size() == 1) && (!kkConstraints.get(i).wasProcessed)) {
				int rowValue = kkConstraints.get(i).kkVariablePositions.get(0).row;
				int colValue = kkConstraints.get(i).kkVariablePositions.get(0).col;
				
				kkConstraints.get(i).wasProcessed = true;
				kkMatrix[rowValue][colValue].value = kkConstraints.get(i).opResult;
				
				// removes all the same number for all the variables domains of this row and column
				for(int j = 0; j < this.n; j++) {
					if(j != colValue)
						kkMatrix[rowValue][j].kkVariableDomain.remove(new Integer(kkConstraints.get(i).opResult));
					kkMatrix[j][colValue].kkVariableDomain.remove(new Integer(kkConstraints.get(i).opResult));
				}
				
				// cleans the domain of this variable
				kkMatrix[rowValue][colValue].kkVariableDomain.clear();
				kkMatrix[rowValue][colValue].kkVariableDomain.add(kkConstraints.get(i).opResult);
				
				foundNode = true;
				
			}
		}
		
		return foundNode;
	}
	
	/* ----------------------------------------------------
	 * method used to apply values to variables when there
	 * is just one number available at a domain
	 * -------------------------------------------------- */
	public boolean applySingleDomainKenKenPuzzle() {
		
		// if any domain contains just one number, applies it
		for(int i = 0; i < this.n; i++) {
			for(int j = 0; j < this.n; j++) {
				
				if((kkMatrix[i][j].kkVariableDomain.size() == 1) && kkMatrix[i][j].value == 0) {
					kkMatrix[i][j].value = kkMatrix[i][j].kkVariableDomain.get(0);
					int value = kkMatrix[i][j].value;
					
					// calls the method to clean line and row
					rowColConsistency(i, j, value);
					
					kkMatrix[i][j].kkVariableDomain.add(value);
					return true;
					
				}
			}
		}
		
		return false;
	}
	
	/* ----------------------------------------------------
	 * method used to apply 2-arc consistency to the 
	 * KenKenPuzzle instance 
	 * -------------------------------------------------- */
	public boolean twoConsistencyKenKenPuzzle() {
		
		boolean twoConsistency = false;
		
		for(int i = 0; i < kkConstraints.size(); i++) {
			if((!kkConstraints.get(i).wasProcessed) && (kkConstraints.get(i).kkVariablePositions.size() == 2)) {
				
				// SUBTRACTION (-) and DIVISION (/) are always composed by only 2 variables
				if(kkConstraints.get(i).opSign == '-' || kkConstraints.get(i).opSign == '/') {
					//if(subDivConsistency(i))
						//return true;
					subDivConsistency(i);
				}
				
				// SUM (+) case
				if(kkConstraints.get(i).opSign == '+') {
					twoSumConsistency(i);
				}
				
				// MULTIPLICATION (x) case
				if(kkConstraints.get(i).opSign == 'x') {
					twoMulConsistency(i);
				}
				
				twoConsistency = true;
				
			}
			
		}
		
		return twoConsistency;
	}
	
	/* ----------------------------------------------------
	 * method used to apply 3-arc consistency to the 
	 * KenKenPuzzle instance 
	 * -------------------------------------------------- */
	public boolean threeConsistencyKenKenPuzzle() {
		
		boolean threeConsistency = false;
		
		for(int i = 0; i < kkConstraints.size(); i++) {
			if((!kkConstraints.get(i).wasProcessed) && (kkConstraints.get(i).kkVariablePositions.size() == 3)) {
				
				// SUM (+) case
				if(kkConstraints.get(i).opSign == '+') {
					threeSumConsistency(i);
				}
				
				// MULTIPLICATION (x) case
				if(kkConstraints.get(i).opSign == 'x') {
					threeMulConsistency(i);
				}
				
				threeConsistency = true;
				
			}
			
		}
		
		return threeConsistency;
		
	}
	
	/* ----------------------------------------------------
	 * method used to apply consistency to subtraction and
	 * division cases
	 * -------------------------------------------------- */
	public void subDivConsistency(int i) {
		
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		int varOneRow = kkConstraints.get(i).kkVariablePositions.get(0).row;
		int varOneCol = kkConstraints.get(i).kkVariablePositions.get(0).col;
		int varTwoRow = kkConstraints.get(i).kkVariablePositions.get(1).row;
		int varTwoCol = kkConstraints.get(i).kkVariablePositions.get(1).col;
		
		//for each pair of values verifies if the expression can be true
		// a - b || a / b
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			for(int k = 0; k < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); k++) {
				
				// subtraction case
				if(kkConstraints.get(i).opSign == '-') {
					if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j) - 
							kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k) == kkConstraints.get(i).opResult) {
						if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j))))
							possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j));
						if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k))))
							possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k));
					}
				}
				
				// division case
				if(kkConstraints.get(i).opSign == '/') {
					if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j) / 
							kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k) == kkConstraints.get(i).opResult) {
						if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j))))
							possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j));
						if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k))))
							possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k));
					}
				}
				
			}
		}
		
		// b - a || b / a
		for(int j = 0; j < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); j++) {
			for(int k = 0; k < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); k++) {
				
				// subtraction case
				if(kkConstraints.get(i).opSign == '-') {						
					if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j) - 
							kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(k) == kkConstraints.get(i).opResult) {
						if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j))))
							possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j));
						if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(k))))
							possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(k));
					}
				}
				
				// division case
				if(kkConstraints.get(i).opSign == '/') {
					if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j) / 
							kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(k) == kkConstraints.get(i).opResult) {
						if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j))))
							possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j));
						if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(k))))
							possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(k));
					}
				}
				
			}
		}
	
		// now remove the values that are not possible for the variables in this constraint
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)))
				kkMatrix[varOneRow][varOneCol].kkVariableDomain.remove(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)));
		}
		for(int j = 0; j < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)))
				kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.remove(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)));
		}
		
		// clean the possible values array
		possibleValues.clear();
		
		kkConstraints.get(i).wasProcessed = true;
		
		// if any domain contains just one value it is assigned to the variable
		if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.size() == 1) {
			kkMatrix[varOneRow][varOneCol].value = kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(0);
			int value = kkMatrix[varOneRow][varOneCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varOneRow, varOneCol, value);
			
			kkMatrix[varOneRow][varOneCol].kkVariableDomain.add(value);
			
		}
		if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size() == 1) {
			kkMatrix[varTwoRow][varTwoCol].value = kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(0);
			int value = kkMatrix[varTwoRow][varTwoCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varTwoRow, varTwoCol, value);
			
			kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.add(value);						
		}
	}
	
	/* ----------------------------------------------------
	 * method used to apply two consistency to sum case
	 * -------------------------------------------------- */
	public void twoSumConsistency(int i) {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		int varOneRow = kkConstraints.get(i).kkVariablePositions.get(0).row;
		int varOneCol = kkConstraints.get(i).kkVariablePositions.get(0).col;
		int varTwoRow = kkConstraints.get(i).kkVariablePositions.get(1).row;
		int varTwoCol = kkConstraints.get(i).kkVariablePositions.get(1).col;
		
		// a + b
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			for(int k = 0; k < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); k++) {
		
				// two variables case
				if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j) + 
						kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k) == kkConstraints.get(i).opResult) {
					if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j))))
						possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j));
					if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k))))
						possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k));
				}
			}
		}
		
		// now remove the values that are not possible for the variables in this constraint
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)))
				kkMatrix[varOneRow][varOneCol].kkVariableDomain.remove(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)));
		}
		for(int j = 0; j < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)))
				kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.remove(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)));
		}
		
		// clean the possible values array
		possibleValues.clear();
		
		kkConstraints.get(i).wasProcessed = true;
		
		// if any domain contains just one value it is assigned to the variable
		if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.size() == 1) {
			kkMatrix[varOneRow][varOneCol].value = kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(0);
			int value = kkMatrix[varOneRow][varOneCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varOneRow, varOneCol, value);
			
			kkMatrix[varOneRow][varOneCol].kkVariableDomain.add(value);
			
		}
		if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size() == 1) {
			kkMatrix[varTwoRow][varTwoCol].value = kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(0);
			int value = kkMatrix[varTwoRow][varTwoCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varTwoRow, varTwoCol, value);
			
			kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.add(value);						
		}
		
	}
	
	/* ----------------------------------------------------
	 * method used to apply three consistency to sum case
	 * -------------------------------------------------- */
	public void threeSumConsistency(int i) {
		
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		int varOneRow = kkConstraints.get(i).kkVariablePositions.get(0).row;
		int varOneCol = kkConstraints.get(i).kkVariablePositions.get(0).col;
		int varTwoRow = kkConstraints.get(i).kkVariablePositions.get(1).row;
		int varTwoCol = kkConstraints.get(i).kkVariablePositions.get(1).col;
		int varThreeRow = kkConstraints.get(i).kkVariablePositions.get(2).row;
		int varThreeCol = kkConstraints.get(i).kkVariablePositions.get(2).col;
		
		// a + b + c
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			for(int k = 0; k < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); k++) {
				for(int l = 0; l < kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.size(); l++) {
					if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j) + 
							kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k) + 
							kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(l) == kkConstraints.get(i).opResult) {
						if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j))))
							possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j));
						if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k))))
							possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k));
						if(!possibleValues.contains(new Integer(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(l))))
							possibleValues.add(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(l));
					}
				}
			}
		}
		
		// now remove the values that are not possible for the variables in this constraint
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)))
				kkMatrix[varOneRow][varOneCol].kkVariableDomain.remove(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)));
		}
		for(int j = 0; j < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)))
				kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.remove(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)));
		}
		
		for(int j = 0; j < kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(j)))
				kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.remove(new Integer(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(j)));
		}
		
		// clean the possible values array
		possibleValues.clear();
		
		kkConstraints.get(i).wasProcessed = true;
		
		// if any domain contains just one value it is assigned to the variable
		if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.size() == 1) {
			kkMatrix[varOneRow][varOneCol].value = kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(0);
			int value = kkMatrix[varOneRow][varOneCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varOneRow, varOneCol, value);
			
			kkMatrix[varOneRow][varOneCol].kkVariableDomain.add(value);
			
		}
		if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size() == 1) {
			kkMatrix[varTwoRow][varTwoCol].value = kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(0);
			int value = kkMatrix[varTwoRow][varTwoCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varTwoRow, varTwoCol, value);
			
			kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.add(value);						
		}
		if(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.size() == 1) {
			kkMatrix[varThreeRow][varThreeCol].value = kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(0);
			int value = kkMatrix[varThreeRow][varThreeCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varThreeRow, varThreeCol, value);
			
			kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.add(value);						
		}
		
	}

	/* ----------------------------------------------------
	 * method used to apply two consistency to multiplication 
	 * case
	 * -------------------------------------------------- */
	public void twoMulConsistency(int i) {
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
		
		int varOneRow = kkConstraints.get(i).kkVariablePositions.get(0).row;
		int varOneCol = kkConstraints.get(i).kkVariablePositions.get(0).col;
		int varTwoRow = kkConstraints.get(i).kkVariablePositions.get(1).row;
		int varTwoCol = kkConstraints.get(i).kkVariablePositions.get(1).col;
		
		// a * b
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			for(int k = 0; k < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); k++) {
				if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j) * 
						kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k) == kkConstraints.get(i).opResult) {
					if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j))))
						possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j));
					if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k))))
						possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k));
				}
			}
		}
		
		// now remove the values that are not possible for the variables in this constraint
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)))
				kkMatrix[varOneRow][varOneCol].kkVariableDomain.remove(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)));
		}
		for(int j = 0; j < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)))
				kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.remove(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)));
		}
		// clean the possible values array
		possibleValues.clear();
		
		kkConstraints.get(i).wasProcessed = true;
		
		// if any domain contains just one value it is assigned to the variable
		if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.size() == 1) {
			kkMatrix[varOneRow][varOneCol].value = kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(0);
			int value = kkMatrix[varOneRow][varOneCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varOneRow, varOneCol, value);
			
			kkMatrix[varOneRow][varOneCol].kkVariableDomain.add(value);
			
		}
		if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size() == 1) {
			kkMatrix[varTwoRow][varTwoCol].value = kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(0);
			int value = kkMatrix[varTwoRow][varTwoCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varTwoRow, varTwoCol, value);
			
			kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.add(value);						
		}
		
	}
	
	/* ----------------------------------------------------
	 * method used to apply three consistency to multiplication
	 * case
	 * -------------------------------------------------- */
	public void threeMulConsistency(int i) {
		
		ArrayList<Integer> possibleValues = new ArrayList<Integer>();
	
		int varOneRow = kkConstraints.get(i).kkVariablePositions.get(0).row;
		int varOneCol = kkConstraints.get(i).kkVariablePositions.get(0).col;
		int varTwoRow = kkConstraints.get(i).kkVariablePositions.get(1).row;
		int varTwoCol = kkConstraints.get(i).kkVariablePositions.get(1).col;
		int varThreeRow = kkConstraints.get(i).kkVariablePositions.get(2).row;
		int varThreeCol = kkConstraints.get(i).kkVariablePositions.get(2).col;

		// a * b
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			for(int k = 0; k < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); k++) {
				for(int l = 0; l < kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.size(); l++) {
					if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j) * 
							kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k) * 
							kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(l) == kkConstraints.get(i).opResult) {
						if(!possibleValues.contains(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j))))
							possibleValues.add(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j));
						if(!possibleValues.contains(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k))))
							possibleValues.add(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(k));
						if(!possibleValues.contains(new Integer(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(l))))
							possibleValues.add(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(l));
					}
				}
			}
		}
		
		// now remove the values that are not possible for the variables in this constraint
		for(int j = 0; j < kkMatrix[varOneRow][varOneCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)))
				kkMatrix[varOneRow][varOneCol].kkVariableDomain.remove(new Integer(kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(j)));
		}
		for(int j = 0; j < kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)))
				kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.remove(new Integer(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(j)));
		}
		for(int j = 0; j < kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.size(); j++) {
			if(!possibleValues.contains(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(j)))
				kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.remove(new Integer(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(j)));
		}
		
		// clean the possible values array
		possibleValues.clear();
		
		kkConstraints.get(i).wasProcessed = true;
		
		// if any domain contains just one value it is assigned to the variable
		if(kkMatrix[varOneRow][varOneCol].kkVariableDomain.size() == 1) {
			kkMatrix[varOneRow][varOneCol].value = kkMatrix[varOneRow][varOneCol].kkVariableDomain.get(0);
			int value = kkMatrix[varOneRow][varOneCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varOneRow, varOneCol, value);
			
			kkMatrix[varOneRow][varOneCol].kkVariableDomain.add(value);
			
		}
		if(kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.size() == 1) {
			kkMatrix[varTwoRow][varTwoCol].value = kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.get(0);
			int value = kkMatrix[varTwoRow][varTwoCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varTwoRow, varTwoCol, value);
			
			kkMatrix[varTwoRow][varTwoCol].kkVariableDomain.add(value);						
		}
		if(kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.size() == 1) {
			kkMatrix[varThreeRow][varThreeCol].value = kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.get(0);
			int value = kkMatrix[varThreeRow][varThreeCol].value;
			
			// removes all the same number for all the variables domains of this row and column
			rowColConsistency(varThreeRow, varThreeCol, value);
			
			kkMatrix[varThreeRow][varThreeCol].kkVariableDomain.add(value);				
		}		
	}
	
	/* ----------------------------------------------------
	 * method used to apply line and row constraints rules 
	 * whenever needed
	 * -------------------------------------------------- */
	public void rowColConsistency(int row, int col, int value) {
				
		for(int j = 0; j < this.n; j++) {
			kkMatrix[row][j].kkVariableDomain.remove(new Integer(value));
			kkMatrix[j][col].kkVariableDomain.remove(new Integer(value));
		}
		
	}

	
	/* ----------------------------------------------------
	 * this method checks if an instance of ken ken is 
	 * already solved
	 * -------------------------------------------------- */
	public boolean solvedKenKen() {
		
		boolean endPuzzle = true;
		for(int i = 0; i < this.n; i++) {
			for(int j = 0; j < this.n; j++) {
				if(kkMatrix[i][j].value == 0)
					endPuzzle = false;
			}
		}
		
		if(endPuzzle) {
			JOptionPane.showMessageDialog(new JFrame(), "Puzzle is already solved!", "KenKen Solver", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		
		return false;
	}
	
	/* ----------------------------------------------------
	 * backtracking search method to solve the problem
	 * -------------------------------------------------- */
	public boolean searchKenKenPuzzle() {
		searchAux();
		return false;
	}
	
	public boolean searchAux() {
		
		int row = -1, col = -1;
		boolean unassignedLocation = false;
		
		for(int i = 0; i < this.n; i++) {
			for(int j = 0; j < this.n; j++) {
				if(kkMatrix[i][j].value == 0) {
					row = i;
					col = j;
					unassignedLocation = true;
				}
			}
		}
		
		if(!unassignedLocation)
			return true;
		
		for(int i = 0; i < kkMatrix[row][col].kkVariableDomain.size(); i++) {
			if((!checkRowColConflict(row, col, kkMatrix[row][col].kkVariableDomain.get(i))) && (checkConstraintsValues())) {
				
				kkMatrix[row][col].value = kkMatrix[row][col].kkVariableDomain.get(i);
				
				if(searchAux())
					return true;
				
				kkMatrix[row][col].value = 0;
			}
		}
		
		return false;
	}
	
	/* ----------------------------------------------------
	 * method used to check if so far all the constraints
	 * that were fully filled respect the desired values
	 * -------------------------------------------------- */
	public boolean checkConstraintsValues() {
		
		for(int i = 0; i < kkConstraints.size(); i++) {
			
			boolean isComplete = true;
			int opResult = 0;
			int opResult2 = 0;
			
			// first checks whether the constraint is full filled or not
			for(int j = 0; j < kkConstraints.get(i).kkVariablePositions.size(); j++) {
				if(kkMatrix[kkConstraints.get(i).kkVariablePositions.get(j).row][kkConstraints.get(i).kkVariablePositions.get(j).col].value == 0)
					isComplete = false;
			}
			
			// if it is full filled proceeds to check its value
			if(isComplete) {
				
				// checks equality case
				if(kkConstraints.get(i).opSign == '=') {
					
					opResult = kkMatrix[kkConstraints.get(i).kkVariablePositions.get(0).row][kkConstraints.get(i).kkVariablePositions.get(0).col].value;
					
					// if the value of the variable is not the same as the result, then the constraint is wrong
					if(opResult != kkConstraints.get(i).opResult){
						return false;
					}
					
				}
				
				// checks division case
				if(kkConstraints.get(i).opSign == '/') {
					
					opResult = 0;
					opResult2 = 0;
					
					opResult = kkMatrix[kkConstraints.get(i).kkVariablePositions.get(0).row][kkConstraints.get(i).kkVariablePositions.get(0).col].value /
							kkMatrix[kkConstraints.get(i).kkVariablePositions.get(1).row][kkConstraints.get(i).kkVariablePositions.get(1).col].value;
					opResult2 = kkMatrix[kkConstraints.get(i).kkVariablePositions.get(1).row][kkConstraints.get(i).kkVariablePositions.get(1).col].value /
							kkMatrix[kkConstraints.get(i).kkVariablePositions.get(0).row][kkConstraints.get(i).kkVariablePositions.get(0).col].value;
					
					// if any of the two values is different from the desired, then the constraint is wrong
					if((opResult != kkConstraints.get(i).opResult) && (opResult2 != kkConstraints.get(i).opResult)) {
						return false;
					}
				}
				
				// checks subtraction case
				if(kkConstraints.get(i).opSign == '-') {
					
					opResult = 0;
					opResult2 = 0;
					
					opResult = kkMatrix[kkConstraints.get(i).kkVariablePositions.get(0).row][kkConstraints.get(i).kkVariablePositions.get(0).col].value -
							kkMatrix[kkConstraints.get(i).kkVariablePositions.get(1).row][kkConstraints.get(i).kkVariablePositions.get(1).col].value;
					opResult2 = kkMatrix[kkConstraints.get(i).kkVariablePositions.get(1).row][kkConstraints.get(i).kkVariablePositions.get(1).col].value -
							kkMatrix[kkConstraints.get(i).kkVariablePositions.get(0).row][kkConstraints.get(i).kkVariablePositions.get(0).col].value;
					
					// if any of the two values is different from the desired, then the constraint is wrong
					if((opResult != kkConstraints.get(i).opResult) && (opResult2 != kkConstraints.get(i).opResult)) {
						return false;
					}
				}
				
				// checks sum case
				if(kkConstraints.get(i).opSign == '+') {
					
					opResult = 0;
					
					for(int j = 0; j < kkConstraints.get(i).kkVariablePositions.size(); j++) {
						opResult += kkMatrix[kkConstraints.get(i).kkVariablePositions.get(j).row][kkConstraints.get(i).kkVariablePositions.get(j).col].value;
					}
					
					// if the obtained value is different from the desired one the constraint is wrong
					if(opResult != kkConstraints.get(i).opResult) {
						return false;
					}
					
				}
				
				// checks multiplication case
				if(kkConstraints.get(i).opSign == 'x') {
					
					opResult = 1;
					
					for(int j = 0; j < kkConstraints.get(i).kkVariablePositions.size(); j++) {
						opResult *= kkMatrix[kkConstraints.get(i).kkVariablePositions.get(j).row][kkConstraints.get(i).kkVariablePositions.get(j).col].value;
					}
					
					// if the obtained value is different from the desired one the constraint is wrong
					if(opResult != kkConstraints.get(i).opResult) {
						return false;
					}
					
				}
				
			}
			
		}
		
		// if all the constraints results are right it returns true
		return true;
	}
	
	/* ----------------------------------------------------
	 * method used to check if there is number conflict on 
	 * a same row or column, giving the number as parameter
	 * -------------------------------------------------- */
	public boolean checkRowColConflict(int row, int col, int n) {
		
		// if there is any conflict returns true
		for(int i = 0; i < this.n; i++) {
			if(kkMatrix[row][i].value == n)
				return true;
			if(kkMatrix[i][col].value == n)
				return true;
		}
		
		// if there is no conflict returns false
		return false;
	}
	
	/* ----------------------------------------------------
	 * method responsible for calculating the search space
	 * of the current instance of the problem
	 * -------------------------------------------------- */
	public void calcSearchSpace() {
		searchSpace = BigInteger.valueOf(1);
		
		for(int i = 0; i < this.n; i++) {
			for(int j = 0; j < this.n; j++) {
				searchSpace = searchSpace.multiply(BigInteger.valueOf(kkMatrix[i][j].kkVariableDomain.size()));
			}
		}
		
		System.out.println("Search space: " + searchSpace);
	}
	
}

