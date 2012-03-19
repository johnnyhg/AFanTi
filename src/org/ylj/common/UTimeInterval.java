package org.ylj.common;

public class UTimeInterval {
	static int IntervalCursor =0;
	static int IntervalArraySize=5;
	static long[] IntervalBegins=new long[IntervalArraySize];
	
	
	
	public static int startNewInterval()
	{
	
		int tryCount=0;
		while(IntervalBegins[IntervalCursor]!=0)
		{
			
			
			if(IntervalCursor==IntervalArraySize-1)
				IntervalCursor=0;
			else
				IntervalCursor++;
			
			tryCount++;
			if(tryCount>IntervalArraySize)
				return -1;
		}
		IntervalBegins[IntervalCursor]=System.nanoTime();
		
		return IntervalCursor;
		
	}
	public static long endInterval()
	{
		long intervalValue= System.nanoTime()-IntervalBegins[IntervalCursor];
		IntervalBegins[IntervalCursor]=0;
		return intervalValue/1000;
		
	}
	public static long endInterval(int intervalIndex)
	{
		long intervalValue= System.nanoTime()-IntervalBegins[intervalIndex];
		IntervalBegins[intervalIndex]=0;
		return intervalValue/1000;
		
	}
}
