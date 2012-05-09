package AFanTi.Recommend;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;

import org.apache.log4j.PropertyConfigurator;

import AFanTi.Estimate.RatingEstimaterServer;

public class testAsyncRecommenderClient {

	public  static void main(String[] args) 
	{
		PropertyConfigurator.configure("log4j.properties");
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RatingEstimaterServer ratingEstimaterServer=new RatingEstimaterServer();
		ratingEstimaterServer.init();
	
		RecommenderServer recommenderServer=new RecommenderServer();
		recommenderServer.init();
		
		
		AsyncRecommenderClient client=null;
		try {
			client = new AsyncRecommenderClient();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			client.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RecommendedItem[] items=client.makeRecommend(3, 10, 10000);
		System.out.println(Arrays.toString(items));
		
	}
	
}
