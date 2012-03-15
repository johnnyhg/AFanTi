package AFanTi.Neighborhood;

import org.ylj.math.Vector;

public interface NeighborhoodSelecter {
	
	
	public Neighborhood[] getNeighborhoodsOfItem(Vector item,long userID);
	
	public Neighborhood[] getNeighborhoodsOfUser(Vector user,long itemID);
}
