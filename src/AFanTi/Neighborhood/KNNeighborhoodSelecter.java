package AFanTi.Neighborhood;

import org.ylj.math.Vector;

import AFanTi.DataModel.DataModel;
import AFanTi.Similarity.SimilarityComputer;

public class KNNeighborhoodSelecter implements NeighborhoodSelecter {

	DataModel dataModel;
	SimilarityComputer similarityComputer;
	int K = 0;

	public KNNeighborhoodSelecter(SimilarityComputer sComputer,
			DataModel model, int Nneighbors) {
		similarityComputer = sComputer;
		dataModel = model;
		K = Nneighbors;
	}

	@Override
	public Neighborhood[] getNeighborhoodsOfItem(Vector itemV, long userID) {
		// TODO Auto-generated method stub
		Neighborhood[] NNeighborhoods = null;
		if (itemV == null)
			return null;

	
		long[] candidateItems = dataModel.getAllItemsRatedByUser(userID);
		if(candidateItems==null)
			return null;
		
		if (dataModel.containItem(itemV.getVectorID())) {
			long[] tempArrary=new long[candidateItems.length-1];
			int offset=0;
			for(int i=0;i<tempArrary.length;i++)
			{
				if(candidateItems[i]==itemV.getVectorID())
					offset=1;
				tempArrary[i]=candidateItems[i+offset];
			}
			candidateItems=tempArrary;
			
		}
		//get all candidateItems
		if (candidateItems.length <= K) {
			// all candidateItems is Neighborhood
			NNeighborhoods = new Neighborhood[candidateItems.length];

			for (int i = 0; i < candidateItems.length; i++) {
				NNeighborhoods[i].neighborhood = dataModel
						.getItemVector(candidateItems[i]);
				NNeighborhoods[i].similarity = similarityComputer
						.computeSimilarity(itemV, NNeighborhoods[i].neighborhood);
			}
			return NNeighborhoods;

		}
		
		
		
		
		return NNeighborhoods;
	}
	
	
	@Override
	public Neighborhood[] getNeighborhoodsOfUser(Vector user, long itemID) {
		// TODO Auto-generated method stub
		return null;
	}

}
