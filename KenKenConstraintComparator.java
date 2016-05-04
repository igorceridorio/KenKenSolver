/*-----------------------------------------------------------
 * ARCHIVE: KenKenConstraintComparator.java
 * 
 * OBJECTIVE:
 * This class defines a KenKenConstraint object comparator. It 
 * is necessary because the consistency algorithm implementation 
 * processes first the constraints with the least number of 
 * variables. A comparator is needed in order to sort the array 
 * of the constraints according to the number of variables involved 
 * in each one. This class creates the rule used to sort these arrays. 
 *---------------------------------------------------------*/

import java.util.Comparator;

public class KenKenConstraintComparator implements Comparator<KenKenConstraint> {

	@Override
	public int compare(KenKenConstraint c1, KenKenConstraint c2) {
		
		if(c1.kkVariablePositions.size() > c2.kkVariablePositions.size())
			return 1;
		else if(c1.kkVariablePositions.size() < c2.kkVariablePositions.size())
			return -1;
		else
			return 0;
	}

}
