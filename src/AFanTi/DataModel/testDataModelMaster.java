package AFanTi.DataModel;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.PropertyConfigurator;

public class testDataModelMaster {
	
	public static void main(String[] args) throws RemoteException
	{
		PropertyConfigurator.configure("log4j.properties");
		GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet\\1M");
		
		DataModelMaster dataModelMaster=new DataModelMaster();
		dataModelMaster.config("");
		dataModelMaster.initial();
		dataModelMaster.start();
		
		DataModelSlaver dataModelSlaver=new DataModelSlaver(myDataModel);
		dataModelSlaver.start();
		
		DataModelClient dataModelClient=new DataModelClient();
		
		
		try {
			LocateRegistry.createRegistry(1099);

			System.out.println("RMI server start..");
			

			Naming.rebind("DataModelMaster", dataModelMaster);	
			System.out.println("DataModelMaster rebind ok.");
			
			Naming.rebind("DataModelSlaver", dataModelSlaver);	
			System.out.println("DataModelSlaver rebind ok.");
			
			Naming.rebind("DataModelClient", dataModelClient);	
			System.out.println("DataModelClient rebind ok.");
			
			
			String DataModelMasterURL="//localhost:1099/DataModelMaster";
			String DataModelSlaverURL="//localhost:1099/DataModelSlaver";
			String DataModelClientURL="//localhost:1099/DataModelClient";
			
			dataModelMaster.addDataModelSlaver(dataModelSlaver);
			dataModelMaster.setRMI_URL(DataModelMasterURL);
			
			dataModelMaster.getRating(1, 3,DataModelClientURL);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
