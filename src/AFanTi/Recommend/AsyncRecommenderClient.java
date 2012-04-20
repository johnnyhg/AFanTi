package AFanTi.Recommend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;

import AFanTi.Estimate.EstimatRatingProxy;

public class AsyncRecommenderClient extends UnicastRemoteObject implements
		AsyncRecommenditionReceiverProxy {

	public AsyncRecommenderClient() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean init() throws IOException
	{
		Properties myProperties = new Properties();
		InputStream is = new FileInputStream(new File("AFanTi.RecommenderClient.properties"));
		myProperties.load(is);
		
		
		String RMI_URL_str=myProperties.getProperty("AFanTi.RecommenderClient.RMI_URL");
		System.out.println(RMI_URL_str);
		String RecommenderServer_RMI_URL=myProperties.getProperty("AFanTi.RecommenderClient.RecommenderServer.RMI_URL");
		System.out.println(RecommenderServer_RMI_URL);

		Naming.rebind(RMI_URL_str, this);
		System.out.println("bind  recommenderClient instance  at RMI:"+RMI_URL_str);
		System.out.println("RecommenderServer_RMI_URL="+RecommenderServer_RMI_URL);
		setAsyncRecommenderProxy(RecommenderServer_RMI_URL);
		this.setRecommenderReceiveProxyRMIPath(RMI_URL_str);
		
		return true;
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
			long maxWaitMillTimes) {
		
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
				ResultOK.wait(maxWaitMillTimes);
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
