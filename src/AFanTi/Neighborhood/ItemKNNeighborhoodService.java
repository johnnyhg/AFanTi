package AFanTi.Neighborhood;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.ylj.math.Vector;

public interface ItemKNNeighborhoodService   extends  Remote {
	
	
	public Neighborhood[] getNeighborhoodsOfItem(Vector item, long userID) throws RemoteException;


}
