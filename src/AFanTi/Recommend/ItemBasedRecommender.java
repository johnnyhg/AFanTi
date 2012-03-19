package AFanTi.Recommend;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.mahout.math.Arrays;
import org.ylj.common.UTimeInterval;
import org.ylj.math.TopN;
import org.ylj.math.Vector;

import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.Estimate.GeneralRatingEstimater;
import AFanTi.Estimate.RatingEstimater;
import AFanTi.Neighborhood.Neighborhood;
import AFanTi.Neighborhood.NeighborhoodSelecter;

public class ItemBasedRecommender implements Recommender {
	
	ItemBasedDataModel itemBasedDataModel;
	NeighborhoodSelecter neighborhoodSelecter;
	RatingEstimater ratingEstimater;
	
	private static Logger logger = Logger.getLogger(ItemBasedDataModel.class.getName());
	
	public ItemBasedRecommender(ItemBasedDataModel dataModel,NeighborhoodSelecter nelecter)
	{
		itemBasedDataModel=dataModel;
		neighborhoodSelecter=nelecter;
		ratingEstimater=new GeneralRatingEstimater();
		
		
	}
	@Override
	public RecommendedItem[] makeRecommend(long userID,int num) {
		
		
		
		UTimeInterval.startNewInterval();
		// TODO Auto-generated method stub
		if(!itemBasedDataModel.containUser(userID))
			return null;
		long[] ratingedItems=itemBasedDataModel.getAllItemsRatedByUser(userID);
		Set<Long> allItems=itemBasedDataModel.getAllItemIDs();

    	
		//do filtrate 
		for(long ratingedItem:ratingedItems)
		{
			allItems.remove(ratingedItem);
		}

		
		Set<Long> candidateItems=allItems;
		TopN<RecommendedItem> topNRecommendedItems=new TopN<RecommendedItem>(num,new RecommendedItemComparator());
	
		logger.debug("[after get all candidateItems:"+candidateItems.size()+"]"+UTimeInterval.endInterval()+"'us");
		
		int j=0;
		for(Long itemID:candidateItems)
		{
			UTimeInterval.startNewInterval();
			++j;
			UTimeInterval.startNewInterval();
			//**********  step 1 :get all neighborhoods of itemID
			
			Vector itemV=itemBasedDataModel.getItemVector(itemID);
		
			Neighborhood[] neighborhoods=neighborhoodSelecter.getNeighborhoodsOfItem(itemV, userID);	
			
			logger.debug("[after step 1 getNeighborhoodsOfItem  :]"+UTimeInterval.endInterval()+"'us");
			UTimeInterval.startNewInterval();
			
			
			//**********  step 2  :get  get All neighborhood's Rating and similarity
			float[] ratingArrary=new float[neighborhoods.length];
			double[] similarityArrary=new double[neighborhoods.length];
			
			for(int i=0;i<neighborhoods.length;i++)
			{
				ratingArrary[i]=itemBasedDataModel.getRating(userID, neighborhoods[i].vector.getVectorID());
				similarityArrary[i]=neighborhoods[i].similarity;
			}
			
			//logger.debug("#step 2  [after getAllneighborhoodsRating()] "+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");
			//testItemBasedRecommender.timeBegin=System.currentTimeMillis();
			
			logger.debug("[after step 2  getAllneighborhoodsRating :]"+UTimeInterval.endInterval()+"'us");
			UTimeInterval.startNewInterval();
			
			//**********  step 3  :estimate Rating of user to the item
			RecommendedItem candidateItem=new RecommendedItem(itemID,ratingEstimater.estimateRating(ratingArrary, similarityArrary));
			//logger.debug("#step 3  [after estimateRating()] "+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");
			//testItemBasedRecommender.timeBegin=System.currentTimeMillis();
			logger.debug("[after step 3  estimateRating :]"+UTimeInterval.endInterval()+"'us");
			UTimeInterval.startNewInterval();
			
			
			//**********  step 4  :after put int topNRecommendedItem
			topNRecommendedItems.put(candidateItem);
			
			logger.debug("[after step 4   :]"+UTimeInterval.endInterval()+"'us");
			UTimeInterval.startNewInterval();
	
			
			//logger.debug("#step 4  [[after put int topNRecommendedItems()] "+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");
			//testItemBasedRecommender.timeBegin=System.currentTimeMillis();
			
			
			//logger.debug("***********  ("+j+"/"+candidateItems.size()+")"+" itemID:"+itemID);
			//logger.debug("  Estimater:"+ratingEstimater.estimateRating(ratingArrary, similarityArrary)+" ************");
			
		}
		//logger.debug("[after estimate all candidateItems]"+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");
		return topNRecommendedItems.toArrary(new RecommendedItem[topNRecommendedItems.getLength()] );
		

	}
	
}
