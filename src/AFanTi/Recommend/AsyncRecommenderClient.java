package AFanTi.Recommend;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import org.apache.log4j.Logger;

import AFanTi.Estimate.EstimatRatingProxy;

public class AsyncRecommenderClient extends UnicastRemoteObject implements
		AsyncRecommenditionReceiverProxy {

	public AsyncRecommenderClient() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long clientID;
	String clientName;

	private static Logger logger = Logger
			.getLogger(AsyncRecommenderClient.class.getName());

	AsyncRecommenderProxy recommenderProxy;
	String recommenderProxyRMIPath;
	String recommenderReceiveProxyRMIPath;

	RecommendedItem[] recommendResult;
	Boolean ResultOK;

	public void setRecommenderReceiveProxyRMIPath(String path) {
		recommenderReceiveProxyRMIPath = path;
		ResultOK=false;
	}

	public boolean setAsyncRecommenderProxy(String Proxy_RMI_PATH) {

		try {

			recommenderProxy = (AsyncRecommenderProxy) Naming
					.lookup(Proxy_RMI_PATH);
			recommenderProxyRMIPath = Proxy_RMI_PATH;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			recommenderProxy = null;
			return false;
		}

	}

	@Override
	public boolean setRecommendItem(long callSerial, RecommendedItem[] result)
			throws RemoteException {

		logger.info("setRecommendItem(" + callSerial + ","
				+ Arrays.toString(result) + ")");
		recommendResult = result;
		
		synchronized (ResultOK)
		{
			ResultOK.notify();
		}
		// TODO Auto-generated method stub

		return false;
	}

	public RecommendedItem[] makeRecommend(long userID, int num,
			long maxWaitTimes) {
		
		logger.info("makeRecommend(" + userID + "," + num + ")");
		long callSerial = 0;
		try {
			callSerial = recommenderProxy.makeRecommend(userID, num,
					recommenderReceiveProxyRMIPath);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		synchronized (ResultOK) {
			try {
				ResultOK.wait(maxWaitTimes);
				if(recommendResult==null)
					return null;
				else
				{
					RecommendedItem[] temp=recommendResult;
					
					recommendResult=null;
					return temp;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		
	}

}
