package AFanTi;
import java.util.List;



public interface RecommenderEngine {
	
	 List<Item> makeRecommend(long userID, int howMany) ;
	 
	 float estimatePreference(long userID, long itemID) ;
	 
	 
	
}
