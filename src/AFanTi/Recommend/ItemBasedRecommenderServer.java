package AFanTi.Recommend;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.ylj.common.UTimeInterval;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;

public class ItemBasedRecommenderServer extends UnicastRemoteObject implements RecommendService {
	
	private static final long serialVersionUID = 7993001819051426852L;
	
	private static Logger logger = Logger.getLogger(ItemBasedRecommenderServer.class.getName());
	
	
	ItemBasedRecommender itemBasedRecommender;
	protected ItemBasedRecommenderServer(GeneralItemBasedDataModel dataModel,ItemNeighborhoodSelecter nselecter) throws RemoteException {
		itemBasedRecommender=new ItemBasedRecommender(dataModel,nselecter);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	

	@Override
	public RecommendedItem[] makeRecommendFromLocalData(long userID, int num)
			throws RemoteException {
		int beginTime=UTimeInterval.startNewInterval();
		
		logger.info("Call makeRecommend("+userID+","+num+")");
		RecommendedItem[] recommendedItems=itemBasedRecommender.makeRecommend(userID, num);
		
		String recommendedItems_str;
		if(recommendedItems==null)
			recommendedItems_str="null";
		else
			recommendedItems_str=Arrays.toString(recommendedItems);
		if(beginTime>=0)
			
			logger.info("makeRecommend  complete,cost "+ UTimeInterval.endInterval(beginTime)+"'us,"+"RecommendItem:"+recommendedItems_str);
		else
			logger.info("makeRecommend  complete,cost null us(UTimeInterval Error),"+"RecommendItem:"+recommendedItems_str);
	
		// TODO Auto-generated method stub
		return recommendedItems;
	}

	@Override
	public RecommendedItem[] makeRecommendFromGlobalData(long userID, int num)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
