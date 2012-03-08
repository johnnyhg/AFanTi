package AFanTi.Recommend;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;



public interface UserBasedRecommendSevice extends Remote {
	
		
	List<RecommendedItem> localRecommend (long userID, int howMany) throws RemoteException ;
	List<RecommendedItem> globalRecommend (long userID, int howMany) throws RemoteException ;
	
}
