package AFanTi.Recommend;


import java.rmi.Naming;


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



import AFanTi.DataModel.GeneralItemBasedDataModel;

import AFanTi.Estimate.EstimatRatingProxy;
import AFanTi.Estimate.EstimatedRatingReceiverProxy;
import AFanTi.Estimate.RatingComputer;
import AFanTi.Estimate.GeneralRatingComputer;

public class AsyncItemBasedRecommender extends UnicastRemoteObject implements
		AsyncRecommenderProxy, EstimatedRatingReceiverProxy {

	protected AsyncItemBasedRecommender(GeneralItemBasedDataModel dataModel) throws RemoteException {
		super();
		itemBasedDataModel=dataModel;
		 respondThread =new DoRespondThread();
		 respondThread.setName("respondThread");
		 timeOutCheckThread =new DoTimeOutCheckThread();
		 running=true;
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	GeneralItemBasedDataModel itemBasedDataModel;

	String RMI_URL;

	
	static long CALL_SERIAL = 0;

	static int PER_PART_SIZE = 1000;

	int K;// k - nighborhoods

	RatingComputer RatingEstimater = new GeneralRatingComputer();

	private Map<Long, Call> WaitForEstimateMap = new HashMap<Long, Call>();
	private Queue<Call> WaitForEstimateQueue = new LinkedList<Call>();



	private Queue<Call> WaitToRespondQueue = new LinkedList<Call>();


	private Queue<AsyncRecommenditionReceiverProxy> clientList = new LinkedList<AsyncRecommenditionReceiverProxy>();

	 int nextEstimatRatingProxy_index=0;
	private List<EstimatRatingProxy> EstimatRatingProxyList = new LinkedList<EstimatRatingProxy>();

	boolean running;

	private static Logger logger = Logger
			.getLogger(AsyncItemBasedRecommender.class.getName());
	
	DoRespondThread respondThread ;
	DoTimeOutCheckThread timeOutCheckThread ;
	
	public void setPartSize(int newSize)
	{
		PER_PART_SIZE=newSize;
	}
	public boolean addEstimatRatingProxy(String estimatRatingProxy_RMI_PATH)
	{
		EstimatRatingProxy estimatRatingProxy;
		try {

			estimatRatingProxy = (EstimatRatingProxy) Naming
					.lookup(estimatRatingProxy_RMI_PATH);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		EstimatRatingProxyList.add(estimatRatingProxy);
		return true;
		
	}
	public void setRMI_URL(String path)
	{
		RMI_URL=path;
	}
	public long[] getCandidateItems(long userID) {

		long[] ratingedItems_arrary = itemBasedDataModel
				.getAllItemsRatedByUser(userID);
		Set<Long> ratingedItems = new TreeSet<Long>();
		for (long ratingedItem : ratingedItems_arrary) {
			ratingedItems.add(ratingedItem);
		}
		Set<Long> allItems = itemBasedDataModel.getAllItemIDs();

		long[] candidateItems = new long[allItems.size() - ratingedItems.size()];

		
		int i = 0;
		for (long tempItem : allItems) {

			if (ratingedItems.contains(tempItem))
				continue;
			candidateItems[i] = tempItem;
			i++;
		}

		return candidateItems;

	}

	public EstimatRatingProxy getNextEstimatRatingProxy()
	{
		if(nextEstimatRatingProxy_index==EstimatRatingProxyList.size())
			nextEstimatRatingProxy_index=0;
		return	EstimatRatingProxyList.get(nextEstimatRatingProxy_index++);
		 
	}
	@Override
	public long makeRecommend(long userID, int num, String receiverName)
			throws RemoteException {
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
		
		logger.info("makeRecommend( userID:"+userID+"  candidateItemsIDs:"+candidateItemsID.length+")");
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
		int partCount=candidateItemsID.length/PER_PART_SIZE+((candidateItemsID.length%PER_PART_SIZE==0)?0:1);
		newCall.partsResultSetMark=new boolean[partCount];
		
		WaitForEstimateQueue.add(newCall);
		WaitForEstimateMap.put(thisCallSerial, newCall);

		/*
		 * call EstimatRatingProxy
		 */
		int cursor = 0;

		logger.info("partCount:"+partCount);
		
		for(int part_k=0;part_k<partCount;part_k++)
		{
			
			
				int size;
				if(part_k<partCount-1)
					size=PER_PART_SIZE;
				else
					size=(candidateItemsID.length - cursor);
				 
				logger.info("part_k:"+part_k+" size:"+size);
				
				long[] aPart = new long[size];
				for (int i = 0; i < size; i++) {
					aPart[i] = candidateItemsID[cursor++];
				}
				
				EstimatRatingProxy estimaterProxy=getNextEstimatRatingProxy();
				if (estimaterProxy.estimatRating(aPart, userID, part_k,
						thisCallSerial, RMI_URL) == false) {
					/*
					 * if a EstimaterProxy error then find a next EstimaterProxy to
					 * send this part
					 */
					logger.error("estimaterProxy.estimatRating Error");
					part_k--;
					cursor -= size;

				}
			
				
		
		}
		

		return thisCallSerial;

	}

	private class DoRespondThread extends Thread {

		public Call waitACall() {
			
			Call aCall = null;
			// wait for a work
			while (true) {

				synchronized (WaitToRespondQueue) {

					if (WaitToRespondQueue.size() > 0) {
						
						
						aCall = WaitToRespondQueue.poll();
						break;

					} else {

						try {
							logger.info(" #thread wait ...");
							WaitToRespondQueue.wait();
							logger.info(" #thread weaken ...");
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
			
			logger.info(" Thread start running.");
			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */
				
				Call aCall = waitACall();
				logger.info("#Respond a Call .");
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
				logger.info("#Respond a Call complement.");
				long timeInterval=(System.nanoTime()-aCall.waitAtNanoTime)/1000;
				
				logger.info("#do Call:"+aCall.callSerial +"  cost:"+timeInterval+"'us");
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
	public void setEstimatedRating(long[] itemIDs, float[] ratings, int part_K,
			long callSerial) {

		

		if (itemIDs.length != ratings.length)
			return;

		Call targetCall = WaitForEstimateMap.get(callSerial);

		if (targetCall == null)
			return;

		targetCall.setEstimateRating(itemIDs, ratings, part_K);

		if (!targetCall.isAllPartsOk())
			return;
		
		synchronized (WaitForEstimateMap) {
			WaitForEstimateMap.remove(callSerial);
		}
		synchronized (WaitForEstimateQueue) {
			WaitForEstimateQueue.remove(targetCall);
		}
		synchronized (WaitToRespondQueue) {
			WaitToRespondQueue.add(targetCall);
			WaitToRespondQueue.notifyAll();
		}
		
		// TODO Auto-generated method stub

	}
	public void start()
	{
		respondThread.start();
	}

}
