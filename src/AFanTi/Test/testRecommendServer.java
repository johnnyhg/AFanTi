package AFanTi.Test;

import java.io.File;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import AFanTi.DataModel.FileDataModel;
import AFanTi.Recommend.UserBasedRecommendServer;

public class testRecommendServer {
	public static void doRecommend() {

		UserBasedRecommendServer server;
		try {

			FileDataModel filemodel = null;

			try {
				filemodel = new FileDataModel(new File(
						"E:\\DataSet\\ml-100k\\u1.base"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DataModel model = filemodel.toDataModel();

			UserSimilarity similarity = null;
			UserNeighborhood neighborhood = null;

			try {
				similarity = new PearsonCorrelationSimilarity(model);

				neighborhood = new NearestNUserNeighborhood(10, similarity,
						model);

			} catch (TasteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			server = new UserBasedRecommendServer(model, similarity, neighborhood);

			List<RecommendedItem> recommendations = server.localRecommend(1, 10);
			if (recommendations != null) {
				System.out.println("output: recommendations.size = "
						+ recommendations.size());

				for (RecommendedItem recommendation : recommendations) {
					System.out.println(recommendation);
				}
			} else {
				System.out.println("output: recommendations = null");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] argvs) {

		FileDataModel filemodel = null;
		UserSimilarity similarity = null;
		UserNeighborhood neighborhood = null;
		try {
			filemodel = new FileDataModel(new File(
					"E:\\DataSet\\ml-100k\\u1.base"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataModel model = filemodel.toDataModel();
		
		try {
			similarity = new PearsonCorrelationSimilarity(model);

			neighborhood = new NearestNUserNeighborhood(10, similarity, model);

		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			LocateRegistry.createRegistry(1099);

			System.out.println("RMI server start..");
			UserBasedRecommendServer server = new UserBasedRecommendServer(model, similarity,
					neighborhood);

			Naming.rebind("RecommendServer", server);
			System.out.println("bind  server instance  at RMI locale:1099");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
