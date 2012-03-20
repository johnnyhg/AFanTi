package AFanTi.Similarity;

import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

public class CosineSimilarityComputer2 implements SimilarityComputer {

	public double computeSimilarity(Vector v_X, Vector v_Y) {
		// TODO Auto-generated method stub
		
		
		if (v_X == null || v_Y == null)
			return 0;

		final long V_X_LENGTH = v_X.getLength();
		final long V_Y_LENGTH = v_Y.getLength();

		if (V_X_LENGTH < 1 || V_Y_LENGTH < 1)
			return 0;
		
		
		
		
		
		//v_X.sortByDimension();
		//v_Y.sortByDimension();

		int XIndex = 0;
		int YIndex = 0;

		double sumX2 = 0.0;
		double sumY2 = 0.0;
		double sumXY = 0.0;
	

		while (true) {

			long xDemsion = v_X.getDimensionOfIndex(XIndex);
			long yDemsion = v_Y.getDimensionOfIndex(YIndex);

			if (xDemsion == yDemsion) {

				double x = v_X.getValueOfIndex(XIndex);
				double y = v_Y.getValueOfIndex(YIndex);

				double x2 = x * x;
				double y2 = y * y;
				double xy = x * y;

				sumX2 += x2;
				sumXY += xy;
				sumY2 += y2;



			}
			if (xDemsion >= yDemsion) {

				if (++YIndex == V_Y_LENGTH)
					break;

			}
			if (xDemsion <= yDemsion) {

				if (++XIndex == V_X_LENGTH)
					break;

			}

		}

		// System.out.println("sumX2="+sumX2);
		// System.out.println("sumY2="+sumY2);
		// System.out.println("sumXY="+sumXY);

		// System.out.println(" Math.sqrt(sumX2)="+ Math.sqrt(sumX2));
		// System.out.println("sumY2="+sumY2);
		double result = sumXY / (Math.sqrt(sumX2) * Math.sqrt(sumY2));

		//System.out.println(">>cos2computeSimilarity() cost: "+UTimeInterval.endInterval()+"'us");
		
		// double result = sumXY / (Math.sqrt(sumX2_XY) * Math.sqrt(sumY2_XY));
		// System.out.println("result="+result);

		return result;
	}

}
