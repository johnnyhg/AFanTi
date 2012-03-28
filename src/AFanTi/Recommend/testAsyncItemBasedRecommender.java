package AFanTi.Recommend;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.PropertyConfigurator;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.GeneralRatingComputer;
import AFanTi.Estimate.ItemBasedRatingEstimaterServer;
import AFanTi.Estimate.RatingComputer;
import AFanTi.Neighborhood.ItemKNNeighborhoodSelecter;
import AFanTi.Neighborhood.ItemKNNeighborhoodServer;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testAsyncItemBasedRecommender {
	
	public static void main(String[] args) throws RemoteException
	{
		
		
		PropertyConfigurator.configure("log4j.properties");
		GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet\\1M");
		
		
		try {
			
			
			LocateRegistry.createRegistry(1099);
			System.out.println("RMI server start..");
			System.out.println("bind  server instance  at RMI locale:1099");
			
			AsyncItemBasedRecommender recommender=new AsyncItemBasedRecommender();
			Naming.rebind("AsyncItemBasedRecommender", recommender);
			System.out.println("AsyncItemBasedRecommender rebind ok.");	
			
			
			//bind ItemKNNeighborhoodServer
		//	SimilarityComputer SimilarityComputer =new PearsonCorrelationSimilarityComputer();

		    
		    SimilarityComputer SimilarityComputer =new CosineSimilarityComputer();
			ItemNeighborhoodSelecter neighborhoodSelector = new ItemKNNeighborhoodSelecter(SimilarityComputer,myDataModel,10);
			RatingComputer computer=new GeneralRatingComputer();
			ItemBasedRatingEstimaterServer ratingEstimater=new ItemBasedRatingEstimaterServer(myDataModel,neighborhoodSelector,computer);
	
			ratingEstimater.start();
			
			
			
			long[] itemIDs={1,2,3,4,5,6};
			long userID=1;
			int part_K=1;
			long callSerial=1;
			String result_receiver="//localhost:1099/AsyncItemBasedRecommender";
			
			ratingEstimater.estimatRating(itemIDs, userID, part_K, callSerial, result_receiver);
			
		    
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
