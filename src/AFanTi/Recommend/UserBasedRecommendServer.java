package AFanTi.Recommend;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import AFanTi.DataModel.FileDataModel;
import AFanTi.Neighborhood.ItemsWithSimilarity;
import AFanTi.Neighborhood.UserNeighborhoodSevice;
import AFanTi.Neighborhood.UsersWithSimilarity;

public class UserBasedRecommendServer extends UnicastRemoteObject implements
		UserBasedRecommendSevice {

	Recommender recommendEngine;

	public UserBasedRecommendServer(DataModel model, UserSimilarity similarity,
			UserNeighborhood neighborhood) throws RemoteException {

		recommendEngine=new GenericUserBasedRecommender(model,neighborhood,similarity);

		System.out.println(model.getClass());

	

	}

	@Override
	public List<RecommendedItem> localRecommend(long userID, int howMany)
			throws RemoteException {
		
		System.out.println("calls  recommend("+userID+","+  howMany+")");
		if (userID < 0 || howMany < 0)
			return null;
		
		try {
			
			List<RecommendedItem> recommendations = recommendEngine.recommend(
					userID, howMany);
			
		
			return recommendations;

		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<RecommendedItem> globalRecommend(long userID, int howMany)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
