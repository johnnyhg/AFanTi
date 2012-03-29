package AFanTi.Recommend;

import java.io.Serializable;

import AFanTi.Neighborhood.Neighborhood;

public class RecommendedItem implements Comparable,Serializable{
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		RecommendedItem other=(RecommendedItem)o;

		int result=estRating>other.estRating?1:estRating<other.estRating?-1:0;
		return result;
	
	}


}
