package AFanTi.Estimate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Neighborhood.ItemKNNNeighborhoodSelecter;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class RatingEstimaterServer {

	public void init(){
		try {
			Properties myProperties = new Properties();
			InputStream is = new FileInputStream(new File("AFanTi.RatingEstimaterServer.properties"));
			myProperties.load(is);
			
			
			String RMI_URL_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RMI_URL");
			System.out.println(RMI_URL_str);
			String RatingDir=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingFilesDir");
			System.out.println(RatingDir);
			String ItemNeighborhoodSelecter_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingEstimater.ItemNeighborhoodSelecter");
			System.out.println(ItemNeighborhoodSelecter_str);
			String SimilarityComputer_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingEstimater.ItemNeighborhoodSelecter.SimilarityComputer");
			System.out.println(SimilarityComputer_str);
			String RatingComputer_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingEstimater.RatingComputer");
			System.out.println(RatingComputer_str);
			String RatingEstimater_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingEstimater");
			System.out.println(RatingEstimater_str);
			String ComputeThread_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingEstimater.ComputeThread");
			System.out.println(ComputeThread_str);
			
			
			
			
			
			GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
			myDataModel.loadFromDir(RatingDir);
			
			
				
			
			/******************  SimilarityComputer *********************/
			SimilarityComputer SimilarityComputer=null;	
			if(SimilarityComputer_str.equals("CosineSimilarityComputer"))
			{
				SimilarityComputer= new CosineSimilarityComputer();
			}
			
			//defalut CosineSimilarityComputer
			if(SimilarityComputer==null)
				SimilarityComputer= new CosineSimilarityComputer();
			
			
			
			
			/****************** ItemNeighborhoodSelecter *********************/
			ItemNeighborhoodSelecter neighborhoodSelector=null;
			if(ItemNeighborhoodSelecter_str.equals("ItemKNNeighborhoodSelecter"))
			{
				
				String K_str=myProperties.getProperty("AFanTi.RatingEstimaterServer.RatingEstimater.ItemKNNeighborhoodSelecter.K");
				
				int K=Integer.parseInt(K_str);
				neighborhoodSelector = new ItemKNNNeighborhoodSelecter(
							SimilarityComputer, myDataModel, K);
			}
			
			
			//defalut ItemKNNNeighborhoodSelecter K=10
			if(neighborhoodSelector==null)
			{
				neighborhoodSelector = new ItemKNNNeighborhoodSelecter(
						SimilarityComputer, myDataModel, 10);
			}
			
			
		
			/************************** RatingComputer r*********************************/
			
			RatingComputer computer =null;
			if(RatingComputer_str.equals("GeneralRatingComputer"))
			{
				computer = new GeneralRatingComputer();
			}
			
			
			if(computer==null)
			{
				computer = new GeneralRatingComputer();
			}
			
			
			/******************* ratingEstimater *********************/
			ItemBasedRatingEstimater ratingEstimater=null;
			if(RatingEstimater_str.equals("ItemBasedRatingEstimater"))
			{
				int threads=Integer.parseInt(ComputeThread_str);
				ratingEstimater = new ItemBasedRatingEstimater(
					myDataModel, neighborhoodSelector, computer,threads,1);
			}

			
			ratingEstimater.start();
				
			Naming.rebind(RMI_URL_str, ratingEstimater);
			System.out.println("bind  ratingEstimater instance  at RMI:"+RMI_URL_str);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String args[])
	{
		PropertyConfigurator.configure("log4j.properties");
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
		RatingEstimaterServer ratingEstimaterServer=new RatingEstimaterServer();
		ratingEstimaterServer.init();
	
	}
}
