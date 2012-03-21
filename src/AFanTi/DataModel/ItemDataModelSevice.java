package AFanTi.DataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.ylj.math.Vector;

public interface ItemDataModelSevice  extends  Remote {
	
	public boolean containItem(long itemID)throws RemoteException;
	public boolean containUser(long userID)throws RemoteException;
	
	public void setRating(long userID, long itemID, float rating)throws RemoteException;
	public Float getRating(long userID, long itemID)throws RemoteException;
	public void removeRating(long userID, long itemID)throws RemoteException;
	
	
	public Vector getItemVector(long itemID)throws RemoteException;;
	/*
	public Vector getUserVector(long userID)throws RemoteException;;
	
	public long[] getAllItemsRatedByUser(long userID)throws RemoteException;;
	public long[] getAllUsersRatedTheItem(long itemID)throws RemoteException;;
	
	public Set<Long> getAllItemIDs()throws RemoteException;;
	public Set<Long> getAllUserIDs()throws RemoteException;;
	*/
	 
}
