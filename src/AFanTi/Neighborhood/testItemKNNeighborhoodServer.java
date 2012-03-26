package AFanTi.Neighborhood;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.math.Arrays;
import org.ylj.math.Vector;

import AFanTi.DataModel.ItemDataModelServer;
import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.PearsonCorrelationSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemKNNeighborhoodServer {
	public static void main(String[] argvs) {
		PropertyConfigurator.configure("log4j.properties");
		
		
		GeneralItemBasedDataModel myDataModel=new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet");
		
		
		try {
			LocateRegistry.createRegistry(1099);

			System.out.println("RMI server start..");
			
			
			//bind ItemDataModelServer
			ItemDataModelServer server = new ItemDataModelServer(myDataModel);
			Naming.rebind("ItemDataModelServer", server);
			System.out.println("ItemDataModelServer rebind ok.");
			
			//bind ItemKNNeighborhoodServer
		//	SimilarityComputer SimilarityComputer =new PearsonCorrelationSimilarityComputer();
			SimilarityComputer SimilarityComputer2 =new CosineSimilarityComputer();
		    ItemKNNeighborhoodServer neighborhoodServer= new ItemKNNeighborhoodServer(SimilarityComputer2,myDataModel,10);
			Naming.rebind("ItemKNNeighborhoodServer", neighborhoodServer);	
			System.out.println("ItemKNNeighborhoodServer rebind ok.");
			
			
			System.out.println("bind  server instance  at RMI locale:1099");
			
			
			Vector itemV6=myDataModel.getItemVector(6);
			Vector itemV7=myDataModel.getItemVector(7);
			
			/*
			List<Vector> itemvList=new ArrayList<Vector>(2);
			itemvList.add(itemV6);
			itemvList.add(itemV7);
			
			
			ItemKNNeighborhoodService itemKNNeighborhoodServer = (ItemKNNeighborhoodService) Naming
			.lookup("//localhost:1099/ItemKNNeighborhoodServer");
			List<Neighborhood[]>  result=itemKNNeighborhoodServer.getNeighborhoodsOfItems(itemvList, 1);
			
			for(Neighborhood[] temphoods:result)
			{
				System.out.println(Arrays.toString(temphoods));
			}
			*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
