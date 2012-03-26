package AFanTi.Client;

import AFanTi.Recommend.RecommendedItem;

public interface Client {
	public boolean setRecommendItem(long callSerial,
			RecommendedItem[] result) ;	
}
