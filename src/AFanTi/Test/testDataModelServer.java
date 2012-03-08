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

import AFanTi.DataModel.DataModelServer;
import AFanTi.DataModel.DataModelSevice;
import AFanTi.DataModel.FileDataModel;
import AFanTi.DataModel.NewGenericDataModel;
import AFanTi.Recommend.UserBasedRecommendServer;

public class testDataModelServer {
	
	


	public static void main(String[] argvs) {
		
	
	FileDataModel filemodel = null;
		
	try {
		filemodel = new FileDataModel(new File(
				"E:\\DataSet\\ml-100k\\u1.base"));

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
	}
	
	DataModel model = filemodel.toDataModel();
	


	try {
		LocateRegistry.createRegistry(1099);

		System.out.println("RMI server start..");
		DataModelSevice server = new DataModelServer((NewGenericDataModel) model);

		Naming.rebind("DataModelServer", server);
		System.out.println("DataModelServer rebind ok.");
		System.out.println("bind  server instance  at RMI locale:1099");
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
