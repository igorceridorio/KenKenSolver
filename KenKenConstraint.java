/*-----------------------------------------------------------
 * ARCHIVE: KenKenConstraint.java
 * 
 * OBJECTIVE:
 * This class defines a constraint object. It is composed by
 * an operation sign, result, an array with the variables
 * that composes this constraint and a boolean value that
 * indicates if this variable has been processed or not.
 *---------------------------------------------------------*/

import java.util.ArrayList;

public class KenKenConstraint {
	
	public char opSign;
	public int opResult;
	public ArrayList<KenKenVariablePosition> kkVariablePositions = new ArrayList<KenKenVariablePosition>();
	public boolean wasProcessed;
	
}
