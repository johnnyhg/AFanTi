package AFanTi.Neighborhood;

import java.util.Comparator;

public class NeighborhoodComparator implements Comparator<Neighborhood>  {

	@Override
	public int compare(Neighborhood n1, Neighborhood n2) {
		
		
		if(Math.abs(n1.similarity)>Math.abs(n2.similarity))
			return 1;
		else if(Math.abs(n1.similarity)<Math.abs(n2.similarity))
			return -1;
	
		return 0;
	}

}
