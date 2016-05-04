/*-----------------------------------------------------------
 * ARCHIVE: KenKenVariable.java
 * 
 * OBJECTIVE:
 * This class defines a variable object. It is composed by
 * the value and an array that stores the domain.
 *---------------------------------------------------------*/

import java.util.ArrayList;

public class KenKenVariable {

	public int value;
	public char opSign;
	public int opResult;
	public int consIdx;
	public ArrayList<Integer> kkVariableDomain = new ArrayList<Integer>();
	
	// constructor
	public KenKenVariable(int n) {
		this.value = 0;
		for(int i = 1; i <= n; i++) {
			kkVariableDomain.add(i);
		}
	}

}
