package AFanTi;
import java.io.File;
import java.util.List;


import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import  AFanTi.DataModel.FileDataModel;

public class testMyUserBasedRecommender {

	public static void main(String[] args) throws Exception {

		long time_call_cost = 0;
		long bulid_model_cost = 0;
		long make_recommendtion_cost = 0;

		/*
		 * if (args.length != 3) {
		 * System.out.println("input(3): rating_file  userId  recommend_n");
		 * 
		 * return; }
		 * 
		 * System.out.println("args.length=" + args.length); int userId =
		 * Integer.parseInt(args[1]);
		 * 
		 * int recommend_n = Integer.parseInt(args[2]);
		 */
		int userId = 1;
		int recommend_n = 1000;

		FileDataModel filemodel = new FileDataModel(new File(
				"E:\\DataSet\\ml-100k\\u1.base"));
		
		DataModel model=filemodel.toDataModel();
		
		System.out.println(model.getClass());
	
		
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(10,
				similarity, model);
		
		
		// test gettime

		System.out.println("***** juts test time function cost   ********");

		long lastTime = System.currentTimeMillis();
		long curTime = System.currentTimeMillis();
		time_call_cost = curTime - lastTime;
		System.out.println(curTime);
		System.out.println(lastTime);
		System.out.println("cost=" + time_call_cost + "'ms");

		System.out.println("***** test time function end  ********\n");

		System.out.println("***** bulid model *******");

		lastTime = System.currentTimeMillis();
		
		
		
		Recommender recommender = new GenericUserBasedRecommender(model,
				neighborhood, similarity);
	
		curTime = System.currentTimeMillis();

		// int YY = Cld.get(Calendar.YEAR) ;
		// int MM = Cld.get(Calendar.MONTH)+1;
		// int DD = Cld.get(Calendar.DATE);

		System.out.println(curTime);
		System.out.println(lastTime);
		bulid_model_cost = curTime - lastTime;

		System.out.println("cost=" + bulid_model_cost + "'ms");

		System.out.println("***** bulid model  end*******\n");

		System.out.println("***** make  recommand  *******");

		System.out.println("user_id=" + userId + "   " + "recommend_n="
				+ recommend_n);

		lastTime = System.currentTimeMillis();
		
		
		
		List<RecommendedItem> recommendations = recommender.recommend(userId,
				recommend_n);
		curTime = System.currentTimeMillis();

		
		
		// int YY = Cld.get(Calendar.YEAR) ;
		// int MM = Cld.get(Calendar.MONTH)+1;
		// int DD = Cld.get(Calendar.DATE);

		make_recommendtion_cost = curTime - lastTime;
		System.out.println("cost=" + make_recommendtion_cost + "'ms");

		if (recommendations != null) {
			System.out.println("output: recommendations.size = "
					+ recommendations.size());

			for (RecommendedItem recommendation : recommendations) {
				System.out.println(recommendation);
			}
		}
		else
		{
			System.out.println("output: recommendations = null");
		}
		
		model.setPreference(1L, 678L,  (float) 3.0);
		recommendations = recommender.recommend(userId,
				recommend_n);
		if (recommendations != null) {
			System.out.println("output: recommendations.size = "
					+ recommendations.size());

			for (RecommendedItem recommendation : recommendations) {
				System.out.println(recommendation);
			}
		}
		else
		{
			System.out.println("output: recommendations = null");
		}
		
		System.out.println("***** make  recommand  end*******");

		System.out.println("***** cost list *******");
		System.out.println("users=" + model.getNumUsers());
		System.out.println("items=" + model.getNumItems());

		System.out.println("time_call_cost=" + time_call_cost + "'ms");
		System.out.println("bulid_model_cost=" + bulid_model_cost + "'ms");

		System.out.println("make_recommendtion_cost=" + make_recommendtion_cost
				+ "'ms");

	}
}
