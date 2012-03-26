package AFanTi.Neighborhood;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import org.ylj.math.Vector;

public class ItemKNNeighborhood_Distributed_Selecter implements
		ItemNeighborhoodSelecter {
	
	int K=0;
	List<ItemKNNeighborhoodService> remoteNeighborhoodServerList = new LinkedList<ItemKNNeighborhoodService>();

	public ItemKNNeighborhood_Distributed_Selecter(int k)
	{
		K=k;
	}
	
	
	public void addRemoteNeighborhoodServer(
			ItemKNNeighborhoodService remoteServer) {
		if (remoteServer != null)
			remoteNeighborhoodServerList.add(remoteServer);
	}

	public void removeRemoteNeighborhoodServer(
			ItemKNNeighborhoodService remoteServer) {

		remoteNeighborhoodServerList.remove(remoteServer);
	}

	@Override
	public Neighborhood[] getNeighborhoodsOfItem(Vector item, long userID) {
		// TODO Auto-generated method stub
		PriorityQueue<Neighborhood> topKNeighborhoods=new PriorityQueue<Neighborhood>(K);
		
		for(ItemKNNeighborhoodService aServer:remoteNeighborhoodServerList)
		{
			try {
				Neighborhood[] tempNeighborhoods= aServer.getNeighborhoodsOfItem(item, userID);
				
				for(Neighborhood tempNeighborhood:tempNeighborhoods)
				{
					if(tempNeighborhood==null)
						continue;
					if(topKNeighborhoods.size()<K)
					{
						topKNeighborhoods.add(tempNeighborhood);
					}
					else
					{
						if(topKNeighborhoods.peek().compareTo(tempNeighborhood)<0)
						{
							topKNeighborhoods.poll();
							topKNeighborhoods.add(tempNeighborhood);
						}
					}
					
					
				}
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return topKNeighborhoods.toArray(new Neighborhood[topKNeighborhoods.size()]);
	}


	@Override
	public List<Neighborhood[]> getNeighborhoodsOfItems(List<Vector> items,
			long userID) {
		// TODO Auto-generated method stub
		return null;
	}

}
