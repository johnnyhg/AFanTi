package AFanTi.Recommend;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AsyncRecommenderProxy extends Remote{
	
	
	/*
	 * for client 
	 */
	/**
	 * return -1 ReceiverName error,cannot find the Receiver
	 */
	long makeRecommend(long userID,int num,String ReceiverRMI_URL)throws RemoteException;
	
	/*
	 * return  a clientID in RecommenderServer
	 * return -1 => register error
	 *
	 */
	
	
	
	
}
