package AFanTi.DataModel;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.PropertyConfigurator;

public class testItemDataModelServer {

		public static void main(String[] args)
		{
			PropertyConfigurator.configure("log4j.properties");
			
			
			GeneralItemBasedDataModel myDataModel=new GeneralItemBasedDataModel();
			myDataModel.loadFromDir("E:\\DataSet\\ratings_23M.dat.base2");
			
			try {
				LocateRegistry.createRegistry(1099);

				System.out.println("RMI server start..");
				ItemDataModelServer server = new ItemDataModelServer(myDataModel);

				Naming.rebind("ItemDataModelServer", server);
				System.out.println("ItemDataModelServer rebind ok.");
				System.out.println("bind  server instance  at RMI locale:1099");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
