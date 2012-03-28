package AFanTi.Recommend;

import java.io.Serializable;

public class RecommendedItem implements Serializable{
	/** @return the recommended item ID */
	public long itemID;
	public float estRating;

	public RecommendedItem(long itemid, float rating) {
		itemID = itemid;
		estRating = rating;
	}

	public void setItemID(long itemid) {
		itemID = itemid;
	}
	long getItemID() {
		return itemID;
	}
	public void setRating(float rating) {
		estRating = rating;
	}

	public float getValue() {
		return estRating;
	}
	
	public String toString(){
		return new String("("+itemID+","+estRating+")");
	}
}
