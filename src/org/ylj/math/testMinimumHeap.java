package org.ylj.math;

import org.apache.mahout.math.Arrays;

public class testMinimumHeap {
	
	public static void main(String[] args)
	{
		LongComparator lComparator=new LongComparator();
		
		MinimumHeap<Long> aMinimumHeap=new MinimumHeap<Long>(10, lComparator);
		

		
		aMinimumHeap.insertAtTail(10L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(9L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(8L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(7L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(6L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(5L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(4L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(3L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(2L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
		aMinimumHeap.insertAtTail(1L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
	
		Long top=aMinimumHeap.removeTop();
		System.out.println("top="+top);
		aMinimumHeap.insertAtTail(10L);
		System.out.println(Arrays.toString(aMinimumHeap.toArrary()));
	}
}
