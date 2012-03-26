package AFanTi.Neighborhood;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import org.ylj.math.Vector;

public interface ItemKNNeighborhoodService   extends  Remote {
	
	
	public Neighborhood[] getNeighborhoodsOfItem(Vector item, long userID) throws RemoteException;
	
	public List<Neighborhood[]> getNeighborhoodsOfItems(Vector[] items, long userID) throws RemoteException;

}
