package AFanTi.Recommend;

import java.rmi.Remote;
import java.rmi.RemoteException;

import AFanTi.Neighborhood.Neighborhood;

public interface RecommenderProxy extends Remote{
	RecommendedItem[] makeRecommend(long userID,int num)throws RemoteException;

}
