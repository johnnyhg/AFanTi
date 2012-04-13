package AFanTi.Estimate;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.LinkedList;

import java.util.Queue;

import org.apache.log4j.Logger;
import org.apache.mahout.math.Arrays;
import org.ylj.common.UTimeInterval;

import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Neighborhood.Neighborhood;

public class ItemBasedRatingEstimater extends UnicastRemoteObject implements EstimatRatingProxy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ItemBasedDataModel itemBasedDataModel;
	ItemNeighborhoodSelecter neighborhoodSelecter;
	RatingComputer ratingComputer;

	boolean running;

	private static Logger logger = Logger
			.getLogger(ItemBasedRatingEstimater.class.getName());

	Queue<EsitmateRequest> WaitToComputeQueue = new LinkedList<EsitmateRequest>();
	Queue<EsitmateRequest> WaitToRespondQueue = new LinkedList<EsitmateRequest>();

	DoComputeThread[] computeThread;
	DoRespondThread[] respondThread;
	DoTimeOutCheckThread timeOutCheckThread;
	
	

	public ItemBasedRatingEstimater(ItemBasedDataModel datamodel,
			ItemNeighborhoodSelecter nbseleter, RatingComputer rcomputer,int computeThreadNum,int respondThreadNum ) throws RemoteException {
		
		itemBasedDataModel = datamodel;
		neighborhoodSelecter = nbseleter;
		ratingComputer = rcomputer;

		computeThread = new DoComputeThread[computeThreadNum];
		respondThread = new DoRespondThread[respondThreadNum];
		timeOutCheckThread = new DoTimeOutCheckThread();
		
		timeOutCheckThread.setName("TimeOutCheckThread");
		
	}

	@Override
	public boolean estimatRating(long[] itemIDs, long userID, int part_K,
			long callSerial, String result_receiver) {

		EstimatedRatingReceiverProxy receiverProxy=null;
		try {

			receiverProxy = (EstimatedRatingReceiverProxy) Naming
					.lookup(result_receiver);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("RMI:error "+result_receiver);
			
			return false;
		}

		EsitmateRequest newRequest = new EsitmateRequest();

		newRequest.itemIDs = itemIDs;
		newRequest.userID=userID;
		newRequest.part_K = part_K;
		newRequest.callSerial = callSerial;
		newRequest.receiver = receiverProxy;
		newRequest.waitAtTime = System.nanoTime();

		
		logger.info("get a EstimatRating Request ( itemIDs:["+itemIDs.length+"],userID:"+userID+",part_K:"+part_K+",callSerial:"+callSerial+",result_receiver:"+result_receiver+")");
		
		
		synchronized (WaitToComputeQueue) {
			
			WaitToComputeQueue.add(newRequest);
			WaitToComputeQueue.notify();
		}
		
		

		return true;

	}

	public float estimatRating(long itemID, long userID) {
		Float estimatedRating;
		if ((estimatedRating = itemBasedDataModel.getRating(userID, itemID)) != null) {
			return estimatedRating;

		}

		Neighborhood[] neighborhoods = neighborhoodSelecter
				.getNeighborhoodsOfItem(itemID, userID);

		if (neighborhoods == null)
		{
			logger.error("neighborhoods==null itemID:"+itemID+"userID:"+userID);
			return 0;
		}
		float[] ratingArrary = new float[neighborhoods.length];
		double[] similarityArrary = new double[neighborhoods.length];

		for (int j = 0; j < neighborhoods.length; j++) {
			ratingArrary[j] = neighborhoods[j].rating;
			similarityArrary[j] = neighborhoods[j].similarity;
		}

		return ratingComputer.computeRating(ratingArrary, similarityArrary);
	}

	public float[] estimatRatings(long[] itemIDs, long userID) {
		if (itemIDs == null)
			return null;
		float[] estimatedRatings = new float[itemIDs.length];
		for (int i = 0; i < estimatedRatings.length; i++) {

			estimatedRatings[i] = estimatRating(itemIDs[i], userID);

		}
		return estimatedRatings;
	}

	private class DoComputeThread extends Thread {
		
		
		long DoComputeCount=0;
		
		
		public EsitmateRequest waitARequest()
		{
			EsitmateRequest aRequest = null;

			// wait for a work
			while (true) {

				synchronized (WaitToComputeQueue){
					
					if (WaitToComputeQueue.size() > 0) {
						
						aRequest = WaitToComputeQueue.poll();
						break;
						
					} else {
						
						try {
							logger.info(" thread wait ...");
							WaitToComputeQueue.wait();
							logger.info(" thread weaken ...");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
			return aRequest;
		}
		@Override
		public void run() {
			logger.info(this.getName()+"  running.");
			
			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */
				
				EsitmateRequest aRequest = waitARequest();
				
				int time=UTimeInterval.startNewInterval();
				
				logger.info(" do a estimatRatings job");
				
				
				
				aRequest.esitmatedRatings = estimatRatings(aRequest.itemIDs,
						aRequest.userID);
				
				++DoComputeCount;
				synchronized (WaitToRespondQueue) {
					WaitToRespondQueue.add(aRequest);
					WaitToRespondQueue.notifyAll();
				}
				logger.info(" do a estimatRatings job complement. itemIDs:"+aRequest.itemIDs.length+" cost:"+UTimeInterval.endInterval(time)+"'us");

			}

		}
	}

	private class DoRespondThread extends Thread {
		
		long DoRespondCount=0;
		/*
		 * sleep until resultsNeighborsfrom neighborhood server all ok
		 * just
		 */
		public EsitmateRequest waitARequest()
		{
			EsitmateRequest aRequest = null;
			
			while (true) {
				
				synchronized (WaitToRespondQueue) {						
					if (WaitToRespondQueue.size() > 0) {						
						aRequest = WaitToRespondQueue.poll();
						break;					
					} else {
						try {
							logger.info(" thread wait ...");
							WaitToRespondQueue.wait();
							logger.info(" thread weaken ...");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
			return aRequest;
		}
		
		@Override
		public void run() {
			logger.info(this.getName()+"  running.");
			
			while (running) {

				
				EsitmateRequest aRequest = waitARequest();		
				int time=UTimeInterval.startNewInterval();
				logger.info(" >> do a Respond job.");
				EstimatedRatingReceiverProxy receiver=aRequest.receiver;			
				try {
					//logger.info(" >> do a Respond:.");
					//logger.info(" >> "+Arrays.toString(aRequest.itemIDs));
					//logger.info(" >> "+Arrays.toString(aRequest.esitmatedRatings));
					//logger.info(" >> "+aRequest.part_K);
					//logger.info(" >> "+aRequest.callSerial);
					receiver.setEstimatedRating(aRequest.itemIDs, aRequest.esitmatedRatings, aRequest.part_K, aRequest.callSerial);
					
					logger.info(" >> do a Respond job complement. cost:"+UTimeInterval.endInterval(time)+"'us");
					DoRespondCount++;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					logger.info(" >> do a Respond job failed.");
					e.printStackTrace();
				}
			
				
			}

		}
	}

	private class DoTimeOutCheckThread extends Thread {

		@Override
		public void run() {

			while (running) {
				// TODO Auto-generated method stub

			}
			// logger.debug("[after estimate all candidateItems]"+(System.currentTimeMillis()-testItemBasedRecommender.timeBegin)+"'ms");

		}
	}

	public void start() {
		
		running=true;
		for(int i=0;i<computeThread.length;i++){
			computeThread[i]=new DoComputeThread();
			
			computeThread[i].setName("ComputeThread-"+i);
			computeThread[i].start();
		}
		
		for(int i=0;i<respondThread.length;i++){
			respondThread[i]=new DoRespondThread();
			
			respondThread[i].setName("RespondThread-"+i);
			respondThread[i].start();
		}
		
		//timeOutCheckThread.start();
	}

	public void stop() {

	}
}
