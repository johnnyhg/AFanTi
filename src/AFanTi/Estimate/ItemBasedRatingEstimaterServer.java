package AFanTi.Estimate;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.util.LinkedList;

import java.util.Queue;

import org.apache.log4j.Logger;

import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.Neighborhood.ItemNeighborhoodSelecter;
import AFanTi.Neighborhood.Neighborhood;

public class ItemBasedRatingEstimaterServer implements EstimatRatingProxy {

	ItemBasedDataModel itemBasedDataModel;
	ItemNeighborhoodSelecter neighborhoodSelecter;
	RatingComputer ratingComputer;

	boolean running;

	private static Logger logger = Logger
			.getLogger(ItemBasedRatingEstimaterServer.class.getName());

	Queue<EsitmateRequest> WaitToComputeQueue = new LinkedList<EsitmateRequest>();
	Queue<EsitmateRequest> WaitToRespondQueue = new LinkedList<EsitmateRequest>();

	DoComputeThread computeThread;
	DoRespondThread respondThread;
	DoTimeOutCheckThread timeOutCheckThread;

	public ItemBasedRatingEstimaterServer(ItemBasedDataModel datamodel,
			ItemNeighborhoodSelecter nbseleter, RatingComputer rcomputer) {
		
		itemBasedDataModel = datamodel;
		neighborhoodSelecter = nbseleter;
		ratingComputer = rcomputer;

		computeThread = new DoComputeThread();
		computeThread.setName("ComputeThread");
		
		respondThread = new DoRespondThread();
		respondThread.setName("RespondThread");
		
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

			return false;
		}

		EsitmateRequest newRequest = new EsitmateRequest();

		newRequest.itemIDs = itemIDs;
		newRequest.part_K = part_K;
		newRequest.callSerial = callSerial;
		newRequest.receiver = receiverProxy;
		newRequest.waitAtTime = System.nanoTime();

		
		logger.info("get a EstimatRating Request ( itemIDs:["+itemIDs.length+"],userID:"+userID+",part_K:"+part_K+",callSerial:"+callSerial+",result_receiver:"+result_receiver+")");
		
		
		synchronized (WaitToComputeQueue) {
			
			WaitToComputeQueue.add(newRequest);
			WaitToComputeQueue.notify();
		}
		
		

		return false;

	}

	public float estimatRating(long itemID, long userID) {
		Float estimatedRating;
		if ((estimatedRating = itemBasedDataModel.getRating(userID, itemID)) != null) {
			return estimatedRating;

		}

		Neighborhood[] neighborhoods = neighborhoodSelecter
				.getNeighborhoodsOfItem(itemID, userID);

		if (neighborhoods == null)
			return 0;

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
							WaitToComputeQueue.wait();
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
				logger.info(" do a estimatRatings job");
				
				
				aRequest.esitmatedRatings = estimatRatings(aRequest.itemIDs,
						aRequest.userID);
				
				++DoComputeCount;
				synchronized (WaitToRespondQueue) {
					WaitToRespondQueue.add(aRequest);
					WaitToRespondQueue.notifyAll();
				}
				logger.info(" do a estimatRatings job complement.");

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
							WaitToRespondQueue.wait();
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
				logger.info(" do a Respond job.");
				EstimatedRatingReceiverProxy receiver=aRequest.receiver;			
				try {
					receiver.setEstimatedRating(aRequest.itemIDs, aRequest.esitmatedRatings, aRequest.part_K, aRequest.callSerial);
					logger.info(" do a Respond job complement.");
					DoRespondCount++;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					logger.info(" do a Respond job failed.");
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
		computeThread.start();
		respondThread.start();
		//timeOutCheckThread.start();
	}

	public void stop() {

	}
}
