package AFanTi.Neighborhood;

import org.apache.log4j.PropertyConfigurator;
import org.apache.mahout.math.Arrays;

import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

import AFanTi.DataModel.GeneralItemBasedDataModel;
import AFanTi.Similarity.CosineSimilarityComputer;
import AFanTi.Similarity.PearsonCorrelationSimilarityComputer;
import AFanTi.Similarity.SimilarityComputer;

public class testKNNeighborhoodSelecter {

	public static void main(String[] str) {
		
		PropertyConfigurator.configure("log4j.properties");

		GeneralItemBasedDataModel myDataModel = new GeneralItemBasedDataModel();
		myDataModel.loadFromDir("E:\\DataSet\\testDataSet");
		
		SimilarityComputer SimilarityComputer =new PearsonCorrelationSimilarityComputer();
		SimilarityComputer SimilarityComputer2 =new CosineSimilarityComputer();
		
		//SimilarityComputer SimilarityComputer =new CosineSimilarityComputer();
		ItemKNNeighborhoodSelecter neighborhoodSelector2 = new ItemKNNeighborhoodSelecter(SimilarityComputer2,myDataModel,10);
		ItemKNNeighborhoodSelecter1 neighborhoodSelector = new ItemKNNeighborhoodSelecter1(SimilarityComputer2,myDataModel,10);
	
		Vector itemV6=myDataModel.getItemVector(6);
		//System.out.println(itemV6);

		
		//System.out.println(Arrays.toString(neighborhoods));
		UTimeInterval.startNewInterval();
		int i=0;
		int loop=10000;
		for(;i<loop;i++)
		{
			Neighborhood[] neighborhoods=neighborhoodSelector.getNeighborhoodsOfItem(itemV6, 1);
		}
		System.out.println("Find  neighborhoodSelector cost ="+UTimeInterval.endInterval()/loop+"'us");
		
		UTimeInterval.startNewInterval();
		for(i=0;i<loop;i++)
		{
			Neighborhood[] neighborhoods=neighborhoodSelector2.getNeighborhoodsOfItem(itemV6, 1);
		}
		System.out.println("Find neighborhoodSelector2 cost ="+UTimeInterval.endInterval()/loop+"'us");
		
		//System.out.println(itemV1);
	//	System.out.println(itemV247);
		//double sim=SimilarityComputer.computeSimilarity(itemV1, itemV247);
	//	System.out.println("cos..");
	//	double sim2=SimilarityComputer2.computeSimilarity(itemV1, itemV247);
	//	System.out.println(sim);
	//	System.out.println(sim2);
	
	}

}
