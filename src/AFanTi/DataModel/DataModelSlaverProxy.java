package AFanTi.DataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataModelSlaverProxy extends Remote{
	
	//Async inter face;
		public boolean setRating(long userID, long itemID, float rating,long command_ID,int resultIndex,String MASTER_RMI_URL)throws RemoteException;

	public boolean removeRating(long userID, long itemID,long command_ID,int resultIndex,String MASTER_RMI_URL)throws RemoteException;

	
	/*
	public boolean prepareSetRating(long userID, long itemID, float rating,long command_ID,int resultIndex,String MASTER_RMI_URL)throws RemoteException;

	public boolean prepareRemoveRating(long userID, long itemID,long command_ID,int resultIndex,String MASTER_RMI_URL)throws RemoteException;	
	
	public void commitCommand(long command_ID);
	*/

	public boolean getRating(long userID, long itemID,long command_ID,int resultIndex,String MASTER_RMI_URL)throws RemoteException;
	
}