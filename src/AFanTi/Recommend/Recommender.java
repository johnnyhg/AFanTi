package AFanTi.Recommend;

public interface Recommender {
	RecommendedItem[] makeRecommend(long userID,int num);
	
}
