package AFanTi.DataModel;

import org.ylj.math.Vector;

public interface DataModel {
	
	public boolean containItem(long itemID);
	public boolean containUser(long userID);
	
	public void setRating(long userID, long itemID, float rating);
	public Float getRating(long userID, long itemID);
	public void removeRating(long userID, long itemID);
	
	public Vector getItemVector(long itemID);
	public Vector getUserVector(long userID);
	
	public long[] getAllItemsRatedByUser(long userID);
	public long[] getAllUsersRatedTheItem(long itemID);
	
	
	
}
