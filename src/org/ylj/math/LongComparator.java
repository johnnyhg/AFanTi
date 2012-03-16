package org.ylj.math;

import java.util.Comparator;

public class LongComparator implements Comparator<Long> {

	@Override
	public int compare(Long arg0, Long arg1) {
		// TODO Auto-generated method stub
		
		if(arg0>arg1)
			return 1;
		else if(arg0<arg1)
			return -1;
		
		return 0;
	}

}
