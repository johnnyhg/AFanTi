package org.ylj.math;

import org.apache.mahout.math.Arrays;

public class testTopN {
	public static void main(String[] args)
	{
		LongComparator lComparator=new LongComparator();
		
	
		TopN<Long> topN=new TopN<Long>(5,lComparator);
		System.out.println(topN.getLength());
		topN.put(3L);
		System.out.println(topN.getLength());
		topN.put(1L);
		System.out.println(topN.getLength());
		topN.put(2L);
		
		System.out.println(Arrays.toString(topN.toArrary(new Long[topN.getLength()])));
		topN.put(4L);
		System.out.println(Arrays.toString(topN.toArrary(new Long[topN.getLength()])));
		topN.put(6L);
		System.out.println(Arrays.toString(topN.toArrary(new Long[topN.getLength()])));
		topN.put(8L);
		System.out.println(Arrays.toString(topN.toArrary(new Long[topN.getLength()])));
		topN.put(5L);
		System.out.println(Arrays.toString(topN.toArrary(new Long[topN.getLength()])));
		topN.put(7L);
		System.out.println(Arrays.toString(topN.toArrary(new Long[topN.getLength()])));
		topN.put(7L);
	
		topN.put(7L);
	
		
		topN.put(7L);
	
		topN.put(7L);
		
		topN.put(7L);

		topN.put(7L);
		
		return;
		
	}
}
