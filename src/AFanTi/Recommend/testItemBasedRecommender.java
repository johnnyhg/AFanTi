package AFanTi.Recommend;

import java.io.File;
import java.rmi.RemoteException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.Arrays;
import org.ylj.common.TimeInterval;
import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Neighborhood.ItemKNNNeighborhoodSelecter;
import AFanTi.Neighborhood.ItemKNNNeighborhoodSelecter;
import AFanTi.Neighborhood.Neighborhood;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.CosineSimilarityComputer2;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemBasedRecommender {
	public static long timeBegin;

	public static void main(String[] args) {
		
		PropertyConfigurator.configure("log4j.properties");
		
		GeneralItemBasedDataModel dataModel = new GeneralItemBasedDataModel();
		dataModel.loadFromDir("E:\\DataSet\\testDataSet\\1M");
	

		

		//System.out.println(begin);
		SimilarityComputer SimilarityComputer = new CosineSimilarityComputer();

		ItemKNNNeighborhoodSelecter neighborhoodSelector = new ItemKNNNeighborhoodSelecter(
				SimilarityComputer, dataModel, 10);
		/*
		Recommender ecommender = new ItemBasedRecommender1(dataModel,
				neighborhoodSelector);
		*/
		Recommender ecommender2=null;
		
			ecommender2 = new ItemBasedRecommender(dataModel,
					neighborhoodSelector);
		

		Vector itemV6 = dataModel.getItemVector(6);
		System.out.println(itemV6);
		 System.out.println("6,1");
		// Neighborhood[]
		// neighborhoods=neighborhoodSelector.getNeighborhoodsOfItem(itemV6, 1);
		// System.out.println(Arrays.toString(neighborhoods));
		
		int loop = 1;
		/*
		 * UTimeInterval.startNewInterval();
		for (int i = 0; i < loop; i++) {

			RecommendedItem[] items = ecommender.makeRecommend(10, 12);
			if (items != null)
				System.out.println(Arrays.toString(items));
		}
		System.out.println("# makeRecommend() cost:"
				+ UTimeInterval.endInterval() / loop + "us");
		*/
		int j = UTimeInterval.startNewInterval();
		for (int i = 0; i < loop; i++) {
			System.out.println("# makeRecommend("+2+") ");
			RecommendedItem[] items = ecommender2.makeRecommend(1,10);
			if (items != null)
				System.out.println(Arrays.toString(items));
		}
		
		System.out.println("# makeRecommend() cost:"
				+ UTimeInterval.endInterval(j) / loop + "us");

		//
	}
}
