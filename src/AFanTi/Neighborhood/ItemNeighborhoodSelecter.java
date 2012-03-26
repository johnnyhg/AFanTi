package AFanTi.Neighborhood;

import java.rmi.RemoteException;
import java.util.List;

import org.ylj.math.Vector;

public interface ItemNeighborhoodSelecter {
	
	
	public Neighborhood[] getNeighborhoodsOfItem(Vector item,long userID);
	public List<Neighborhood[]> getNeighborhoodsOfItems(Vector[] items, long userID) ;
}
