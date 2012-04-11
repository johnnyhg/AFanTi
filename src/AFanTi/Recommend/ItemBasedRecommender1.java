package AFanTi.Recommend;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.mahout.math.Arrays;
import org.ylj.common.UTimeInterval;
import org.ylj.math.TopN;
import org.ylj.math.Vector;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.GeneralRatingComputer;
import AFanTi.Estimate.RatingComputer;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Neighborhood.Neighborhood;


public class ItemBasedRecommender1 implements Recommender {
	
	GeneralItemBasedDataModel itemBasedDataModel;
	ItemNeighborhoodSelecter neighborhoodSelecter;
	RatingComputer ratingEstimater;
	
	private static Logger logger = Logger.getLogger(GeneralItemBasedDataModel.class.getName());
	
	public ItemBasedRecommender1(GeneralItemBasedDataModel dataModel,ItemNeighborhoodSelecter nelecter)
	{
		itemBasedDataModel=dataModel;
		neighborhoodSelecter=nelecter;
		ratingEstimater=new GeneralRatingComputer();
		
		
	}
	@Override
	public RecommendedItem[] makeRecommend(long userID,int num) {
		
		
		
		//UTimeInterval.startNewInterval();
		// TODO Auto-generated method stub
		if(!itemBasedDataModel.containUser(userID)||num<=0)
			return null;
		
		Set<Long> allItems=itemBasedDataModel.getAllItemIDs();
		long[] ratingedItems=itemBasedDataModel.getAllItemsRatedByUser(userID);
		
		logger.info("ItemBasedRecommender1[all allItems:"+allItems.size()+"]");
		logger.info("ItemBasedRecommender1[all ratingedItems:"+ratingedItems.length+"]");
		//do filtrate 
		
		for(long ratingedItem:ratingedItems)
		{
			
			allItems.remove(ratingedItem);
		}
	
		Set<Long> candidateItems=allItems;
		TopN<RecommendedItem> topNRecommendedItems=new TopN<RecommendedItem>(num,new RecommendedItemComparator());
		logger.info("ItemBasedRecommender1[all candidateItems:"+candidateItems.size()+"]");
		
	//	logger.debug("[after get all candidateItems:"+candidateItems.size()+"]"+UTimeInterval.endInterval()+"'us");
	
		int j=0;
		for(Long itemID:candidateItems)
		{
		//	UTimeInterval.startNewInterval();
			++j;
		//	UTimeInterval.startNewInterval();
			//**********  step 1 :get all neighborhoods of itemID
			
			Vector itemV=itemBasedDataModel.getItemVector(itemID);
		
			Neighborhood[] neighborhoods=neighborhoodSelecter.getNeighborhoodsOfItem(itemV, userID);	
			
		//	logger.debug("[after step 1 getNeighborhoodsOfItem  :]"+UTimeInterval.endInterval()+"'us");
		//	UTimeInterval.startNewInterval();
			
			
			//**********  step 2  :get  get All neighborhood's Rating and similarity
			float[] ratingArrary=new float[neighborhoods.length];
			double[] similarityArrary=new double[neighborhoods.length];
			
			for(int i=0;i<neighborhoods.length;i++)
			{
				ratingArrary[i]=itemBasedDataModel.getRating(userID, neighborhoods[i].vector.getVectorID());
				similarityArrary[i]=neighborhoods[i].similarity;
			}
			
			//logger.debug("#step 2  [after getAllneighborhoodsRating()] "+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");

	
			
			//**********  step 3  :estimate Rating of user to the item
			RecommendedItem candidateItem=new RecommendedItem(itemID,ratingEstimater.computeRating(ratingArrary, similarityArrary));
		
			
			
			//**********  step 4  :after put int topNRecommendedItem
			
			topNRecommendedItems.put(candidateItem);
			
		//	logger.debug("[after step 4   :]"+UTimeInterval.endInterval()+"'us");
		//	UTimeInterval.startNewInterval();
	
			
		
		}
		//logger.debug("[after estimate all candidateItems]"+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");
		return topNRecommendedItems.toArrary(new RecommendedItem[topNRecommendedItems.getLength()] );
		

	}
	
}
