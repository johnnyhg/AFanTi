package AFanTi.Neighborhood;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.Arrays;
import org.ylj.math.Vector;

import AFanTi.DataModel.ItemBasedDataModel;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.PearsonCorrelationSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testKNNeighborhoodSelecter {

	public static void main(String[] str) {
		
		PropertyConfigurator.configure("log4j.properties");

		ItemBasedDataModel myDataModel = new ItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet");
		
		SimilarityComputer SimilarityComputer =new PearsonCorrelationSimilarityComputer();
		SimilarityComputer SimilarityComputer2 =new CosineSimilarityComputer();
		//SimilarityComputer SimilarityComputer =new CosineSimilarityComputer();
		KNNeighborhoodSelecter neighborhoodSelector = new KNNeighborhoodSelecter(SimilarityComputer2,myDataModel,10);
		
		Vector itemV1=myDataModel.getItemVector(1);
		System.out.println(itemV1);
		Vector itemV2=myDataModel.getItemVector(2);
		System.out.println(itemV2);
		
		Vector itemV3=myDataModel.getItemVector(3);
		System.out.println(itemV3);
		Vector itemV6=myDataModel.getItemVector(6);
		System.out.println(itemV6);
		Vector itemV7=myDataModel.getItemVector(7);
		System.out.println(itemV7);
		Vector itemV247=myDataModel.getItemVector(247);
		System.out.println(itemV247);
		Neighborhood[] neighborhoods=neighborhoodSelector.getNeighborhoodsOfItem(itemV6, 1);
		
		System.out.println(Arrays.toString(neighborhoods));
		
		//System.out.println(itemV1);
	//	System.out.println(itemV247);
		//double sim=SimilarityComputer.computeSimilarity(itemV1, itemV247);
	//	System.out.println("cos..");
	//	double sim2=SimilarityComputer2.computeSimilarity(itemV1, itemV247);
	//	System.out.println(sim);
	//	System.out.println(sim2);
	
	}

}
