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
	long makeRecommend(long userID,int num,String ReceiverName)throws RemoteException;
	
	/*
	 * return  a clientID in RecommenderServer
	 * return -1 => register error
	 *
	 */
	
	int registerClientProxy(String client_rmi_obj)throws RemoteException;
	
	int unregisterClientProxy(String client_rmi_obj)throws RemoteException;
	
	boolean isClientProxyRegisted(String client_rmi_obj)throws RemoteException;
	
	
	
	
}
