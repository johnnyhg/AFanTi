package AFanTi.Runner;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import AFanTi.DataModel.FileDataModel;
import AFanTi.Recommend.UserBasedRecommendServer;

public class testMahoutItembasedRecommender {
	
	public static void main(String[] args) throws TasteException, RemoteException
	{
		FileDataModel filemodel=null;
		try {
			filemodel = new FileDataModel(new File(
					"E:\\DataSet\\ml-100k\\u1.base"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeBegin=0;
		System.out.println(timeBegin=System.currentTimeMillis());
		DataModel model = filemodel.toDataModel();
		ItemSimilarity similarity = new UncenteredCosineSimilarity(model);

		Recommender recommendEngine=new GenericItemBasedRecommender(model,similarity);
		
		List<RecommendedItem> recommendedItems= recommendEngine.recommend(1, 10);
		for(RecommendedItem item:recommendedItems)
		{
			System.out.println("("+item.getItemID()+","+item.getValue()+")");
		}

		System.out.println(System.currentTimeMillis()-timeBegin+"ms");
	}
}
