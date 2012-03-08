package AFanTi.Test;

import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import AFanTi.DataModel.FileDataModel;
import AFanTi.DataModel.NewGenericDataModel;
import AFanTi.Neighborhood.UserNeighborhoodServer;
import AFanTi.Recommend.UserBasedRecommendServer;

public class testUserNeighborhoodServer {
	
	public static void main(String[] argvs) {
		
	FileDataModel filemodel = null;
	NewGenericDataModel model=null;
	UserSimilarity similarity = null;
	UserNeighborhood neighborhood = null;

	try {
		filemodel = new FileDataModel(new File(
				"E:\\DataSet\\ml-100k\\u1.base"));

		model=(NewGenericDataModel) filemodel.toDataModel();
		similarity = new PearsonCorrelationSimilarity(model);
		neighborhood = new NearestNUserNeighborhood(10, similarity, model);
		
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	try {
		
		LocateRegistry.createRegistry(1099);

		System.out.println("RMI server start..");
		UserNeighborhoodServer neighborhoodServer = new UserNeighborhoodServer(model);
		
		Naming.rebind("neighborhoodServer", neighborhoodServer);
		//neighborhoodServer.getNearestNUserNeighborhood("xx", 10, 1, null);
		System.out.println("bind  server instance  at RMI locale:1099");
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
