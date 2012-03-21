package AFanTi.Neighborhood;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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

	
}
