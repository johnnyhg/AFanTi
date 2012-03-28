package AFanTi.Estimate;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.ylj.math.Vector;

public interface EstimatedRatingReceiverProxy extends Remote{
	
	
	public void setEstimatedRating(long[]  itemIDs,float[] ratings,int part_K,long callSerial)throws RemoteException;
	
}
