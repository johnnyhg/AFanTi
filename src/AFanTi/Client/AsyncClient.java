package AFanTi.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import AFanTi.Recommend.RecommendedItem;

public interface AsyncClient  extends Remote{
	public boolean setRecommendItem(long callSerial,
			RecommendedItem[] result)  throws RemoteException ;
	
}
