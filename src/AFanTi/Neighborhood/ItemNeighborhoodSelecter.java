package AFanTi.Neighborhood;

import org.ylj.math.Vector;

public interface ItemNeighborhoodSelecter {
	
	
	public Neighborhood[] getNeighborhoodsOfItem(Vector item,long userID);
	public Neighborhood[] getNeighborhoodsOfItemGlobal(Vector item,long userID);
	
}
