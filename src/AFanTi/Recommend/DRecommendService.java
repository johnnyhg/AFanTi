package AFanTi.Recommend;

import java.rmi.RemoteException;

import AFanTi.Neighborhood.Neighborhood;

public interface DRecommendService extends RecommendService{
	
	boolean setNeighborhoodsResult(Long callSerial,CallBackResult_fromNeighborhoodServer neighborhoodsResult)throws RemoteException;
	
}
