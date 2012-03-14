package AFanTi.DataModel;

import org.apache.log4j.PropertyConfigurator;
import org.ylj.math.Vector;

public class testItemBasedDataModel {
	public static void printArray(long[]  arrary)
	{
		System.out.print(":");
		for(long l:arrary)
		{
			System.out.print(" ["+l+"]");
		}
		System.out.println("#");
	}
	public static void main(String[] argvs) {
		PropertyConfigurator.configure("log4j.properties");
		
		
		ItemBasedDataModel myDataModel=new ItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet");
		Vector v=myDataModel.getItemVector(2);
		v.print();
		myDataModel.setRating(312, 2, (float)2.9);
		v=myDataModel.getItemVector(2);
		v.print();
		
		myDataModel.removeRating(1, 2);
		v=myDataModel.getItemVector(2);
		v.print();
		myDataModel.setRating(312, 4, (float)2.9);
		v=myDataModel.getItemVector(4);
		v.print();
		
		myDataModel.removeRating(312, 4);
		
		/*
		long[] user=myDataModel.getUserArrary(2);
		printArray(user);
		
		
		user=myDataModel.getUserArrary(2);
		printArray(user);
		*/
		
	}
}
