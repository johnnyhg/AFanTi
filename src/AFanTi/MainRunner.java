package AFanTi;
import java.io.File;
import java.io.IOException;
import AFanTi.DataModel.MemDataModel;

public class MainRunner {
	
	public static void main(String[]agvs)
	{
		 MemDataModel dataModel=new MemDataModel();
		 
		 try {
			dataModel.loadFromFile(new File("E:\\DataSet\\ml-100k\\u1.base"),"[\t ]");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
}
