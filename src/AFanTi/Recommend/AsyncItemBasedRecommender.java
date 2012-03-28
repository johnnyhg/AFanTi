package AFanTi.Recommend;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

import java.util.Queue;

import org.apache.log4j.Logger;
import org.ylj.math.Vector;

import AFanTi.Call.Call;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Estimate.EsitmateRequest;
import AFanTi.Estimate.EstimatRatingProxy;
import AFanTi.Estimate.EstimatedRatingReceiverProxy;
import AFanTi.Estimate.RatingComputer;
import AFanTi.Estimate.GeneralRatingComputer;


public class 

extends UnicastRemoteObject  implements AsyncRecommenderProxy,
		EstimatedRatingReceiverProxy {

	
	
	protected AsyncItemBasedRecommender() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	GeneralItemBasedDataModel itemBasedDataModel;

	String RMI_PATH;
	static long CLIENT_SERIAL = 1;
	static long CALL_SERIAL = 1;

	static int PER_ESTIMATE_REQUEST_SIZE = 1000;

	int K;// k - nighborhoods

	RatingComputer RatingEstimater = new GeneralRatingComputer();

	private Map<Long, Call> WaitForEstimateMap = new HashMap<Long, Call>();
	private Queue<Call> WaitForEstimateQueue = new LinkedList<Call>();

	// private Queue<Call> WaitToComputeQueue = new LinkedList<Call>();

	private Queue<Call> WaitToRespondQueue = new LinkedList<Call>();

	/*
	 * client
	 */
	private Map<String, AsyncRecommenditionReceiverProxy> clientName2ClientProxyMap = new HashMap<String, AsyncRecommenditionReceiverProxy>();

	private Queue<AsyncRecommenditionReceiverProxy> clientList = new LinkedList<AsyncRecommenditionReceiverProxy>();

	private List<EstimatRatingProxy> EstimatRatingProxyList = new LinkedList<EstimatRatingProxy>();

	boolean running;
	
	private static Logger logger = Logger.getLogger(ItemBasedRecommender.class
			.getName());

	

	public long[] getCandidateItems(long userID) {

		long[] ratingedItems_arrary = itemBasedDataModel
				.getAllItemsRatedByUser(userID);
		Set<Long> ratingedItems = new TreeSet<Long>();
		for (long ratingedItem : ratingedItems_arrary) {
			ratingedItems.add(ratingedItem);
		}
		Set<Long> allItems = itemBasedDataModel.getAllItemIDs();

		long[] candidateItems = new long[allItems.size() - ratingedItems.size()];
		// do filtrate
		int i = 0;
		for (long tempItem : allItems) {

			if (ratingedItems.contains(tempItem))
				continue;
			candidateItems[i] = tempItem;
			i++;
		}

		return candidateItems;

	}

	@Override
	public long makeRecommend(long userID, int num, String receiverName) throws RemoteException {
		// TODO Auto-generated method stub

		CALL_SERIAL++;
		if (Long.MAX_VALUE == CALL_SERIAL) {
			CALL_SERIAL = 0;

		}
		long thisCallSerial = CALL_SERIAL;
		AsyncRecommenditionReceiverProxy receiverProxy = null;

		try {

			receiverProxy = (AsyncRecommenditionReceiverProxy) Naming
					.lookup(receiverName);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			receiverProxy = null;
			return -1;
		}

		long[] candidateItemsID = getCandidateItems(userID);

		Call newCall = new Call();
		/*
		 * initial thisCallResult
		 */
		newCall.N = num;
		newCall.TopNItems = new PriorityQueue<RecommendedItem>();
		newCall.callSerial = thisCallSerial;
		newCall.candidateItemsID = candidateItemsID;
		newCall.waitAtNanoTime = System.nanoTime();
		newCall.resultReceiverProxy = receiverProxy;

		WaitForEstimateQueue.add(newCall);
		WaitForEstimateMap.put(thisCallSerial, newCall);

		/*
		 * call EstimatRatingProxy
		 */
		int cursor = 0;
		int part_k = 0;
		for (EstimatRatingProxy EstimaterProxy : EstimatRatingProxyList) {

			int size = (candidateItemsID.length - cursor);
			if (size == 0)
				break;
			size = size > PER_ESTIMATE_REQUEST_SIZE ? PER_ESTIMATE_REQUEST_SIZE
					: size;
			long[] aPart = new long[size];
			for (int i = 0; i < size; i++) {
				aPart[i] = candidateItemsID[cursor++];
			}

			if (EstimaterProxy.estimatRating(aPart, userID, part_k,
					thisCallSerial, RMI_PATH) == false) {
				/*
				 * if a EstimaterProxy error then find a next EstimaterProxy to
				 * send this part
				 */
				cursor -= size;

			}
			part_k++;

		}

		return thisCallSerial;

	}

	private class DoRespondThread extends Thread {

		public Call waitACall()
		{
			Call aCall = null;

			// wait for a work
			while (true) {

				synchronized (WaitToRespondQueue){
					
					if (WaitToRespondQueue.size() > 0) {
						
						aCall = WaitToRespondQueue.poll();
						break;
						
					} else {
						
						try {
							WaitToRespondQueue.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
			return aCall;
			
		}
		@Override
		public void run() {

			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */

					Call aCall = waitACall();
					long callSerial = aCall.callSerial;
					AsyncRecommenditionReceiverProxy client = aCall.resultReceiverProxy;
					
					try {
						RecommendedItem[] recommendedItems = aCall
								.getRecommendedItems();
						client.setRecommendItem(callSerial, recommendedItems);

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

	private class DoTimeOutCheckThread extends Thread {

		@Override
		public void run() {

			while (running) {

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
		AsyncRecommenditionReceiverProxy proxy = clientName2ClientProxyMap
				.get(client_rmi_obj);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setEstimatedRating(long[] itemIDs, float[] ratings, int part_K,
			long callSerial) {
		
		logger.info("SetEstimatedRating(["+itemIDs.length+"],part_K:"+part_K+",callSerial:"+callSerial+")");
		
		if (itemIDs.length != ratings.length)
			return;

		Call targetCall = WaitForEstimateMap.get(callSerial);

		if (targetCall == null)
			return;
		

		
		targetCall.setEstimateRating(itemIDs, ratings, part_K);

		if (!targetCall.isAllPartsOk())
			return;
		
		
		
		WaitForEstimateMap.remove(callSerial);
		WaitForEstimateQueue.remove(targetCall);
		WaitToRespondQueue.add(targetCall);

		// TODO Auto-generated method stub

	}
	
	
}
