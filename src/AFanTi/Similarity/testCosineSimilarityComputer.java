package AFanTi.Similarity;

import org.ylj.math.Vector;

public class testCosineSimilarityComputer {
	public static void main(String[] args)
	{
		Vector myVector1=new Vector(3);
		myVector1.setVectorID(1);
		
		myVector1.setDimensionOfIndex(0, 1);
		myVector1.setValueOfIndex(0, 1.1F);
		
		myVector1.setDimensionOfIndex(1, 2);
		myVector1.setValueOfIndex(1, 1.2F);
		
		myVector1.setDimensionOfIndex(2, 3);
		myVector1.setValueOfIndex(2, 1.3F);
		
		Vector myVector2=new Vector(2);
		myVector2.setVectorID(2);
		
		myVector2.setDimensionOfIndex(0, 1);
		myVector2.setValueOfIndex(0, 1.5F);
		
		myVector2.setDimensionOfIndex(1, 3);
		myVector2.setValueOfIndex(1, 1.6F);
	
		//System.out.println(	myVector.getLength());
		
		System.out.println(	"sortByValue");
		myVector1.print();
		myVector2.print();
		double result=	CosineSimilarityComputer.computeSimilarity(myVector1, myVector2);
		System.out.println(	"result="+result);
	}
}
