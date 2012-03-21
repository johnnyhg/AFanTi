package AFanTi.Recommend;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RecommendService extends Remote{
	RecommendedItem[] makeRecommendFromLocalData(long userID,int num)throws RemoteException;
	RecommendedItem[] makeRecommendFromGlobalData(long userID,int num)throws RemoteException;
}
