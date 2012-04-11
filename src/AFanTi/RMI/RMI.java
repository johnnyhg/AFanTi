package AFanTi.RMI;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

import AFanTi.DataModel.DataModelMaster;

public class RMI {
	
	public static Registry registry;   
	public static int LocalRMIPort=1099;
	public static String  RMI_URL;

	private static Logger logger = Logger.getLogger(RMI.class
			.getName());
	
	public static void setPort(int newPort)
	{
		LocalRMIPort=newPort;
	}
	
	public static void start()
	{
		java.net.InetAddress ad=null;
		try {
			ad = java.net.InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ;
		}
		RMI_URL="//"+ad.getHostAddress()+":"+LocalRMIPort+"/";
		
		/*
		try {
			registry=LocateRegistry.createRegistry(LocalRMIPort);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		logger.info("RMI server start.. "+RMI_URL);
	}
	public static boolean bind(String rmi_url,Remote o)
	{
		try {
			Naming.rebind(rmi_url, o);
			
			logger.info("bind rmi_url:"+rmi_url+" ok");
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static void stop()
	{
		
	}
}
