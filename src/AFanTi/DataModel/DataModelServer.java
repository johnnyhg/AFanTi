package AFanTi.DataModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.apache.mahout.cf.taste.common.TasteException;



public class DataModelServer extends UnicastRemoteObject implements DataModelSevice{
	
	NewGenericDataModel dataModel;
	
	public DataModelServer(NewGenericDataModel data_model) throws RemoteException {
		
		dataModel=data_model;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setPreference(long userID, long itemID, float value)
			throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("calls  dataModel.setPreference("+userID+"," +itemID+","+ value+");");
		dataModel.setPreference(userID, itemID, value);
		
	}

	@Override
	public void removePreference(long userID, long itemID)
			throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("calls  dataModel.removePreference("+userID+"," +itemID+")");
		dataModel.removePreference(userID, itemID);
		
	}

	@Override
	public float getPreference(long userID, long itemID) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("calls  dataModel.getPreferenceValue("+userID+"," +itemID+")");
		Float value=null;
		try {
			value = dataModel.getPreferenceValue(userID, itemID);
			if(value==null)
				return (float)-1;
			else
				return value;
				
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return value;
	}
		
	

}
