package AFanTi.Recommend;

import java.rmi.RemoteException;

import AFanTi.Asynchronous.RecommendCallBack;
import AFanTi.Client.AsyncClient;

public class GeneralAsyncClientProxy implements AsyncClientProxy{

	 long clientID;
	 String clientName;
	 
	 AsyncClient remoteCilent;
	 
	@Override
	public boolean setRecommendItem(long callSerial,
			RecommendedItem[] result) {
		// TODO Auto-generated method stub
		try {
			remoteCilent.setRecommendItem(callSerial, result);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	

}
