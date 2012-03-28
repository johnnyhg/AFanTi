package AFanTi.Call;







import java.util.PriorityQueue;

import AFanTi.Neighborhood.AsynchronousItemKNNeighborhoodResultReceiverProxy;
import AFanTi.Neighborhood.Neighborhood;
import AFanTi.Recommend.AsyncRecommenditionReceiverProxy;
import AFanTi.Recommend.CallBackResult_fromNeighborhoodServer;
import AFanTi.Recommend.RecommendedItem;





public class Call {
	
	public long callSerial;

	public int N;

	/*
	 * for Estimate Rating
	 */
	public long[] candidateItemsID;
	// parts mark
	public boolean[] partsResultSetMark;
	public PriorityQueue<RecommendedItem> TopNItems;
	
	
	/*
	 * for send to client 
	 */
	public long waitAtNanoTime;  //nano tiem
	
	/*
	 * for call back
	 */
	public AsyncRecommenditionReceiverProxy resultReceiverProxy;
	
	public void setEstimateRating(long[] itemIDs, float[] ratings, int part_K)
	{
		if(itemIDs==null||ratings==null)
			return ;
		for(int i=0;i<itemIDs.length;i++)
		{
			
			if(TopNItems.size()<N)
			{
				RecommendedItem newRecommendedItem=new RecommendedItem(itemIDs[i],ratings[i]);
				TopNItems.add(newRecommendedItem);
			}
			else
			{
				RecommendedItem minItem=TopNItems.peek();
				
				if(ratings[i]>minItem.estRating)
				{
					RecommendedItem newRecommendedItem=new RecommendedItem(itemIDs[i],ratings[i]);
					TopNItems.add(newRecommendedItem);
					TopNItems.poll();
				}
				
			}
		
		}
		partsResultSetMark[part_K]=true;
		
	}
	
	public  boolean isAllPartsOk()
	{
		for(int i=0;i<partsResultSetMark.length;i++)
		{
			if(partsResultSetMark[i]==false)
				return false;
		}
		return true;
	}
	
	public  void initalPartsFalse()
	{
		for(int i=0;i<partsResultSetMark.length;i++)
		{
			partsResultSetMark[i]=false;
				
		}

	}
	public RecommendedItem[] getRecommendedItems()
	{
		return TopNItems.toArray(new RecommendedItem[TopNItems.size()]);
	}
}
