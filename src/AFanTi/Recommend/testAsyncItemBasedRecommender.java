package AFanTi.Recommend;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.Arrays;
import org.ylj.common.TimeInterval;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.EstimatedRatingReceiverProxy;
import AFanTi.Estimate.GeneralRatingComputer;
import AFanTi.Estimate.ItemBasedRatingEstimaterServer;
import AFanTi.Estimate.RatingComputer;
import AFanTi.Neighborhood.ItemKNNeighborhoodSelecter;
import AFanTi.Neighborhood.ItemKNNeighborhoodServer;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testAsyncItemBasedRecommender {

	public static void main(String[] args) throws RemoteException {

		PropertyConfigurator.configure("log4j.properties");
		GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
		//myDataModel.loadFromDir("E:\\DataSet\\testDataSet\\1M");
		
		//myDataModel.loadFromFile(new File("E:\\DataSet\\testDataSet\\1M\\1M.base"));
		myDataModel.loadFromFile(new File("E:\\DataSet\\testDataSet\\100K\\100K.base"));
		try {

		//	LocateRegistry.createRegistry(1099);
			System.out.println("RMI server start..");
			System.out.println("bind  server instance  at RMI locale:1099");

			/*
			 * start RatingEstimaterServer
			 */
			SimilarityComputer SimilarityComputer = new CosineSimilarityComputer();
			ItemNeighborhoodSelecter neighborhoodSelector = new ItemKNNeighborhoodSelecter(
					SimilarityComputer, myDataModel, 10);
			RatingComputer computer = new GeneralRatingComputer();
			ItemBasedRatingEstimaterServer ratingEstimater = new ItemBasedRatingEstimaterServer(
					myDataModel, neighborhoodSelector, computer,3);

			ratingEstimater.start();

			Naming.rebind("EstimatRatingProxy", ratingEstimater);

			String EstimatRatingProxy_RMI_PATH = "//localhost:1099/EstimatRatingProxy";
			/*
			 * start RatingRecommenderServer
			 */
		
		

			AsyncItemBasedRecommender recommender = new AsyncItemBasedRecommender(myDataModel);
			recommender.addEstimatRatingProxy(EstimatRatingProxy_RMI_PATH);
			recommender.setPartSize(50);
			recommender.start();
		
			Naming.rebind("AsyncItemBasedRecommender", recommender);
			System.out.println("AsyncRecommenderProxy rebind ok.");

			System.out.println("EstimatedRatingReceiverProxy rebind ok.");

	

			String AsyncRecommenderProxy_RMI_PATH = "//localhost:1099/AsyncItemBasedRecommender";
			String estimatedRating_result_receiver_RMI_PATH = "//localhost:1099/AsyncItemBasedRecommender";
			
			recommender.setRMI_URL(estimatedRating_result_receiver_RMI_PATH);
			
			
			AsyncRecommenderClient clientProxy = new AsyncRecommenderClient();
			Naming.rebind("AsyncRecommenditionReceiverProxy", clientProxy);
			System.out.println("AsyncClient rebind ok.");
			clientProxy
					.setAsyncRecommenderProxy(AsyncRecommenderProxy_RMI_PATH);
			String recommendition_result_receiver_RMI_PATH = "//localhost:1099/AsyncRecommenditionReceiverProxy";
			clientProxy.setRecommenderReceiveProxyRMIPath(recommendition_result_receiver_RMI_PATH);
			
			int i=TimeInterval.startNewInterval();
			RecommendedItem[] result=	clientProxy.makeRecommend(1, 10,2000);
			long time=TimeInterval.endInterval(i);
			
			System.out.println(Arrays.toString(result));
			System.out.println("cost "+time+"'ms");
			// bind ItemKNNeighborhoodServer
			// SimilarityComputer SimilarityComputer =new
			// PearsonCorrelationSimilarityComputer();


			// ratingEstimater.estimatRating(itemIDs, userID, part_K,
			// callSerial, estimatedRating_result_receiver);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
