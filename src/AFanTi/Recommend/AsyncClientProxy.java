package AFanTi.Recommend;

import AFanTi.Asynchronous.RecommendCallBack;

public interface AsyncClientProxy {
	
	public boolean setRecommendItem(long callSerial,
			RecommendedItem[] result) ;	
}
