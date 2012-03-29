package AFanTi.DataModel;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;

import AFanTi.Estimate.ItemBasedRatingEstimaterServer;
import AFanTi.Recommend.AsyncRecommenditionReceiverProxy;
import AFanTi.Recommend.Call;
import AFanTi.Recommend.RecommendedItem;

public class DataModelMaster implements DataModelMasterProxy {

	long commandSerial = 0;
	boolean running;

	Queue<CommandResult> commandResultQueue = new LinkedList<CommandResult>();
	Map<Long, CommandResult> commandResultMap = new HashMap<Long, CommandResult>();

	Queue<CommandResult> WaitToRespondQueue = new LinkedList<CommandResult>();

	List<DataModelSlaverProxy> slavers = new LinkedList<DataModelSlaverProxy>();

	String MASTER_RMI_URL;
	private static Logger logger = Logger.getLogger(DataModelMaster.class
			.getName());
	
	DoRespondThread respondThread;
	DoTimeOutCheckThread timeOutCheckThread;
	public DataModelMaster()
	{
		respondThread=new DoRespondThread();
		respondThread.setName("RespondThread");
		timeOutCheckThread=new DoTimeOutCheckThread();
		
		
	}

	public long getCommandSerial() {
		if (commandSerial == Long.MAX_VALUE)
			commandSerial = 0;
		return commandSerial++;

	}

	@Override
	public boolean addDataModelSlaver(DataModelSlaverProxy slaver) {
		// TODO Auto-generated method stub
		slavers.add(slaver);
		return false;
	}

	@Override
	public boolean removeDataModelSlaver(DataModelSlaverProxy slaver) {
		// TODO Auto-generated method stub
		slavers.remove(slaver);
		return false;
	}

