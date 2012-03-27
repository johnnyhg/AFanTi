package AFanTi.Neighborhood;

import AFanTi.Recommend.CallBackResult_fromNeighborhoodServer;

public interface AsynchronousItemKNNeighborhoodResultReceiverProxy {
	 boolean setNeighborhoodsResult(long callSerial,
				CallBackResult_fromNeighborhoodServer result);
}
