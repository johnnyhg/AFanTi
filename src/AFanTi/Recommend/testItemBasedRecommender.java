package AFanTi.Recommend;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.Arrays;
import org.ylj.common.TimeInterval;
import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.Neighborhood.KNNeighborhoodSelecter;
import AFanTi.Neighborhood.Neighborhood;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.CosineSimilarityComputer2;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testItemBasedRecommender {
	public static long timeBegin;

	public static void main(String[] args) {

		ItemBasedDataModel dataModel = new ItemBasedDataModel();
		dataModel.loadFromDir("E:\\DataSet\\testDataSet");
		long begin = System.currentTimeMillis();

		PropertyConfigurator.configure("log4j.properties");

		System.out.println(begin);
		SimilarityComputer SimilarityComputer = new CosineSimilarityComputer();

		KNNeighborhoodSelecter neighborhoodSelector = new KNNeighborhoodSelecter(
				SimilarityComputer, dataModel, 10);

		Recommender ecommender = new ItemBasedRecommender(dataModel,
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
			RecommendedItem[] items = ecommender.makeRecommend(1, 10);

			System.out.println(Arrays.toString(items));
		}

		System.out.println("# makeRecommend() cost:"
				+ UTimeInterval.endInterval()/loop + "us");

		//
	}
}
