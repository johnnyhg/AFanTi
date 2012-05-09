package AFanTi.Recommend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.RatingEstimaterServer;

public class RecommenderServer {
	
	public void init(){
try {
			
			Properties myProperties = new Properties();
			InputStream is = new FileInputStream(new File("AFanTi.RecommenderServer.properties"));
			myProperties.load(is);
			
			
			String RMI_URL_str=myProperties.getProperty("AFanTi.RecommenderServer.RMI_URL");
			System.out.println(RMI_URL_str);
			String RatingDir=myProperties.getProperty("AFanTi.RecommenderServer.RatingFilesDir");
			System.out.println(RatingDir);
			String partSize_str=myProperties.getProperty("AFanTi.RecommenderServer.PART_SIZE");
			System.out.println(partSize_str);
			int partSize=Integer.parseInt(partSize_str);
			
			
			String RatingEstimaterServers_str=myProperties.getProperty("AFanTi.RecommenderServer.RatingEstimaterServers");
			System.out.println(RatingEstimaterServers_str);
			String[] RatingEstimaterServers=RatingEstimaterServers_str.split(";");
			
			
		
			
			GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
			myDataModel.loadFromDir(RatingDir);
			
			AsyncItemBasedRecommender recommender = new AsyncItemBasedRecommender(myDataModel);
			recommender.setRMI_URL(RMI_URL_str);
			recommender.setPartSize(partSize);	
			for(String RatingEstimaterServer:RatingEstimaterServers)
			{
				String RatingEstimaterServer_RMI_URL=myProperties.getProperty("AFanTi.RecommenderServer."+RatingEstimaterServer+".RMI_URL");
				recommender.addEstimatRatingProxy(RatingEstimaterServer_RMI_URL);
			}		
			recommender.start();
		
			Naming.rebind(RMI_URL_str, recommender);
			System.out.println("bind  RecommenderServer instance  at RMI:"+RMI_URL_str);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) 
	{
		
		PropertyConfigurator.configure("log4j.properties");
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
	
	
		RecommenderServer recommenderServer=new RecommenderServer();
		recommenderServer.init();
	}
}
