package AFanTi.Estimate;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EstimatRatingProxy extends Remote{
	
	public boolean estimatRating(long[] itemIDs,long userID,int part_K,long callSerial,String result_receiver)throws RemoteException;
	
}
