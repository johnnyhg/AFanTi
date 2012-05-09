package AFanTi.Recommend;

import java.rmi.Remote;

public interface AsyncRecommenderClientProxy extends Remote{
	boolean ping();
}
