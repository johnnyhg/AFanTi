package AFanTi.Recommend;

import org.ylj.math.Vector;





public class Call {
	
	long callSerial;
	
	long callSerial_client;
	
	Vector[] candidteItemVs;
	
	/*
	 * for Neighborhood
	 */
	int expectNeighborhoodResultCount;
	int neighborhoodResultArrivedCount;
	int[] neighborhoodServerIDs;
	CallBackResult_fromNeighborhoodServer[]neighborhoodResultArrary;
	
	
	/*
	 * for send to client 
	 */
	
	RecommendedItem[]  recommendedItems;

	
	long waitAtNanoTime;  //nano tiem
	
	
	
	public boolean addNeighborhoodResult(CallBackResult_fromNeighborhoodServer result)
	{
		for(int i=0;i<neighborhoodServerIDs.length;i++)
		{
			if(neighborhoodServerIDs[i]==result.NeighborhoodServer_ID)
			{
				if(neighborhoodResultArrary[i]==null)
					neighborhoodResultArrivedCount++;
				neighborhoodResultArrary[i]=result;
				return true;
				
			}
		}
		return false;
	}
	
	public boolean isAllNeighborhoodResultOK()
	{
		if(expectNeighborhoodResultCount==neighborhoodResultArrivedCount)
			return true;
		return false;
	}
	
	
}
