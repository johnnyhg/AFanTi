package AFanTi.DataModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import org.apache.log4j.Logger;

import AFanTi.RMI.RMI;

public class DataModelClient extends UnicastRemoteObject implements
		DataModelClientProxy, UpdateDataModel {

	DataModelMasterProxy masterProxy;
	String RMI_URL;

	Properties configProperties;

	boolean stateResult = false;
	Float valueResult = Float.NaN;

	private static Logger logger = Logger.getLogger(DataModelClient.class
			.getName());

	public DataModelClient() throws RemoteException {

		// TODO Auto-generated constructor stub
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
		logger.info("start inital.");
		String MasterRMI_URL = configProperties
				.getProperty("AFanTi.DataModelClient.Master.RMI_URL");

		masterProxy = getMasterProxy(MasterRMI_URL);
		try {
			int client_id = masterProxy.getClientID();

			RMI_URL = AFanTi.RMI.RMI.RMI_URL + "DataModelClient_" + client_id;

			RMI.bind(RMI_URL, this);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("inital complete.");

	}

	public DataModelMasterProxy getMasterProxy(String ProxyRMI_URL) {
		DataModelMasterProxy masterProxy;
		try {

			masterProxy = (DataModelMasterProxy) Naming.lookup(ProxyRMI_URL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("RMI:error " + ProxyRMI_URL);

			return null;
		}
		return masterProxy;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void setResult(long command_ID, boolean state) {
		logger.info("setResult(command_ID:" + command_ID + ",state:" + state
				+ ")");
		stateResult = state;
		synchronized (this) {
			this.notifyAll();
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void setResult(long command_ID, Float value) {
		logger.info("setResult(command_ID:" + command_ID + ",value:" + value
				+ ")");
		// TODO Auto-generated method stub
		valueResult = value;
		synchronized (this) {
			this.notifyAll();
		}
	}

	@Override
	public boolean setRating(long userID, long itemID, float rating) {

		try {
			if (masterProxy.setRating(userID, itemID, rating, RMI_URL) == false)
				return false;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		synchronized (this) {
			try {
				logger.info("thread wait...");
				this.wait();
				logger.info("thread weaken...");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return stateResult;

		// TODO Auto-generated method stub

	}

	
	/**
	 * return null 
	 * return Float.NaN  inconsistency
	 */
	@Override
	public Float getRating(long userID, long itemID) {
		// TODO Auto-generated method stub

		try {
			if (masterProxy.getRating(userID, itemID, RMI_URL) == false)
				return null;

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		synchronized (this) {
			try {
				logger.info("thread wait...");
				this.wait();
				logger.info("thread weaken...");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return valueResult;
	}

	@Override
	public boolean removeRating(long userID, long itemID) {

		try {
			if (masterProxy.removeRating(userID, itemID, RMI_URL) == false)
				return false;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		synchronized (this) {
			try {
				logger.info("thread wait...");
				this.wait();
				logger.info("thread weaken...");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return stateResult;

	}

}
