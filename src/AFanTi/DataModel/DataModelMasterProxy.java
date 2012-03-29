package AFanTi.DataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataModelMasterProxy extends Remote{
	
	
	//for client
	public boolean addDataModelSlaver(DataModelSlaverProxy slaver)throws RemoteException;
	public boolean removeDataModelSlaver(DataModelSlaverProxy slaver)throws RemoteException;
	

	public boolean setRating(long userID, long itemID, float rating)throws RemoteException;
	public boolean removeRating(long userID, long itemID)throws RemoteException;
	public boolean getRating(long userID, long itemID)throws RemoteException;
	
	//
	public boolean setCommandResult(long commandID,int resultIndex,boolean state)throws RemoteException;
	public boolean setCommandResult(long commandID,int resultIndex,float value)throws RemoteException;
	
}
