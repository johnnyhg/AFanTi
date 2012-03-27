package AFanTi.Recommend;

import java.rmi.RemoteException;

public interface AsyncRecommenditionReceiverProxy {
	public boolean setRecommendItem(long callSerial,
			RecommendedItem[] result) throws RemoteException ;	
}
