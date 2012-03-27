package AFanTi.Call;






import 

import AFanTi.Neighborhood.AsynchronousItemKNNeighborhoodResultReceiverProxy;
import AFanTi.Neighborhood.Neighborhood;
import AFanTi.Recommend.AsyncRecommenditionReceiverProxy;
import AFanTi.Recommend.CallBackResult_fromNeighborhoodServer;
import AFanTi.Recommend.RecommendedItem;





public class Call {
	
	public long callSerial;

	public org.ylj.math.Vector[] candidteItemVs;
	
	/*
	 * for Neighborhood
	 */
	public int expectNeighborhoodResultCount;
	public int neighborhoodResultArrivedCount;
	public int[] neighborhoodServerIDs;
	public java.util.Vector<Neighborhood[]>[] neighborhoodResultArrary;
	
	
	/*
	 * for send to client 
	 */
	
	public RecommendedItem[]  recommendedItems;

	
	public long waitAtNanoTime;  //nano tiem
	
	/*
	 * for call back
	 */
	public AsyncRecommenditionReceiverProxy resultReceiverProxy;
	
	
	
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
