package AFanTi.Recommend;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.log4j.Logger;

import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.GeneralRatingEstimater;
import AFanTi.Estimate.RatingEstimater;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Neighborhood.Neighborhood;

public class ItemBasedRecommender implements Recommender {

	GeneralItemBasedDataModel itemBasedDataModel;
	ItemNeighborhoodSelecter neighborhoodSelecter;
	RatingEstimater ratingEstimater;

	private static Logger logger = Logger
			.getLogger(ItemBasedRecommender.class.getName());

	public ItemBasedRecommender(GeneralItemBasedDataModel dataModel,
			ItemNeighborhoodSelecter nelecter) {
		itemBasedDataModel = dataModel;
		neighborhoodSelecter = nelecter;
		ratingEstimater = new GeneralRatingEstimater();

	}

	@Override
	public RecommendedItem[] makeRecommend(long userID, int num) {
		
		
		
		
		// TODO Auto-generated method stub
		if (!itemBasedDataModel.containUser(userID)||num<=0)
			return null;
		long[] ratingedItems = itemBasedDataModel
				.getAllItemsRatedByUser(userID);
		Set<Long> allItems = itemBasedDataModel.getAllItemIDs();
	
		
		
		
		// do filtrate
		for (long ratingedItem : ratingedItems) {
			allItems.remove(ratingedItem);
		}

		Set<Long> candidateItems = allItems;
		PriorityQueue<RecommendedItem> topNRecommendedItems = new PriorityQueue<RecommendedItem>(
				num, new RecommendedItemComparator());
		boolean full = false;
		float minValue = Float.NEGATIVE_INFINITY;

		// logger.debug("[after get all candidateItems:"+candidateItems.size()+"]"+UTimeInterval.endInterval()+"'us");

		int j = 0;
		for (Long itemID : candidateItems) {
			
			++j;
			

			Vector itemV = itemBasedDataModel.getItemVector(itemID);

			Neighborhood[] neighborhoods = neighborhoodSelecter
					.getNeighborhoodsOfItem(itemV, userID);

			// logger.debug("[after step 1 getNeighborhoodsOfItem  :]"+UTimeInterval.endInterval()+"'us");
			// UTimeInterval.startNewInterval();

			// ********** step 2 :get get All neighborhood's Rating and
			// similarity
			float[] ratingArrary = new float[neighborhoods.length];
			double[] similarityArrary = new double[neighborhoods.length];

			for (int i = 0; i < neighborhoods.length; i++) {
				ratingArrary[i] = itemBasedDataModel.getRating(userID,
						neighborhoods[i].vector.getVectorID());
				similarityArrary[i] = neighborhoods[i].similarity;
			}

			// logger.debug("#step 2  [after getAllneighborhoodsRating()] "+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");

			// ********** step 3 :estimate Rating of user to the item
			float estimatedValue = ratingEstimater.estimateRating(ratingArrary,
					similarityArrary);

			if (estimatedValue > minValue) {
				RecommendedItem candidateItem = new RecommendedItem(itemID,
						estimatedValue);
				
				topNRecommendedItems.add(candidateItem);
				

				if (topNRecommendedItems.size() > num) {
					topNRecommendedItems.poll();
					minValue = topNRecommendedItems.peek().estRating;
				}
			}

			// ********** step 4 :after put int topNRecommendedItem

		

			// logger.debug("[after step 4   :]"+UTimeInterval.endInterval()+"'us");
			// UTimeInterval.startNewInterval();

		}
		// logger.debug("[after estimate all candidateItems]"+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");
		
		RecommendedItem[] recommendedItems=topNRecommendedItems.toArray(new RecommendedItem[topNRecommendedItems.size()]);
			
		return recommendedItems;
	

	}

}
