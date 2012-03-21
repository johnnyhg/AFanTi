package AFanTi.Recommend;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.PropertyConfigurator;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.DataModel.ItemDataModelServer;
import AFanTi.Neighborhood.ItemKNNeighborhoodSelecter1;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemBasedRecommenderServer {
	public static void main(String[] args)
	{
		PropertyConfigurator.configure("log4j.properties");
		
		
		GeneralItemBasedDataModel myDataModel=new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet");
		SimilarityComputer SimilarityComputer = new CosineSimilarityComputer();
		ItemKNNeighborhoodSelecter1 neighborhoodSelector = new ItemKNNeighborhoodSelecter1(
				SimilarityComputer, myDataModel, 10);
		
		try {
			LocateRegistry.createRegistry(1099);

			System.out.println("RMI server start..");
			ItemBasedRecommenderServer server = new ItemBasedRecommenderServer(myDataModel,neighborhoodSelector);

			Naming.rebind("ItemBasedRecommenderServer", server);
			System.out.println("ItemBasedRecommenderServer rebind ok.");
			System.out.println("bind  server instance  at RMI locale:1099");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
