package AFanTi.Similarity;

import org.ylj.math.Vector;

public class CosineSimilarityComputer   implements SimilarityComputer{

	
	
	public  double computeSimilarity(Vector v_X, Vector v_Y) {
		// TODO Auto-generated method stub
		if (v_X == null || v_Y == null)
			return 0;

		final long V_X_LENGTH = v_X.getLength();
		final long V_Y_LENGTH = v_Y.getLength();

		if (V_X_LENGTH < 1 || V_Y_LENGTH < 1)
			return 0;

		v_X.sortByDimension();
		v_Y.sortByDimension();

		int XIndex = 0;
		int YIndex = 0;

		double sumX2 = 0.0;
		double sumY2 = 0.0;
		double sumXY = 0.0;

		while (true) {
			if (v_X.getDimensionOfIndex(XIndex) == v_Y
					.getDimensionOfIndex(YIndex)) {

				sumX2 += v_X.getValueOfIndex(XIndex)
						* v_X.getValueOfIndex(XIndex);
				sumXY += v_X.getValueOfIndex(XIndex)
						* v_Y.getValueOfIndex(YIndex);
				sumY2 += v_Y.getValueOfIndex(YIndex)
						* v_Y.getValueOfIndex(YIndex);
				XIndex++;
				YIndex++;

			} else if (v_X.getDimensionOfIndex(XIndex) > v_Y
					.getDimensionOfIndex(YIndex)) {
				sumY2 += v_Y.getValueOfIndex(YIndex)
						* v_Y.getValueOfIndex(YIndex);
				YIndex++;

			} else if (v_X.getDimensionOfIndex(XIndex) < v_Y
					.getDimensionOfIndex(YIndex)) {
				sumX2 += v_X.getValueOfIndex(XIndex)
						* v_X.getValueOfIndex(XIndex);
				XIndex++;
			}

			if (XIndex == V_X_LENGTH || YIndex == V_Y_LENGTH)
				break;
		}

		while (XIndex < V_X_LENGTH) {
			sumX2 += v_X.getValueOfIndex(XIndex) * v_X.getValueOfIndex(XIndex);
			XIndex++;
		}

		while (YIndex < V_Y_LENGTH) {
			sumY2 += v_Y.getValueOfIndex(YIndex) * v_Y.getValueOfIndex(YIndex);
			YIndex++;
		}
		
		System.out.println("sumX2="+sumX2);
		System.out.println("sumY2="+sumY2);
		System.out.println("sumXY="+sumXY);
		
		
		double result=sumXY/ Math.sqrt(sumX2)*Math.sqrt(sumY2);
		
		System.out.println("result="+result);

		return result;
	}

}
