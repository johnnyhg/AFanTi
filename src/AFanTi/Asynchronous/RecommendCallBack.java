package AFanTi.Asynchronous;
import AFanTi.Recommend.RecommendedItem;


public interface RecommendCallBack {
	
	public boolean setRecommendCallBack(long callSerial,RecommendedItem[] result);
}
