package AFanTi;
import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import org.apache.mahout.common.LongPair;

public class MyUserBasedRecommender extends AbstractRecommender implements
		UserBasedRecommender {

	protected MyUserBasedRecommender(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}

	public MyUserBasedRecommender(DataModel dataModel,
			UserNeighborhood neighborhood, UserSimilarity similarity) {
		super(dataModel);
		

	}

	@Override
	public float estimatePreference(long userID, long itemID) throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataModel getDataModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany, IDRescorer rescorer) 
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePreference(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPreference(long arg0, long arg1, float arg2)
			throws TasteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public long[] mostSimilarUserIDs(long arg0, int arg1) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long[] mostSimilarUserIDs(long arg0, int arg1,
			Rescorer<LongPair> arg2) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

}
