package AFanTi.Neighborhood;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.ylj.common.TimeInterval;
import org.ylj.common.UTimeInterval;
import org.ylj.math.TopN;
import org.ylj.math.Vector;

import AFanTi.DataModel.DataModel;
import AFanTi.Similarity.SimilarityComputer;

public class KNNeighborhoodSelecter2 implements NeighborhoodSelecter {

	DataModel dataModel;
	SimilarityComputer similarityComputer;

	

	int K = 0;

	public KNNeighborhoodSelecter2(SimilarityComputer sComputer,
			DataModel model, int Nneighbors) {
		similarityComputer = sComputer;
		dataModel = model;
		K = Nneighbors;
	}

	@Override
	public Neighborhood[] getNeighborhoodsOfItem(Vector itemV, long userID) {
		// TODO Auto-generated method stub
		//UTimeInterval.startNewInterval();
		
		Neighborhood[] NNeighborhoods = null;
		if (itemV == null)
			return null;
		
		long[] candidateItems = dataModel.getAllItemsRatedByUser(userID);
		if (candidateItems == null)
			return null;

		if (dataModel.containItem(itemV.getVectorID())) {
			long[] tempArrary = new long[candidateItems.length - 1];
			int offset = 0;
			for (int i = 0; i < tempArrary.length; i++) {
				if (candidateItems[i] == itemV.getVectorID())
					offset = 1;
				tempArrary[i] = candidateItems[i + offset];
			}
			candidateItems = tempArrary;

		}
	//	System.out.println("Get candidateItems("+candidateItems.length +") cost "+UTimeInterval.endInterval()+"'us");
	//	int begin_index=UTimeInterval.startNewInterval();
		// get all candidateItems
		if (candidateItems.length <= K) {
			// all candidateItems is Neighborhood
			NNeighborhoods = new Neighborhood[candidateItems.length];

			for (int i = 0; i < candidateItems.length; i++) {
				NNeighborhoods[i].vector = dataModel.getItemVector(candidateItems[i]);
				NNeighborhoods[i].similarity = similarityComputer.computeSimilarity(itemV, NNeighborhoods[i].vector);
			}

			return NNeighborhoods;

		}

		// get topN neighborhood
		NeighborhoodComparator comparator = new NeighborhoodComparator();
		PriorityQueue<Neighborhood> maxKPriorityQueue=new PriorityQueue<Neighborhood>(K+1);
		List<Neighborhood> NaNSimilarityList = new LinkedList<Neighborhood>();
		double lowestTopValue=0;
		boolean full = false;
		
		for (long aItemID : candidateItems) {
			
			//UTimeInterval.startNewInterval();
			
			
			
			Neighborhood aNewNeighborhood;
			Vector tempVector= dataModel.getItemVector(aItemID);
			
			
		//	System.out.println(">> Get   vector cost: "+UTimeInterval.endInterval()+"'us");
		
		//	UTimeInterval.startNewInterval();
			
			double tempSimilarity= similarityComputer.computeSimilarity(tempVector, itemV);
		//	System.out.println(">> Computer similarity cost: "+UTimeInterval.endInterval()+"'us");
			
		//	UTimeInterval.startNewInterval();
			if (Double.isNaN(tempSimilarity))
			{
				aNewNeighborhood=new Neighborhood(tempVector,tempSimilarity);
				NaNSimilarityList.add(aNewNeighborhood);
			}
				
			else
			{

				
			    if (!Double.isNaN(tempSimilarity) && (!full || tempSimilarity > lowestTopValue)) {
			    	aNewNeighborhood = new Neighborhood(tempVector,tempSimilarity);
			    	maxKPriorityQueue.add(aNewNeighborhood);
			          if (full) {
			        	  maxKPriorityQueue.poll();
			          } else if (maxKPriorityQueue.size() > K) {
			            full = true;
			            maxKPriorityQueue.poll();
			          }
			          lowestTopValue = maxKPriorityQueue.peek().getSimilarity();
			    }
			      
			}
				
		//	System.out.println(">> put into topK cost: "+UTimeInterval.endInterval()+"'us");
			
		}
		
	//	System.out.println("Get topN cost "+UTimeInterval.endInterval(begin_index)+"'us");
		
		NNeighborhoods = maxKPriorityQueue.toArray(new Neighborhood[K]);

		int NNeighborhoodsCount = maxKPriorityQueue.size();
		for (int i = NNeighborhoodsCount; i < K; i++) {
			NNeighborhoods[i] = NaNSimilarityList.remove(0);
		}

		return NNeighborhoods;
	}

	@Override
	public Neighborhood[] getNeighborhoodsOfUser(Vector user, long itemID) {
		// TODO Auto-generated method stub
		return null;
	}

}