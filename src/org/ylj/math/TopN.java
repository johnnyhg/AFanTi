package org.ylj.math;

import java.util.Comparator;

public class TopN<E> {

	final int  N;
	MinimumHeap<E> minmHeap;
	Comparator<E> comparator;
	public TopN(int n,Comparator<E> com)
	{
		N=n;
		comparator=com;
		minmHeap=new MinimumHeap<E>(N, com);
	}
	
	public int getLength()
	{
		return minmHeap.tailIndex-minmHeap.topIndex;
	}
	public boolean put(E e)
	{
		if(e==null)
			return false;
		
		if(!minmHeap.isFull())
		{
			minmHeap.insertAtTail(e);
			return true;
		}
	
		E topE=minmHeap.getTop();
		if(comparator.compare(e,topE)>0)
		{
			minmHeap.removeTop();
			minmHeap.insertAtTail(e);
			return true;
		}
		return false;
	}
	
	public E[] toArrary(E[] Earrary)
	{
		return minmHeap.toArrary(Earrary);
	}

	
}
