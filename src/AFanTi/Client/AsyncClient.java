package AFanTi.Client;

import java.rmi.RemoteException;

import AFanTi.Recommend.AsyncRecommenditionReceiverProxy;
import AFanTi.Recommend.RecommendedItem;

public class AsyncClient implements AsyncRecommenditionReceiverProxy{

	 long clientID;
	 String clientName;
	@Override
	public boolean setRecommendItem(long callSerial, RecommendedItem[] result)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
	 


	

}
