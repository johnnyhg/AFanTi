package AFanTi.DataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataModelSevice  extends  Remote{
	
	  float getPreference(long userID, long itemID)throws RemoteException;
	  void setPreference(long userID, long itemID, float value)throws RemoteException;
	  void removePreference(long userID, long itemID)throws RemoteException;
}
