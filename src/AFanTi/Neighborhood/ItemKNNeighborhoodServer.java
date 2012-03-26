package AFanTi.Neighborhood;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ylj.math.Vector;

import AFanTi.DataModel.DataModel;
import AFanTi.DataModel.ItemDataModelServer;
import AFanTi.DataModel.ItemDataModelSevice;
import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.DataModel.NewGenericDataModel;
import AFanTi.Similarity.SimilarityComputer;

public class ItemKNNeighborhoodServer extends UnicastRemoteObject implements ItemKNNeighborhoodService{

	private static final long serialVersionUID = 1L;

	ItemNeighborhoodSelecter neighborhoodSelecter;

	
	private static Logger logger = Logger.getLogger(ItemKNNeighborhoodServer.class.getName());
	
	public ItemKNNeighborhoodServer(SimilarityComputer sComputer, ItemBasedDataModel model, int Nneighbors) throws RemoteException {
		
		neighborhoodSelecter=new ItemKNNeighborhoodSelecter(sComputer,model,Nneighbors);
		
	}
	

	/**
	 * 
	 */
	

	@Override
	public Neighborhood[] getNeighborhoodsOfItem(Vector item, long userID) {
		// TODO Auto-generated method stub
		if(item==null)
		{
			logger.info("Call getNeighborhoodsOfItem(null,"+userID+")");
			return null;
		}			
		logger.info("Call getNeighborhoodsOfItem("+item.getVectorID()+","+userID+")");
		
		return neighborhoodSelecter.getNeighborhoodsOfItem(item, userID);
		
	}


	@Override
	public List<Neighborhood[]> getNeighborhoodsOfItems(Vector[] items,
			long userID) throws RemoteException {
		
		if(items==null)
		{
			logger.info("Call getNeighborhoodsOfItem(null,"+userID+")");
			return null;
		}
		String items_str="[";
		for(Vector item:items)
		{
			items_str=items_str+item.getVectorID()+" ";
		}
		items_str=items_str+"]";
		
		logger.info("Call getNeighborhoodsOfItems("+items_str+","+userID+")");

		return  neighborhoodSelecter.getNeighborhoodsOfItems(items, userID);
	
	}

	
}