	@Override
	public boolean setRating(long userID, long itemID, float rating) {
		// TODO Auto-generated method stub
		long thisCommandID = getCommandSerial();

		for (int i = 0; i < slavers.size(); i++) {
			DataModelSlaverProxy aSlaver = slavers.get(i);
			try {
				if (aSlaver.setRating(userID, itemID, rating, thisCommandID, i,
						MASTER_RMI_URL) == false) {
					logger.error("Slaver" + i + " setRating Error");
					return false;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Slaver" + i + " setRating Error");
				return false;
			}
		}
		CommandResult newCommandResult = new CommandResult(thisCommandID,
				slavers.size(), false);
		newCommandResult.waitAtNanoTime = System.nanoTime();

		synchronized (commandResultMap) {
			commandResultQueue.add(newCommandResult);
			commandResultMap.put(thisCommandID, newCommandResult);
		}
		return true;
	}

	@Override
	public boolean removeRating(long userID, long itemID) {
		long thisCommandID = getCommandSerial();

		for (int i = 0; i < slavers.size(); i++) {
			DataModelSlaverProxy aSlaver = slavers.get(i);
			try {
				if (aSlaver.removeRating(userID, itemID, thisCommandID, i,
						MASTER_RMI_URL) == false) {
					logger.error("Slaver" + i + " removeRating Error");
					return false;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				logger.error("Slaver" + i + " removeRating Error");
				e.printStackTrace();
				return false;
			}
		}
		CommandResult newCommandResult = new CommandResult(thisCommandID,
				slavers.size(), false);
		newCommandResult.waitAtNanoTime = System.nanoTime();

		synchronized (commandResultMap) {
			commandResultQueue.add(newCommandResult);
			commandResultMap.put(thisCommandID, newCommandResult);
		}
		return true;
	}

	@Override
	public boolean getRating(long userID, long itemID) {
		// TODO Auto-generated method stub
		long thisCommandID = getCommandSerial();

		for (int i = 0; i < slavers.size(); i++) {
			DataModelSlaverProxy aSlaver = slavers.get(i);
			try {
				if (aSlaver.getRating(userID, itemID, thisCommandID, i,
						MASTER_RMI_URL) == false) {
					logger.error("Slaver" + i + " getRating Error");
					return false;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				logger.error("Slaver" + i + " getRating Error");
				e.printStackTrace();
				return false;
			}
		}
		CommandResult newCommandResult = new CommandResult(thisCommandID,
				slavers.size(), true);
		newCommandResult.waitAtNanoTime = System.nanoTime();

		synchronized (commandResultMap) {
			commandResultQueue.add(newCommandResult);
			commandResultMap.put(thisCommandID, newCommandResult);
		}

		return true;
	}

	class CommandResult {
		long commandID;

		boolean[] slaverReslutMark;
		//
		boolean valueResult;
		float[] slaverReslut;
		boolean[] slaverReslutState;

		DataModleClientProxy resultReceiverProxy;
		long waitAtNanoTime;

		public CommandResult(long comId, int slaverNum, boolean withValue) {

			commandID = comId;
			valueResult = withValue;
			slaverReslutMark = new boolean[slaverNum];

			slaverReslutState = new boolean[slaverNum];
			if (valueResult)
				slaverReslut = new float[slaverNum];

		}

		public void setState(int resultIndex, boolean state) {
			slaverReslutState[resultIndex] = state;
		}

		public void setValue(int resultIndex, float value) {
			slaverReslut[resultIndex] = value;
		}

		public void setMark(int resultIndex) {
			slaverReslutMark[resultIndex] = true;
		}

		public void setTime(long nanoTime) {
			waitAtNanoTime = nanoTime;
		}

		public boolean isResultCompletion() {
			for (boolean aSlaverState : slaverReslutMark) {
				if (aSlaverState == false)
					return false;
			}
			return true;
		}

		public boolean getState() {
			for (boolean aSlaverState : slaverReslutState) {
				if (aSlaverState == false)
					return false;
			}
			return true;

		}

		public float getValue() {
			float result = slaverReslut[0];
			for (float aSlaverValue : slaverReslut) {
				if (aSlaverValue != result)
					return Float.NaN;
				result = aSlaverValue;

			}
			return result;

		}

	}

	@Override
	public boolean setCommandResult(long commandID, int resultIndex,
			boolean state) {

		CommandResult targetResult = commandResultMap.get(commandID);
		if (targetResult == null)
			return false;

		targetResult.setState(resultIndex, state);
		targetResult.setMark(resultIndex);

		if (targetResult.isResultCompletion()) {
			synchronized (WaitToRespondQueue) {

				synchronized (commandResultMap) {
					commandResultMap.remove(commandID);
					commandResultQueue.remove(targetResult);
				}

				WaitToRespondQueue.add(targetResult);
				WaitToRespondQueue.notifyAll();
			}
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean setCommandResult(long commandID, int resultIndex, float value) {
		// TODO Auto-generated method stub
		CommandResult targetResult = commandResultMap.get(commandID);
		if (targetResult == null)
			return false;
		targetResult.setValue(resultIndex, value);
		targetResult.setMark(resultIndex);

		if (targetResult.isResultCompletion()) {
			synchronized (WaitToRespondQueue) {

				commandResultMap.remove(commandID);
				commandResultQueue.remove(targetResult);

				WaitToRespondQueue.add(targetResult);
				WaitToRespondQueue.notifyAll();
			}
			// do something
		}
		return true;
	}

	private class DoRespondThread extends Thread {

		public CommandResult waitComplementResult() {

			CommandResult aCommandResult = null;
			// wait for a work
			while (true) {

				synchronized (WaitToRespondQueue) {

					if (WaitToRespondQueue.size() > 0) {

						aCommandResult = WaitToRespondQueue.poll();
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
			return aCommandResult;

		}

		@Override
		public void run() {

			logger.info(" Thread start running.");
			
			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */

				CommandResult aCommandResult = waitComplementResult();
				logger.info("#Respond a CommandResult .");

				DataModleClientProxy clientProxy =aCommandResult.resultReceiverProxy;
				if (aCommandResult.valueResult)
					
					clientProxy.setResult(aCommandResult.commandID,
									aCommandResult.getValue());
				else
					clientProxy.setResult(aCommandResult.commandID,
									aCommandResult.getState());

				logger.info("#Respond a CommandResult complement.");

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
	public void start()
	{
		running=true;
		respondThread.start();
		
	}

}
