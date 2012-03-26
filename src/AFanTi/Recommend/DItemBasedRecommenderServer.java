package AFanTi.Recommend;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import java.util.Queue;

import org.ylj.math.Vector;


import AFanTi.Estimate.RatingEstimater;
import AFanTi.Estimate.GeneralRatingEstimater;
import AFanTi.Neighborhood.AsynchronousItemKNNeighborhoodService;
import AFanTi.Neighborhood.Neighborhood;

public class DItemBasedRecommenderServer implements RecommendService,
		setNeighborhoodsCallBack {

	long CALL_SERIVAL = 1;
	int K;//k - nighborhoods
	int N;//n - n-recommend items
	
	RatingEstimater RatingEstimater=new GeneralRatingEstimater();
	
	
	private Map<Long, Call> resultsWaitForNeighborsMap = new HashMap<Long, Call>();

	
	private Queue<Call> resultsWaitForNeighborsQueue = new LinkedList<Call>();
	
	private Queue<Call> resultsWaitToComputeQueue = new LinkedList<Call>();
	
	private Queue<Call> resultsWaitToRespondQueue = new LinkedList<Call>();
	
	
	/*
	 * 
	 */
	private Map<Long,AsyncClientProxy> call2clientMap = new HashMap<Long,AsyncClientProxy>();
	private Queue<AsyncClientProxy> clientList = new LinkedList<AsyncClientProxy>();
	
	
	
	private List<AsynchronousItemKNNeighborhoodService> NeighborhoodServerList = new LinkedList<AsynchronousItemKNNeighborhoodService>();

	boolean running;

	public DItemBasedRecommenderServer() {
		super();
		CALL_SERIVAL = 1;
	}

	public Vector[] getCandidateItemVs(long userID) {
		return null;
	}

	public void getAllNeighborhoods(Vector[] candidateItems, long userID,
			long callSerial) {

		Call thisCallResult = new Call();
		/*
		 * initial thisCallResult
		 */
		thisCallResult.callSerial = callSerial;
		thisCallResult.candidteItemVs = candidateItems;
		thisCallResult.expectNeighborhoodResultCount = NeighborhoodServerList.size();
		thisCallResult.neighborhoodServerIDs = new int[thisCallResult.expectNeighborhoodResultCount];
		thisCallResult.neighborhoodResultArrivedCount = 0;
		thisCallResult.neighborhoodResultArrary = new CallBackResult_fromNeighborhoodServer[thisCallResult.expectNeighborhoodResultCount];
		thisCallResult.waitAtNanoTime = System.nanoTime();

		resultsWaitForNeighborsQueue.add(thisCallResult);
		resultsWaitForNeighborsMap.put(callSerial, thisCallResult);

		for (AsynchronousItemKNNeighborhoodService Nserver : NeighborhoodServerList) {
			// call remote server
			Nserver.getNeighborhoodsOfItems(candidateItems, userID, callSerial);

		}
	}

	@Override
	public RecommendedItem[] makeRecommend(long userID, int num) {
		// TODO Auto-generated method stub

		CALL_SERIVAL++;
		if (Long.MAX_VALUE == CALL_SERIVAL) {
			CALL_SERIVAL = 0;

		}
		long thisCallSerial = CALL_SERIVAL;

		Vector[] candidateItemvs = getCandidateItemVs(userID);

		getAllNeighborhoods(candidateItemvs, userID, thisCallSerial);

		return null;
	}

	@Override
	public boolean setNeighborhoodsCallBack(long callSerial,
			CallBackResult_fromNeighborhoodServer result) {
		// TODO Auto-generated method stub
		Call callResults = resultsWaitForNeighborsMap
				.get(callSerial);

		if (callResults == null)
			return false;
		boolean returnCode = callResults.addNeighborhoodResult(result);

		if (callResults.isAllNeighborhoodResultOK()) {
			/*
			 * do something
			 */
		}
		return returnCode;
	}

	private class ResponderThread extends Thread {

		@Override
		public void run() {

			while (running) {
				
				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok 
				 * just 
				 */
				
				while(resultsWaitToRespondQueue.size()>0)
				{
					
					Call aCall=resultsWaitToRespondQueue.poll();
					long callSerial=aCall.callSerial_client;
					AsyncClientProxy client=call2clientMap.get(callSerial);
					client.setRecommendItem(callSerial, aCall.recommendedItems);
				
					/*
					 * remove aCall
					 */
				}
				
			}

		}
	}

	private class TimeOutCheckThread extends Thread {
		
		@Override
		public void run() {

			while (running) {
				
			

			}

		}
	}
	
	private class DoComputeThread extends Thread {
		
		@Override
		public void run() {

			while (running) {
				
				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok 
				 * just 
				 */
				while(resultsWaitToComputeQueue.size()>0)
				{
					//do a Call;
					Call okResult=resultsWaitToComputeQueue.poll();
					
					PriorityQueue<RecommendedItem> topKCandidates=new PriorityQueue<RecommendedItem>();
				
					for(int i=0;i<okResult.candidteItemVs.length;i++)
					{
						
						
						PriorityQueue<Neighborhood> topKNeighborhoods=new PriorityQueue<Neighborhood>();
						
						for(CallBackResult_fromNeighborhoodServer aNeighborhoodServer:okResult.neighborhoodResultArrary)
						{
							Neighborhood[] tempNeighborhoods=aNeighborhoodServer.neighborhoodList.get(i);
							
							for(Neighborhood neighborhood:tempNeighborhoods)
							{
								
								if(topKNeighborhoods.size()<K)
								{
									topKNeighborhoods.add(neighborhood);
								}
								else
								{
									if(neighborhood.similarity>topKNeighborhoods.peek().similarity)
									{
										topKNeighborhoods.poll();
										topKNeighborhoods.add(neighborhood);
									}
								}
							}		
							
						}
						Neighborhood[] trueNeighborhoods=topKNeighborhoods.toArray(new Neighborhood[topKNeighborhoods.size()]);
						float estimateRating=RatingEstimater.estimateRating(trueNeighborhoods);
						
						
						
						
						if(topKCandidates.size()<N)
						{
							RecommendedItem newRecommendedItem=new RecommendedItem(okResult.candidteItemVs[i].getVectorID(),estimateRating);
							topKCandidates.add(newRecommendedItem);
						}
						else
						{
							if(estimateRating>topKCandidates.peek().estRating)
							{
								RecommendedItem newRecommendedItem=new RecommendedItem(okResult.candidteItemVs[i].getVectorID(),estimateRating);
			
								topKCandidates.poll();
								topKCandidates.add(newRecommendedItem);
							}
						}
						
					}
					
					okResult.candidteItemVs=null;
					okResult.neighborhoodServerIDs=null;
					okResult.neighborhoodResultArrary=null;
		
					okResult.recommendedItems=topKCandidates.toArray(new RecommendedItem[topKCandidates.size()]);
					
					resultsWaitToRespondQueue.add(okResult);
					
				}

			}

		}
	}
}
