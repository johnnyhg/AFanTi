package AFanTi.DataModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.ylj.math.Vector;

public class ItemDataModelServer extends UnicastRemoteObject implements ItemDataModelSevice {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1187775549885524940L;
	ItemBasedDataModel dataModel;
	private static Logger logger = Logger.getLogger(ItemDataModelServer.class.getName());

	public ItemDataModelServer(ItemBasedDataModel data_model) throws RemoteException {

		dataModel = data_model;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setRating(long userID, long itemID, float rating) {

		logger.info("Call setRating("+userID+","+itemID+","+rating+")");
		dataModel.setRating(userID, itemID, rating);
		// TODO Auto-generated method stub

	}

	@Override
	public Float getRating(long userID, long itemID) {
		// TODO Auto-generated method stub
		logger.info("Call getRating("+userID+","+itemID+")");
		return dataModel.getRating(userID, itemID);
	}

	@Override
	public void removeRating(long userID, long itemID) {
		// TODO Auto-generated method stub
		logger.info("Call removeRating("+userID+","+itemID+")");
		dataModel.removeRating(userID, itemID);

	}

	@Override
	public boolean containItem(long itemID) {
		// TODO Auto-generated method stub
		logger.info("Call containItem("+itemID+")");
		return dataModel.containItem(itemID);
	}

	@Override
	public boolean containUser(long userID) {
		// TODO Auto-generated method stub
		logger.info("Call containUser("+userID+")");
		return dataModel.containUser(userID);
	}

	@Override
	public Vector getItemVector(long itemID) {
		// TODO Auto-generated method stub
		logger.info("Call getItemVector("+itemID+")");
		return dataModel.getItemVector(itemID);
	}
	/*
	@Override
	public Vector getUserVector(long userID) {
		// TODO Auto-generated method stub
		return dataModel.getUserVector(userID);
	}

	@Override
	public long[] getAllItemsRatedByUser(long userID) {
		// TODO Auto-generated method stub
		return dataModel.getAllItemsRatedByUser(userID);
	}

	@Override
	public long[] getAllUsersRatedTheItem(long itemID) {
		// TODO Auto-generated method stub
		return dataModel.getAllUsersRatedTheItem(itemID);
	}

	@Override
	public Set<Long> getAllItemIDs() {
		// TODO Auto-generated method stub
		return dataModel.getAllItemIDs();
	}

	@Override
	public Set<Long> getAllUserIDs() {
		// TODO Auto-generated method stub
		return dataModel.getAllUserIDs();
	}
	*/
}
