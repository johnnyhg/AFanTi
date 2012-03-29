package AFanTi.DataModel;

import java.rmi.Remote;

public interface DataModleClientProxy extends Remote{
	
	void setResult(long command_ID,boolean state);
	void setResult(long command_ID,float value);
	
}
