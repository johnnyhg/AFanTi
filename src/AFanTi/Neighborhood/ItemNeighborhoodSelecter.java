package AFanTi.Neighborhood;

import java.rmi.RemoteException;
import java.util.List;

import org.ylj.math.Vector;

public interface ItemNeighborhoodSelecter {
	
	
	public Neighborhood[] getNeighborhoodsOfItem(Vector itemV,long userID);
	public Neighborhood[] getNeighborhoodsOfItem(long itemID,long userID);
	
}
