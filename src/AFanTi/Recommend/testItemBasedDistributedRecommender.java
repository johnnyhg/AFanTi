package AFanTi.Recommend;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Arrays;

import org.apache.log4j.PropertyConfigurator;
import org.ylj.common.UTimeInterval;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.DataModel.ItemDataModelServer;
import AFanTi.Neighborhood.ItemKNNeighborhoodSelecter1;
import AFanTi.Neighborhood.ItemKNNeighborhoodServer;
import AFanTi.Neighborhood.ItemKNNeighborhoodService;
import AFanTi.Neighborhood.ItemKNNeighborhood_Distributed_Selecter;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemBasedDistributedRecommender {
	
	public static void main(String[] args) throws InterruptedException, MalformedURLException, RemoteException, NotBoundException
	{
		PropertyConfigurator.configure("log4j.properties");
		/*
		 * start neighborhood server
		 */
		GeneralItemBasedDataModel dataModel = new GeneralItemBasedDataModel();
		dataModel.loadFromDir("E:\\DataSet\\testDataSet");
		try {
			LocateRegistry.createRegistry(1999);

			System.out.println("RMI server start..");
			
	
			//bind ItemKNNeighborhoodServer
		//	SimilarityComputer SimilarityComputer =new PearsonCorrelationSimilarityComputer();
			SimilarityComputer SimilarityComputer2 =new CosineSimilarityComputer();
		    ItemKNNeighborhoodServer neighborhoodServer= new ItemKNNeighborhoodServer(SimilarityComputer2,dataModel,10);
			Naming.rebind("ItemKNNeighborhoodServer", neighborhoodServer);	
			System.out.println("ItemKNNeighborhoodServer rebind ok.");	
			System.out.println("bind  server instance  at RMI locale:1999");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread.sleep(1000);
		
		
		/*
		 * 
		 */
		
		ItemKNNeighborhoodService itemKNNeighborhoodServer = (ItemKNNeighborhoodService) Naming.lookup("//localhost:1099/ItemKNNeighborhoodServer");
		ItemKNNeighborhood_Distributed_Selecter neighborhoodSelector = new ItemKNNeighborhood_Distributed_Selecter(10);
		neighborhoodSelector.addRemoteNeighborhoodServer(itemKNNeighborhoodServer);

		
		Recommender recommender = new ItemBasedRecommender1(dataModel,neighborhoodSelector);
		
		UTimeInterval.startNewInterval();
		RecommendedItem[] items=recommender.makeRecommend(1, 10);
		System.out.println(UTimeInterval.endInterval()+"'us");
		System.out.print(Arrays.toString(items));
	}
}
