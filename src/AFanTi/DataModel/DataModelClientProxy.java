package AFanTi.DataModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataModelClientProxy extends Remote{
	
	void setResult(long command_ID,boolean state)throws RemoteException;
	void setResult(long command_ID,Float value)throws RemoteException;
	
	
	
}
