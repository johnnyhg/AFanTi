package AFanTi.DataModel;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.WriterAppender;

import AFanTi.RMI.RMI;

public class testDataModelMaster2 {
	
	  private static Logger logger = Logger.getLogger(DataModelMaster.class
	 			.getName());
	  
	public static void main(String[] args) throws RemoteException
	{
		
		PropertyConfigurator.configure("log4j.properties");
		
		RMI.start();
		
		GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet\\1M");
			
		DataModelSlaver dataModelSlaver0=new DataModelSlaver(myDataModel);
		dataModelSlaver0.config("AFanTi.DataModelSlaver0.properties");
		dataModelSlaver0.initial();
		dataModelSlaver0.start();
		
		DataModelSlaver dataModelSlaver1=new DataModelSlaver(myDataModel);
		dataModelSlaver1.config("AFanTi.DataModelSlaver1.properties");
		dataModelSlaver1.initial();
		dataModelSlaver1.start();
		
		DataModelMaster dataModelMaster=new DataModelMaster();
		dataModelMaster.config("AFanTi.DataModelMaster.properties");
		dataModelMaster.initial();
		dataModelMaster.start();
			
		DataModelClient dataModelClient=new DataModelClient();
		dataModelClient.config("AFanTi.DataModelClient.properties");
		dataModelClient.initial();
		
		 System.out.println("(1, 1)myDataModel:result="+myDataModel.getRating(1, 1));		     
	     System.out.println("(1, 1)dataModelClient:result="+dataModelClient.getRating(1, 1));
	    
	  
	     System.out.println("(1, 2)myDataModel:result="+myDataModel.getRating(1, 2));
	     System.out.println("(1, 2)dataModelClient:result="+dataModelClient.getRating(1, 2));
	     System.out.println("(1, 2)myDataModel:result="+myDataModel.getRating(1, 2));
	     System.out.println("(1, 2)dataModelClient:result="+dataModelClient.getRating(1, 2));
	     System.out.println("(1, 2)myDataModel:result="+myDataModel.getRating(1, 2));
	     System.out.println("(1, 2)dataModelClient:result="+dataModelClient.getRating(1, 2));
	     System.out.println("(1, 2)myDataModel:result="+myDataModel.getRating(1, 2));
	     System.out.println("(1, 2)dataModelClient:result="+dataModelClient.getRating(1, 2));
	     
	     System.out.println("(1, 2)myDataModel:result="+myDataModel.getRating(1, 2));
	     System.out.println("(1, 2)dataModelClient:result="+dataModelClient.getRating(1, 2));
	     System.out.println("(1, 2)myDataModel:result="+myDataModel.getRating(1, 2));
	     System.out.println("(1, 2)dataModelClient:result="+dataModelClient.getRating(1, 2));
	     
	    System.out.println("(1, 3)myDataModel:result="+myDataModel.getRating(1, 3));
	    System.out.println("(1, 3)dataModelClient:result="+dataModelClient.getRating(1, 3));
	  
	    System.out.println("(1, 4)myDataModel:result="+myDataModel.getRating(1, 4));
	    System.out.println("(1, 4)dataModelClient:result="+dataModelClient.getRating(1, 4));
	    System.out.println("(1, 5)myDataModel:result="+myDataModel.getRating(1, 5));
	    System.out.println("(1, 5)dataModelClient:result="+dataModelClient.getRating(1, 5));
	    System.out.println("(1, 6)myDataModel:result="+myDataModel.getRating(1, 6));
	    System.out.println("(1, 6)dataModelClient:result="+dataModelClient.getRating(1, 6));
	    System.out.println("(1, 7)myDataModel:result="+myDataModel.getRating(1, 7));
	    System.out.println("(1, 7)dataModelClient:result="+dataModelClient.getRating(1, 7));
	    System.out.println("(1, 8)myDataModel:result="+myDataModel.getRating(1, 8));
	    System.out.println("(1, 8)dataModelClient:result="+dataModelClient.getRating(1, 8));
	    System.out.println("(1, 9)myDataModel:result="+myDataModel.getRating(1, 9));
	    System.out.println("(1, 9)dataModelClient:result="+dataModelClient.getRating(1, 9));
	    System.out.println("(1, 10)myDataModel:result="+myDataModel.getRating(1, 10));
	    System.out.println("(1, 10)dataModelClient:result="+dataModelClient.getRating(1, 10));
	     System.out.println("*************   end   ****************");
	     
	     while(true)
	     {
	    	 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info(".");
	     }
	     /*
	     System.out.println("*************   next   ****************");
	       System.out.println("(1, 3)myDataModel:result-"+myDataModel.getRating(1, 3));
	     System.out.println("(1, 3)dataModelClient:result="+dataModelClient.getRating(1, 3));
	    System.out.println("(1, 3)myDataModel:result-"+myDataModel.getRating(1, 3));
	     System.out.println("(1, 3)dataModelClient:result="+dataModelClient.getRating(1, 3));
	     System.out.println("*************   next   ****************");
	     System.out.println("(1, 4)myDataModel:result-"+myDataModel.getRating(1, 4));
	     System.out.println("(1, 4)dataModelClient:result="+dataModelClient.getRating(1, 4));
	     System.out.println("*************   next   ****************");
	     System.out.println("(1,10)myDataModel:result-"+myDataModel.getRating(1,10));
	     System.out.println("(1,10)dataModelClient:result="+dataModelClient.getRating(1, 10));
	     
	*/
	
	     
	     
	    // flushAllLogs();
	   //  System.out.flush();
	}
	public static void flushAllLogs()
	{
	    try
	    {
	       
	        Enumeration currentLoggers = LogManager.getLoggerRepository().getCurrentLoggers();
	        while(currentLoggers.hasMoreElements())
	        {
	            Object nextLogger = currentLoggers.nextElement();
	            if(nextLogger instanceof Logger)
	            {
	                Logger currentLogger = (Logger) nextLogger;
	                Enumeration allAppenders = currentLogger.getAllAppenders();
	                while(allAppenders.hasMoreElements())
	                {
	                    Object nextElement = allAppenders.nextElement();
	                    if(nextElement instanceof WriterAppender)
	                    {
	                    	WriterAppender Appender = (WriterAppender) nextElement;
	                    	Appender.setImmediateFlush(true);
	                    	 currentLogger.info("FLUSH");
	                      
	                    }
	                }
	            }
	        }
	    }
	    catch(RuntimeException e)
	    {
	        System.err.println("Failed flushing logs");
	    }
	}
}
