package AFanTi.Neighborhood;

import java.util.List;

import org.ylj.math.Vector;

public interface AsynchronousItemKNNeighborhoodServerProxy {
	
	public long getNeighborhoodsOfItems(Vector[] items, long userID,long callserial,String receiverName) ;

	
}
