package AFanTi.Neighborhood;

import java.rmi.Naming;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.ylj.math.Vector;

import AFanTi.Recommend.AsyncRecommenditionReceiverProxy;
import AFanTi.Recommend.CallBackResult_fromNeighborhoodServer;

public class AsyncItemKNNeighborhoodServer implements
		AsynchronousItemKNNeighborhoodServerProxy {
	
	 Queue<Request> waitToComputeQueue=new LinkedList<Request>();
	 Queue<Request> WaitToRespondQueue=new LinkedList<Request>();
	 boolean running;
	 ItemKNNeighborhoodSelecter selecter;

	class Request {
		
		long callserial;
		
		Vector[] items;
		long userID;
		
		java.util.Vector<Neighborhood[]> neighborhoodResult;
		
		AsynchronousItemKNNeighborhoodResultReceiverProxy resultReceiver;
	    long waitAtNanoTime;  //nano tiem

		public Request(
				Vector[] items_,
				long userID_,
				long callserial_,
				AsynchronousItemKNNeighborhoodResultReceiverProxy resultReceiver_) {
			
			items = items_;
			userID = userID_;
			callserial = userID_;
			resultReceiver = resultReceiver_;
		}
	}

	@Override
	public long getNeighborhoodsOfItems(Vector[] items, long userID,
			long callserial, String receiverName) {

		AsynchronousItemKNNeighborhoodResultReceiverProxy receiverProxy = null;

		try {

			receiverProxy = (AsynchronousItemKNNeighborhoodResultReceiverProxy) Naming
					.lookup(receiverName);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			receiverProxy = null;
			return -4;
		}
		if (items == null)
			return -1;

		Request newRequest = new Request(items,userID,callserial,receiverProxy);
		newRequest.waitAtNanoTime=System.nanoTime();
		waitToComputeQueue.add(newRequest);
		// TODO Auto-generated method stub
		return 0;
	}
	
	private class DoComputeThread extends Thread {

		@Override
		public void run() {
			while (running) {
				
				/*
				 * sleep
				 */
				while(waitToComputeQueue.size()>0)
				{
					Request aRequest=waitToComputeQueue.poll();
					aRequest.neighborhoodResult=selecter.getNeighborhoodsOfItems(aRequest.items, aRequest.userID);
					WaitToRespondQueue.add(aRequest);
				}

			}
		}
		
	}
	
	private class DoRespondThread extends Thread {

		@Override
		public void run() {
			while (running) {
				while(WaitToRespondQueue.size()>0)
				{
					Request aRequest=WaitToRespondQueue.poll();
					AsynchronousItemKNNeighborhoodResultReceiverProxy receiver=aRequest.resultReceiver;
					if(receiver==null)
						continue;
					CallBackResult_fromNeighborhoodServer
					receiver.setNeighborhoodsResult(aRequest.callserial, result);
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

}
