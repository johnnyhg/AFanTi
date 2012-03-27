package AFanTi.Neighborhood;

import java.util.LinkedList;
import java.util.List;

import org.ylj.common.TimeInterval;
import org.ylj.common.UTimeInterval;
import org.ylj.math.TopN;
import org.ylj.math.Vector;

import AFanTi.DataModel.DataModel;
import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.Similarity.SimilarityComputer;


/*
 * 
 * use   topN write by myself find little slower than PriorityQueue
 *
 */
public class ItemKNNeighborhoodSelecter1 implements ItemNeighborhoodSelecter {

	ItemBasedDataModel dataModel;
	SimilarityComputer similarityComputer;
	int K = 0;

	public ItemKNNeighborhoodSelecter1(SimilarityComputer sComputer, ItemBasedDataModel model, int Nneighbors) {
		similarityComputer = sComputer;
		dataModel = model;
		K = Nneighbors;
	}

	@Override
	public Neighborhood[] getNeighborhoodsOfItem(Vector itemV, long userID) {
		// TODO Auto-generated method stub
	//	UTimeInterval.startNewInterval();
		
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
		//System.out.println("Get candidateItems("+candidateItems.length +") cost "+UTimeInterval.endInterval()+"'us");
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
		TopN<Neighborhood> topKNeighborhoods = new TopN<Neighborhood>(K, comparator);
		List<Neighborhood> NaNSimilarityList = new LinkedList<Neighborhood>();

		for (long aItemID : candidateItems) {
			
			//UTimeInterval.startNewInterval();
			
			
			
			Neighborhood aNewNeighborhood;
			Vector tempVector= dataModel.getItemVector(aItemID);
		//	System.out.println(">> Get   vector cost: "+UTimeInterval.endInterval()+"'us");
			
			
			
			double tempSimilarity= similarityComputer.computeSimilarity(tempVector, itemV);
			
		
			//UTimeInterval.startNewInterval();
			if (Double.isNaN(tempSimilarity))
			{
				aNewNeighborhood=new Neighborhood(tempVector,tempSimilarity);
				NaNSimilarityList.add(aNewNeighborhood);
			}
				
			else
			{
				
				aNewNeighborhood = new Neighborhood(tempVector,tempSimilarity);
				
				topKNeighborhoods.put(aNewNeighborhood);
			}
				
			//System.out.println(">> put into topK cost: "+UTimeInterval.endInterval()+"'us");
			
			
		}
		//System.out.println("Get topN cost "+UTimeInterval.endInterval(begin_index)+"'us");
		
		NNeighborhoods = topKNeighborhoods.toArrary(new Neighborhood[K]);

		int NNeighborhoodsCount = topKNeighborhoods.getLength();
		for (int i = NNeighborhoodsCount; i < K; i++) {
			NNeighborhoods[i] = NaNSimilarityList.remove(0);
		}

		return NNeighborhoods;
	}

	@Override
	public List<Neighborhood[]> getNeighborhoodsOfItems(Vector[] items,
			long userID) {
		// TODO Auto-generated method stub
		return null;
	}

	



}
