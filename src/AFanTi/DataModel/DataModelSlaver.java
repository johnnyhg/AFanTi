package AFanTi.DataModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.ylj.common.UTimeInterval;

import AFanTi.RMI.RMI;

public class DataModelSlaver extends UnicastRemoteObject implements
		DataModelSlaverProxy {

	Queue<DataChangeRequest> waitToOperateQueue = new LinkedList<DataChangeRequest>();
	Queue<DataChangeRequest> WaitToRespondQueue = new LinkedList<DataChangeRequest>();

	private static Logger logger = Logger.getLogger(DataModelSlaver.class
			.getName());

	public static final int SET = 1;
	public static final int REMOVE = 2;
	public static final int GET = 3;

	DataModel dataModel;

	boolean running;
	DoRespondThread respondThread;
	DoOperateThread operateThread;

	Properties configProperties;
	String RMI_URL;
	String name;

	public DataModelSlaver(DataModel model) throws RemoteException {
		respondThread = new DoRespondThread();
		respondThread.setName("RespondThread");
		operateThread = new DoOperateThread();
		operateThread.setName("OperateThread");
		dataModel = model;

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
	
	public void initial()
	{
		logger.info("start inital.");
		name = configProperties.getProperty("AFanTi.DataModelSlaver.RMIName");
		
		RMI_URL =RMI.RMI_URL + name;
		
		RMI.bind(RMI_URL, this);
		
		logger.info("inital complete.");
	}



	public class DataChangeRequest {

		long commandID;
		int resultIndex;

		int operateCode;
		long userID;
		long itemID;
		Float rating;

		DataModelMasterProxy receiver;

		public long waitAtTime; // nano

	}

	public void doOperate(DataChangeRequest request) {

		switch (request.operateCode) {

		case SET:
			dataModel.setRating(request.userID, request.itemID, request.rating);
			break;

		case REMOVE:
			dataModel.removeRating(request.userID, request.itemID);
			break;
		case GET:
			request.rating = dataModel.getRating(request.userID, request.itemID);			
			break;

		}

	}

	public DataModelMasterProxy getMasterProxy(String url) {
		DataModelMasterProxy masterProxy = null;
		try {

			masterProxy = (DataModelMasterProxy) Naming.lookup(url);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("RMI:error " + url);

			return null;
		}
		return masterProxy;
	}

	@Override
	public boolean setRating(long userID, long itemID, float rating,
			long command_ID, int resultIndex, String MASTER_RMI_URL)
			throws RemoteException {

		DataModelMasterProxy masterProxy = getMasterProxy(MASTER_RMI_URL);
		if (masterProxy == null)
			return false;

		logger.info("receive a SetRating command command_ID:"+command_ID+",resultIndex:"+resultIndex+",userID:"+userID+",itemID:"+itemID+",rating:"+rating);
		
		DataChangeRequest aNewDataChangeRequest = new DataChangeRequest();
		aNewDataChangeRequest.commandID = command_ID;
		aNewDataChangeRequest.itemID = itemID;
		aNewDataChangeRequest.operateCode = SET;
		aNewDataChangeRequest.rating = rating;
		aNewDataChangeRequest.receiver = masterProxy;
		aNewDataChangeRequest.resultIndex = resultIndex;
		aNewDataChangeRequest.userID = userID;
		aNewDataChangeRequest.waitAtTime = System.nanoTime();

		synchronized (waitToOperateQueue) {
			waitToOperateQueue.add(aNewDataChangeRequest);
			waitToOperateQueue.notifyAll();
		}
		return true;
	}

	@Override
	public boolean removeRating(long userID, long itemID, long command_ID,
			int resultIndex, String MASTER_RMI_URL) throws RemoteException {

		DataModelMasterProxy masterProxy = getMasterProxy(MASTER_RMI_URL);
		if (masterProxy == null)
			return false;
		logger.info("receive a RemoveRating command command_ID:"+command_ID+",resultIndex:"+resultIndex+",userID:"+userID+",itemID:"+itemID);
		
		DataChangeRequest aNewDataChangeRequest = new DataChangeRequest();
		aNewDataChangeRequest.commandID = command_ID;
		aNewDataChangeRequest.itemID = itemID;
		aNewDataChangeRequest.operateCode = REMOVE;
		aNewDataChangeRequest.receiver = masterProxy;
		aNewDataChangeRequest.resultIndex = resultIndex;
		aNewDataChangeRequest.userID = userID;
		aNewDataChangeRequest.waitAtTime = System.nanoTime();

		synchronized (waitToOperateQueue) {
			waitToOperateQueue.add(aNewDataChangeRequest);
			waitToOperateQueue.notifyAll();
		}
		return true;
	}

	@Override
	public boolean getRating(long userID, long itemID, long command_ID,
			int resultIndex, String MASTER_RMI_URL) throws RemoteException {

		DataModelMasterProxy masterProxy = getMasterProxy(MASTER_RMI_URL);
		if (masterProxy == null)
			return false;
		logger.info("receive a GetRating command command_ID:"+command_ID+",resultIndex:"+resultIndex+",userID:"+userID+",itemID:"+itemID);
		
		DataChangeRequest aNewDataChangeRequest = new DataChangeRequest();
		aNewDataChangeRequest.commandID = command_ID;
		aNewDataChangeRequest.itemID = itemID;
		aNewDataChangeRequest.operateCode = GET;

		aNewDataChangeRequest.receiver = masterProxy;
		aNewDataChangeRequest.resultIndex = resultIndex;
		aNewDataChangeRequest.userID = userID;
		aNewDataChangeRequest.waitAtTime = System.nanoTime();

		synchronized (waitToOperateQueue) {
			waitToOperateQueue.add(aNewDataChangeRequest);
			waitToOperateQueue.notifyAll();
		}

		return true;
	}

	private class DoOperateThread extends Thread {

		long DoOperateCount = 0;

		public DataChangeRequest waitARequest() {
			DataChangeRequest aRequest = null;

			// wait for a work
			while (true) {

				synchronized (waitToOperateQueue) {

					if (waitToOperateQueue.size() > 0) {

						aRequest = waitToOperateQueue.poll();
						break;

					} else {

						try {
							logger.info(" thread wait ...");
							waitToOperateQueue.wait();
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

			logger.info("Thread start running.");

			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */

				DataChangeRequest aRequest = waitARequest();

				int time = UTimeInterval.startNewInterval();

				logger.info(" do a operate job");

				doOperate(aRequest);

				++DoOperateCount;
				synchronized (WaitToRespondQueue) {
					WaitToRespondQueue.add(aRequest);
					WaitToRespondQueue.notifyAll();
				}

				logger.info(" do a operate job complement. itemID:"
						+ aRequest.itemID + " userID:" + aRequest.userID
						+ " cost:" + UTimeInterval.endInterval(time) + "'us");

			}

		}
	}

	private class DoRespondThread extends Thread {

		long DoRespondCount = 0;

		public void sendResult(DataChangeRequest request) {
			DataModelMasterProxy receiver = request.receiver;

			switch (request.operateCode) {
			case SET:
			case REMOVE:
				try {
				
					receiver.setCommandResult(request.commandID,
							request.resultIndex, true);
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case GET:
				try {
					receiver.setCommandResult(request.commandID,
							request.resultIndex, request.rating);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			}

		}

		public DataChangeRequest waitARequest() {
			DataChangeRequest aRequest = null;

			// wait for a work
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

			logger.info("Thread start running.");
			while (running) {

				/*
				 * sleep until resultsNeighborsfrom neighborhood server all ok
				 * just
				 */

				DataChangeRequest ARequest = waitARequest();
				logger.info("#Respond a Request .");

				sendResult(ARequest);

				DoRespondCount++;
				long timeInterval = (System.nanoTime() - ARequest.waitAtTime) / 1000;

				logger.info("# Respond complement command_ID: "
						+ ARequest.commandID + "  cost:" + timeInterval + "'us");
				/*
				 * remove aCall
				 */

			}

		}

	}

	public void start() {
		running = true;
		operateThread.start();
		respondThread.start();

	}

<<<<<<< HEAD

=======
	
>>>>>>> 0f0c485d0fc83957fef42e0a5886dd0f5d40b11c

}
