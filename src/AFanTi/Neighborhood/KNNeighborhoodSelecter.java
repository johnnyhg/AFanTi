package AFanTi.Neighborhood;

import java.util.LinkedList;
import java.util.List;

import org.ylj.math.TopN;
import org.ylj.math.Vector;

import AFanTi.DataModel.DataModel;
import AFanTi.Similarity.SimilarityComputer;

public class KNNeighborhoodSelecter implements NeighborhoodSelecter {

	DataModel dataModel;
	SimilarityComputer similarityComputer;
	int K = 0;

	public KNNeighborhoodSelecter(SimilarityComputer sComputer, DataModel model, int Nneighbors) {
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
			Neighborhood aNewNeighborhood = new Neighborhood();
			aNewNeighborhood.vector = dataModel.getItemVector(aItemID);
			aNewNeighborhood.similarity = similarityComputer.computeSimilarity(aNewNeighborhood.vector, itemV);

			if (Double.isNaN(aNewNeighborhood.similarity))
				NaNSimilarityList.add(aNewNeighborhood);
			else

				topKNeighborhoods.put(aNewNeighborhood);
		}

		NNeighborhoods = topKNeighborhoods.toArrary(new Neighborhood[K]);

		int NNeighborhoodsCount = topKNeighborhoods.getLength();
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
