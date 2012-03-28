package AFanTi.Recommend;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AsyncRecommenditionReceiverProxy extends Remote{
	public boolean setRecommendItem(long callSerial,
			RecommendedItem[] result) throws RemoteException ;	
}
