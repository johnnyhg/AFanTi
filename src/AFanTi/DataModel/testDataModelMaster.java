package AFanTi.DataModel;

import org.apache.log4j.PropertyConfigurator;

public class testDataModelMaster {
	
	public static void main(String[] args)
	{
		PropertyConfigurator.configure("log4j.properties");
		DataModelMaster dataModelMaster=new DataModelMaster();
		dataModelMaster.start();
		
		
	}
}
