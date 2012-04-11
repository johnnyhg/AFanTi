package AFanTi.DataModel;

public interface UpdateDataModel {
	
	public boolean setRating(long userID, long itemID, float rating);
	public Float getRating(long userID, long itemID);
	public boolean removeRating(long userID, long itemID);
}
