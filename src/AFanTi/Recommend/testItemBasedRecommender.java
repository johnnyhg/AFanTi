package AFanTi.Recommend;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.Arrays;
import org.ylj.common.TimeInterval;
import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Neighborhood.ItemKNNeighborhoodSelecter1;
import AFanTi.Neighborhood.Neighborhood;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.CosineSimilarityComputer2;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemBasedRecommender {
	public static long timeBegin;

	public static void main(String[] args) {

		GeneralItemBasedDataModel dataModel = new GeneralItemBasedDataModel();
		dataModel.loadFromDir("E:\\DataSet\\testDataSet");
		long begin = System.currentTimeMillis();

		PropertyConfigurator.configure("log4j.properties");

		System.out.println(begin);
		SimilarityComputer SimilarityComputer = new CosineSimilarityComputer();

		ItemKNNeighborhoodSelecter1 neighborhoodSelector = new ItemKNNeighborhoodSelecter1(
				SimilarityComputer, dataModel, 10);

		Recommender ecommender = new ItemBasedRecommender1(dataModel,
				neighborhoodSelector);
		
		Recommender ecommender2 = new ItemBasedRecommender(dataModel,
				neighborhoodSelector);

		Vector itemV6 = dataModel.getItemVector(6);
		// System.out.println(itemV6);
		// System.out.println("6,1");
		// Neighborhood[]
		// neighborhoods=neighborhoodSelector.getNeighborhoodsOfItem(itemV6, 1);
		// System.out.println(Arrays.toString(neighborhoods));
		UTimeInterval.startNewInterval();
		int loop = 1;
		for (int i = 0; i < loop; i++) {

			RecommendedItem[] items = ecommender.makeRecommend(100, 12);
			if (items != null)
				System.out.println(Arrays.toString(items));
		}
		System.out.println("# makeRecommend() cost:"
				+ UTimeInterval.endInterval() / loop + "us");

		int j = UTimeInterval.startNewInterval();
		for (int i = 0; i < loop; i++) {
			RecommendedItem[] items = ecommender2.makeRecommend(100,12);
			if (items != null)
				System.out.println(Arrays.toString(items));
		}
		System.out.println("# makeRecommend() cost:"
				+ UTimeInterval.endInterval(j) / loop + "us");

		//
	}
}
