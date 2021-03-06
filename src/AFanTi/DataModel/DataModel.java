package AFanTi.DataModel;

import java.util.Set;

import org.ylj.math.Vector;

public interface DataModel {
	
	public boolean containItem(long itemID);
	public boolean containUser(long userID);
	
	public void setRating(long userID, long itemID, float rating);
	public Float getRating(long userID, long itemID);
	public void removeRating(long userID, long itemID);
	
	public long[] getAllItemsRatedByUser(long userID);
	public long[] getAllUsersRatedTheItem(long itemID);
	
	public Set<Long> getAllItemIDs();
	public Set<Long> getAllUserIDs();
	
}
