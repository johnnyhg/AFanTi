package org.ylj.common;

public class TimeInterval {
	
	static int IntervalCursor =0;
	static int IntervalArraySize=5;
	static long[] IntervalBegins=new long[IntervalArraySize];
	
	
	
	public static int startNewInterval()
	{
	
		int tryCount=0;
		while(IntervalBegins[IntervalCursor]!=0)
		{
			System.out.println(IntervalBegins[IntervalCursor]);
			
			if(IntervalCursor==IntervalArraySize-1)
				IntervalCursor=0;
			else
				IntervalCursor++;
			
			tryCount++;
			if(tryCount>IntervalArraySize)
				return -1;
		}
		IntervalBegins[IntervalCursor]=System.currentTimeMillis();
		
		return IntervalCursor;
		
	}
	public static long endInterval()
	{
		long intervalValue= System.currentTimeMillis()-IntervalBegins[IntervalCursor];
		IntervalBegins[IntervalCursor]=0;
		return intervalValue;
		
	}
	public static long endInterval(int intervalIndex)
	{
		long intervalValue= System.currentTimeMillis()-IntervalBegins[intervalIndex];
		IntervalBegins[intervalIndex]=0;
		return intervalValue;
		
	}
}
