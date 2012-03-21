package AFanTi.Similarity;

import org.ylj.common.UTimeInterval;
import org.ylj.math.Vector;

public class CosineSimilarityComputer implements SimilarityComputer {

	public double computeSimilarity(Vector v_X, Vector v_Y) {
		// TODO Auto-generated method stub
		if (v_X == null || v_Y == null)
			return 0;

		final long V_X_LENGTH = v_X.getLength();
		final long V_Y_LENGTH = v_Y.getLength();

		if (V_X_LENGTH < 1 || V_Y_LENGTH < 1)
			return 0;
		

		int XIndex = 0;
		int YIndex = 0;


		double sumXY = 0.0;
		double sumX2_XY = 0.0;
		double sumY2_XY = 0.0;
		
		
		while (true) {
			
			long xDemsion = v_X.getDimensionOfIndex(XIndex);
			long yDemsion = v_Y.getDimensionOfIndex(YIndex);
			
			
			if (xDemsion == yDemsion) {
				
				double x = v_X.getValueOfIndex(XIndex);
				double y = v_Y.getValueOfIndex(YIndex);

				double x2 = x * x;
				double y2 = y * y;
				double xy = x * y;

				sumXY += xy;				
				sumX2_XY+=x2;
				sumY2_XY+=y2;
				
				XIndex++;
				YIndex++;

			} else if (xDemsion > yDemsion) {
				
				YIndex++;

			} else if (xDemsion < yDemsion) {
				
				XIndex++;
			}

			if (XIndex == V_X_LENGTH || YIndex == V_Y_LENGTH)
				break;
		}

		double result = sumXY / (Math.sqrt(sumX2_XY) * Math.sqrt(sumY2_XY));
		
		
		return result;
	}

}
