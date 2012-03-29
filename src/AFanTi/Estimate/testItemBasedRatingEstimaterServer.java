package AFanTi.Estimate;

import java.rmi.RemoteException;

import org.apache.log4j.PropertyConfigurator;
import org.ylj.math.Vector;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Neighborhood.ItemKNNeighborhoodSelecter;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.PearsonCorrelationSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemBasedRatingEstimaterServer {
	
	public static void main(String[] args)
	{

		PropertyConfigurator.configure("log4j.properties");

		GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet\\1M");
		
		
		SimilarityComputer SimilarityComputer =new CosineSimilarityComputer();
		ItemNeighborhoodSelecter neighborhoodSelector = new ItemKNNeighborhoodSelecter(SimilarityComputer,myDataModel,10);
		
		
		RatingComputer computer=new GeneralRatingComputer();
		
		
		ItemBasedRatingEstimaterServer ratingEstimater=null;
		try {
			ratingEstimater = new ItemBasedRatingEstimaterServer(myDataModel,neighborhoodSelector,computer,1,1);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ratingEstimater.start();
		long[] itemIDs={1,2,3,4,5,6};
		long userID=1;
		int part_K=1;
		long callSerial=1;
		String result_receiver="xxx";
		ratingEstimater.estimatRating(itemIDs, userID, part_K, callSerial, result_receiver);
		
		//SimilarityComputer SimilarityComputer =new CosineSimilarityComputer();
	
		//System.out.println(ratingEstimater.estimatRating(973, 2));
		//System.out.println(ratingEstimater.estimatRating(366, 2));
		//System.out.println(ratingEstimater.estimatRating(851, 2));
		//System.out.println(ratingEstimater.estimatRating(482, 2));
		
		
	}
}
