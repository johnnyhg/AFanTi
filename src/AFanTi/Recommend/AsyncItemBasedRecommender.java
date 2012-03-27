package AFanTi.Recommend;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import java.util.Queue;

import org.ylj.math.Vector;

import AFanTi.Call.Call;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.RatingEstimater;
import AFanTi.Estimate.GeneralRatingEstimater;
import AFanTi.Neighborhood.AsynchronousItemKNNeighborhoodResultReceiverProxy;
import AFanTi.Neighborhood.AsynchronousItemKNNeighborhoodServerProxy;
import AFanTi.Neighborhood.Neighborhood;

public class AsyncItemBasedRecommender implements AsyncRecommenderProxy,
		AsynchronousItemKNNeighborhoodResultReceiverProxy {

	GeneralItemBasedDataModel itemBasedDataModel;

	static long CLIENT_SERIVAL = 1;
	static long CALL_SERIVAL = 1;

	int K;// k - nighborhoods
	int N;// n - n-recommend items

	RatingEstimater RatingEstimater = new GeneralRatingEstimater();

	private Map<Long, Call> WaitForNeighborsMap = new HashMap<Long, Call>();
	private Queue<Call> WaitForNeighborsQueue = new LinkedList<Call>();

	private Queue<Call> WaitToComputeQueue = new LinkedList<Call>();

	private Queue<Call> WaitToRespondQueue = new LinkedList<Call>();

	/*
	 * client
	 */
	private Map<String, AsyncRecommenditionReceiverProxy> clientName2ClientProxyMap = new HashMap<String, AsyncRecommenditionReceiverProxy>();

	private Queue<AsyncRecommenditionReceiverProxy> clientList = new LinkedList<AsyncRecommenditionReceiverProxy>();

	private List<AsynchronousItemKNNeighborhoodServerProxy> NeighborhoodServerList = new LinkedList<AsynchronousItemKNNeighborhoodServerProxy>();

	boolean running;

	public AsyncItemBasedRecommender() {
		super();
		CALL_SERIVAL = 1;
	}

	public java.util.Vector<Vector> getCandidateItemVs(long userID) {

		long[] ratingedItems = itemBasedDataModel
				.getAllItemsRatedByUser(userID);
		Set<Long> allItems = itemBasedDataModel.getAllItemIDs();

		// do filtrate
		for (long ratingedItem : ratingedItems) {
			allItems.remove(ratingedItem);
		}
		allItems.t
		return allItems.toArray(new Vector[allItems.size()]);

	}

	@Override
	public long makeRecommend(long userID, int num, String receiverName) {
		// TODO Auto-generated method stub

		CALL_SERIVAL++;
		if (Long.MAX_VALUE == CALL_SERIVAL) {
			CALL_SERIVAL = 0;

		}
		long thisCallSerial = CALL_SERIVAL;
		AsyncRecommenditionReceiverProxy receiverProxy = null;
		
		try {
			
			receiverProxy = (AsyncRecommenditionReceiverProxy) Naming.lookup(receiverName);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			receiverProxy=null;
			return -1;
		}

		Vector[] candidateItemvs = getCandidateItemVs(userID);

		Call newCall = new Call();
		/*
		 * initial thisCallResult
		 */
		newCall.callSerial = thisCallSerial;
		newCall.candidteItemVs = candidateItemvs;
		newCall.expectNeighborhoodResultCount = NeighborhoodServerList.size();
		newCall.neighborhoodServerIDs = new int[newCall.expectNeighborhoodResultCount];
		newCall.neighborhoodResultArrivedCount = 0;
		newCall.neighborhoodResultArrary = new CallBackResult_fromNeighborhoodServer[newCall.expectNeighborhoodResultCount];
		newCall.waitAtNanoTime = System.nanoTime();
		newCall.resultReceiverProxy = receiverProxy;
		

		WaitForNeighborsQueue.add(newCall);
		WaitForNeighborsMap.put(thisCallSerial, newCall);

		/*
		 * call all KNNeighborhoodServer
		 */
		for (AsynchronousItemKNNeighborhoodServerProxy neighborhoodServer : NeighborhoodServerList) {
			// call remote server
			neighborhoodServer.getNeighborhoodsOfItems(newCall.candidteItemVs, userID,
					thisCallSerial,"thisObjName");
		}

		return thisCallSerial;

	}

	@Override
	public boolean setNeighborhoodsResult(long callSerial,
			CallBackResult_fromNeighborhoodServer result) {
		// TODO Auto-generated method stub
		Call call = WaitForNeighborsMap.get(callSerial);

		if (call == null)
			return false;

		boolean returnCode = call.addNeighborhoodResult(result);

		if (call.isAllNeighborhoodResultOK()) {

			WaitForNeighborsQueue.remove(call);
			WaitForNeighborsMap.remove(callSerial);

			WaitToComputeQueue.add(call);

			/*
			 * do something
			 */
		}
		return returnCode;
	}

	private class DoRespondThread extends Thread {

		@Override
		public void run() {

			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */

				while (WaitToRespondQueue.size() > 0) {

					Call aCall = WaitToRespondQueue.poll();
					long callSerial = aCall.callSerial;
					AsyncRecommenditionReceiverProxy client = aCall.resultReceiverProxy;
					try {
						client.setRecommendItem(callSerial,
								aCall.recommendedItems);

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						// remove a error client
						clientList.remove(client);
					}

					/*
					 * remove aCall
					 */
				}

			}

		}
	}

	private class DoTimeOutCheckThread extends Thread {

		@Override
		public void run() {

			while (running) {

			}

		}
	}

	private class DoComputeThread extends Thread {

		@Override
		public void run() {

			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */
				while (WaitToComputeQueue.size() > 0) {
					// do a Call;
					Call okResult = WaitToComputeQueue.poll();

					PriorityQueue<RecommendedItem> topKCandidates = new PriorityQueue<RecommendedItem>();

					for (int i = 0; i < okResult.candidteItemVs.length; i++) {

						PriorityQueue<Neighborhood> topKNeighborhoods = new PriorityQueue<Neighborhood>();

						for (CallBackResult_fromNeighborhoodServer aNeighborhoodServer : okResult.neighborhoodResultArrary) {
							Neighborhood[] tempNeighborhoods = aNeighborhoodServer.neighborhoodList
									.get(i);

							for (Neighborhood neighborhood : tempNeighborhoods) {

								if (topKNeighborhoods.size() < K) {
									topKNeighborhoods.add(neighborhood);
								} else {
									if (neighborhood.similarity > topKNeighborhoods
											.peek().similarity) {
										topKNeighborhoods.poll();
										topKNeighborhoods.add(neighborhood);
									}
								}
							}

						}
						Neighborhood[] trueNeighborhoods = topKNeighborhoods
								.toArray(new Neighborhood[topKNeighborhoods
										.size()]);
						float estimateRating = RatingEstimater
								.estimateRating(trueNeighborhoods);

						if (topKCandidates.size() < N) {
							RecommendedItem newRecommendedItem = new RecommendedItem(
									okResult.candidteItemVs[i].getVectorID(),
									estimateRating);
							topKCandidates.add(newRecommendedItem);
						} else {
							if (estimateRating > topKCandidates.peek().estRating) {
								RecommendedItem newRecommendedItem = new RecommendedItem(
										okResult.candidteItemVs[i]
												.getVectorID(),
										estimateRating);

								topKCandidates.poll();
								topKCandidates.add(newRecommendedItem);
							}
						}

					}

					okResult.candidteItemVs = null;
					okResult.neighborhoodServerIDs = null;
					okResult.neighborhoodResultArrary = null;

					okResult.recommendedItems = topKCandidates
							.toArray(new RecommendedItem[topKCandidates.size()]);

					WaitToRespondQueue.add(okResult);

				}

			}

		}
	}

	@Override
	public int registerClientProxy(String client_rmi_obj)
			throws RemoteException {

		// TODO Auto-generated method stub
		try {
			AsyncRecommenditionReceiverProxy clientProxy = (AsyncRecommenditionReceiverProxy) Naming
					.lookup(client_rmi_obj);

			clientList.add(clientProxy);
			clientName2ClientProxyMap.put(client_rmi_obj, clientProxy);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	@Override
	public int unregisterClientProxy(String client_rmi_obj)
			throws RemoteException {
		AsyncRecommenditionReceiverProxy proxy = clientName2ClientProxyMap.get(client_rmi_obj);
		if (proxy == null)
			return 0;
		clientList.remove(proxy);
		clientName2ClientProxyMap.remove(client_rmi_obj);
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean isClientProxyRegisted(String client_rmi_obj)
			throws RemoteException {
		AsyncRecommenditionReceiverProxy proxy = clientName2ClientProxyMap.get(client_rmi_obj);
		if (proxy == null)
			return false;
		return true;

	}
}
