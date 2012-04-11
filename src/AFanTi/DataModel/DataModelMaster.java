package AFanTi.DataModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.apache.mahout.math.Arrays;

import AFanTi.Estimate.EstimatedRatingReceiverProxy;
import AFanTi.Estimate.ItemBasedRatingEstimaterServer;
import AFanTi.RMI.RMI;
import AFanTi.Recommend.AsyncRecommenditionReceiverProxy;
import AFanTi.Recommend.Call;
import AFanTi.Recommend.RecommendedItem;

public class DataModelMaster extends UnicastRemoteObject implements
		DataModelMasterProxy {

	int clientSerial = 0;
	long commandSerial = 0;

	Queue<CommandResult> commandResultQueue = new LinkedList<CommandResult>();
	Map<Long, CommandResult> commandResultMap = new HashMap<Long, CommandResult>();

	Queue<CommandResult> WaitToRespondQueue = new LinkedList<CommandResult>();

	List<DataModelSlaverProxy> slavers = new LinkedList<DataModelSlaverProxy>();

	String RMI_URL;

	private static Logger logger = Logger.getLogger(DataModelMaster.class
			.getName());

	boolean running;
	DoRespondThread respondThread;
	DoTimeOutCheckThread timeOutCheckThread;

	Properties configProperties;

	public DataModelMaster() throws RemoteException {

		respondThread = new DoRespondThread();
		respondThread.setName("RespondThread");
		timeOutCheckThread = new DoTimeOutCheckThread();
	}

	public void config(String configFile) {

		configProperties = new Properties();
		InputStream is;
		try {
			is = new FileInputStream(new File(configFile));
			configProperties.load(is);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

	}

	public void initial() {
		/*
		 * String master_RMI_URL
		 * =configProperties.getProperty("AFanTi.DataModelMaster.RMI_URL");
		 * if(master_RMI_URL==null) {
		 * logger.error("Cannot AFanTi.DataModelMaster.RMI_URL properties:");
		 * return ; }
		 */
		logger.info("start inital.");
		String rmiName = configProperties
				.getProperty("AFanTi.DataModelMaster.RMIName");
		RMI_URL = AFanTi.RMI.RMI.RMI_URL + rmiName;

		RMI.bind(RMI_URL, this);

		String Slavers = configProperties
				.getProperty("AFanTi.DataModelMaster.Slavers");
		String[] IDs = Slavers.split(",");

		for (int i = 0; i < IDs.length; i++) {

			String slaver_RMI_URL = configProperties
					.getProperty("AFanTi.DataModelMaster.Slaver_" + IDs[i]
							+ ".RMI_URL");

			DataModelSlaverProxy aSlaver = getSlaverProxy(slaver_RMI_URL);

			if (aSlaver == null) {
				logger.error("Cannot DataModelSlaver RMI:" + slaver_RMI_URL);
				continue;
			}

			this.addDataModelSlaver(aSlaver);
			logger.info("Add DataModelSlaver  Slaver_" + IDs[i]);

		}
		logger.info("inital complete.");
	}

	public void setRMI_URL(String url) {
		RMI_URL = url;
	}

	public long getCommandSerial() {

		if (commandSerial == Long.MAX_VALUE)
			commandSerial = 0;
		return ++commandSerial;

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

	public DataModelSlaverProxy getSlaverProxy(String ProxyRMI_URL) {
		DataModelSlaverProxy slaverProxy;
		try {

			slaverProxy = (DataModelSlaverProxy) Naming.lookup(ProxyRMI_URL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("RMI:error " + ProxyRMI_URL);

			return null;
		}
		return slaverProxy;
	}

	public DataModelClientProxy getClientProxy(String ProxyRMI_URL) {
		DataModelClientProxy clientProxy;
		try {

			clientProxy = (DataModelClientProxy) Naming.lookup(ProxyRMI_URL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("RMI:error " + ProxyRMI_URL);

			return null;
		}
		return clientProxy;
	}

	@Override
	public boolean setRating(long userID, long itemID, float rating,
			String client_RMI_URL) {
		// TODO Auto-generated method stub
		DataModelClientProxy clientProxy = getClientProxy(client_RMI_URL);
		if (clientProxy == null)
			return false;

		long thisCommandID = getCommandSerial();
		CommandResult newCommandResult = new CommandResult(thisCommandID,
				slavers.size(), false);
		newCommandResult.resultReceiverProxy = clientProxy;
		newCommandResult.waitAtNanoTime = System.nanoTime();

		synchronized (commandResultMap) {
			commandResultQueue.add(newCommandResult);
			commandResultMap.put(thisCommandID, newCommandResult);
		}
		
		if (slavers.size() == 0) {
			logger.error("slavers.size()==0");
			
			synchronized (commandResultMap) {
				commandResultQueue.remove(newCommandResult);
				commandResultMap.remove(thisCommandID);
			}
			
			return false;
		}
		
		for (int i = 0; i < slavers.size(); i++) {
			DataModelSlaverProxy aSlaver = slavers.get(i);
			try {
				if (aSlaver.setRating(userID, itemID, rating, thisCommandID, i,
						RMI_URL) == false) {
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
		
		return true;
	}

	@Override
	public boolean removeRating(long userID, long itemID, String client_RMI_URL) {

		DataModelClientProxy clientProxy = getClientProxy(client_RMI_URL);
		if (clientProxy == null)
			return false;

		long thisCommandID = getCommandSerial();
		
		CommandResult newCommandResult = new CommandResult(thisCommandID,
				slavers.size(), false);
		newCommandResult.resultReceiverProxy = clientProxy;
		newCommandResult.waitAtNanoTime = System.nanoTime();

		synchronized (commandResultMap) {
			commandResultQueue.add(newCommandResult);
			commandResultMap.put(thisCommandID, newCommandResult);
		}
		
		if (slavers.size() == 0) {
			logger.error("slavers.size()==0");
			
			synchronized (commandResultMap) {
				commandResultQueue.remove(newCommandResult);
				commandResultMap.remove(thisCommandID);
			}
			
			return false;
		}
		
		
		for (int i = 0; i < slavers.size(); i++) {
			DataModelSlaverProxy aSlaver = slavers.get(i);
			try {
				if (aSlaver.removeRating(userID, itemID, thisCommandID, i,
						RMI_URL) == false) {
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
		
		return true;
	}

	@Override
	public boolean getRating(long userID, long itemID, String client_RMI_URL) {

		logger.info("getRating(userID:" + userID + ",itemID:" + itemID
				+ ",client_RMI_URL:" + client_RMI_URL + ")");

		DataModelClientProxy clientProxy = getClientProxy(client_RMI_URL);

		if (clientProxy == null) {
			logger.error("clientProxy==null");
			return false;
		}

		// TODO Auto-generated method stub
		long thisCommandID = getCommandSerial();

		
		CommandResult newCommandResult = new CommandResult(thisCommandID,
				slavers.size(), true);
		newCommandResult.resultReceiverProxy = clientProxy;
		newCommandResult.waitAtNanoTime = System.nanoTime();

		synchronized (commandResultMap) {
			commandResultQueue.add(newCommandResult);
			commandResultMap.put(thisCommandID, newCommandResult);
		}
		
		if (slavers.size() == 0) {
			logger.error("slavers.size()==0");
			
			synchronized (commandResultMap) {
				commandResultQueue.remove(newCommandResult);
				commandResultMap.remove(thisCommandID);
			}
			
			return false;
		}
		
		
		for (int i = 0; i < slavers.size(); i++) {
			DataModelSlaverProxy aSlaver = slavers.get(i);
			try {
				if (aSlaver
						.getRating(userID, itemID, thisCommandID, i, RMI_URL) == false) {
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
		

		return true;
	}

	class CommandResult {
		long commandID;

		boolean[] slaverReslutMark;
		//
		boolean valueResult;
		Float[] slaverReslut;
		boolean[] slaverReslutState;

		DataModelClientProxy resultReceiverProxy;
		long waitAtNanoTime;

		public CommandResult(long comId, int slaverNum, boolean withValue) {

			commandID = comId;
			valueResult = withValue;
			slaverReslutMark = new boolean[slaverNum];

			slaverReslutState = new boolean[slaverNum];
			if (valueResult)
				slaverReslut = new Float[slaverNum];

		}

		public void setState(int resultIndex, boolean state) {
			slaverReslutState[resultIndex] = state;
		}

		public void setValue(int resultIndex, Float value) {
			slaverReslut[resultIndex] = value;
		}

		public void setMark(int resultIndex) {
			slaverReslutMark[resultIndex] = true;
		}

		public void setTime(long nanoTime) {
			waitAtNanoTime = nanoTime;
		}

		public boolean isResultCompletion() {
			
			for (boolean aSlaverMark : slaverReslutMark) {
				if (aSlaverMark == false)
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

		public Float getValue() {
			Float result = slaverReslut[0];
			System.out.println("" + slaverReslut.length);
			
			
			for (Float aSlaverValue : slaverReslut) {

				if(aSlaverValue==null)
				{
					if(result==null)
						continue;
					else
						return Float.NaN;
				}
				else if (!aSlaverValue.equals(result))
				{
					return Float.NaN;
				}
				result = aSlaverValue;

			}
			return result;

		}
		public void printMarks()
		{
			logger.info(Arrays.toString(slaverReslutMark));
		}

	}

	@Override
	public boolean setCommandResult(long commandID, int resultIndex,
			boolean state) {

		logger.info("<<receiver a Result commandID:" + commandID
				+ " resultIndex:" + resultIndex);
		CommandResult targetResult;
		synchronized (commandResultMap) {
			targetResult= commandResultMap.get(commandID);
		}
		if (targetResult == null)
			return false;

		targetResult.setState(resultIndex, state);
		targetResult.setMark(resultIndex);

		if (targetResult.isResultCompletion()) {

			logger.info("<<CommandResult_OK CommandID:" + commandID
					+ " :do commandResultMap commandResultQueue");

			synchronized (commandResultMap) {
				commandResultMap.remove(commandID);
				commandResultQueue.remove(targetResult);
			}

			synchronized (WaitToRespondQueue) {

				WaitToRespondQueue.add(targetResult);
				WaitToRespondQueue.notifyAll();
			}
			logger.info("<<CommandResult_OK CommandID:" + commandID
					+ " :do commandResultMap commandResultQueue complement");
		}
		
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public boolean setCommandResult(long commandID, int resultIndex, Float value) {
		// TODO Auto-generated method stub
		logger.info("<<receiver a Result commandID:" + commandID
				+ " resultIndex:" + resultIndex + " value:" + value);
		
		CommandResult targetResult;
		synchronized (commandResultMap) {
			targetResult= commandResultMap.get(commandID);
		}
		
		logger.info("here:");
		if (targetResult == null)
		{
			logger.info("<<targetResult==null");
			return false;
		}
		
		targetResult.setValue(resultIndex, value);
		targetResult.setMark(resultIndex);

		if (targetResult.isResultCompletion()) {
			logger.info("<<CommandResult_OK CommandID:" + commandID
					+ " :do commandResultMap commandResultQueue");

			synchronized (commandResultMap) {
				commandResultMap.remove(commandID);
				commandResultQueue.remove(targetResult);
			}

			synchronized (WaitToRespondQueue) {

				WaitToRespondQueue.add(targetResult);
				WaitToRespondQueue.notifyAll();

			}
			logger.info("<<CommandResult_OK CommandID:" + commandID
					+ " :do commandResultMap commandResultQueue complement");
			// do something
		}
		else
		{
			logger.info("<<targetResult==not Completion");
			targetResult.printMarks();
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
				logger.info(">>Respond a CommandResult .");

				DataModelClientProxy clientProxy = aCommandResult.resultReceiverProxy;

				if (aCommandResult.valueResult)
					try {
						clientProxy.setResult(aCommandResult.commandID,
								aCommandResult.getValue());

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						logger.info(">>Respond a CommandResult error.");
						e.printStackTrace();
					}
				else
					try {
						clientProxy.setResult(aCommandResult.commandID,
								aCommandResult.getState());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						logger.info(">>Respond a CommandResult error.");
						e.printStackTrace();
					}

				logger.info(">>Respond a CommandResult complement.");

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

	public void start() {
		running = true;
		respondThread.start();

	}

	@Override
	public int getClientID() throws RemoteException {
		// TODO Auto-generated method stub
		if (clientSerial == Integer.MAX_VALUE)
			clientSerial = 0;
		return clientSerial++;
	}

}
