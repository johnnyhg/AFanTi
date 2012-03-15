package AFanTi.Similarity;

import org.ylj.math.Vector;

public class PearsonCorrelationSimilarityComputer implements SimilarityComputer{
	
	public  double computeSimilarity(Vector v_X, Vector v_Y) {
		// TODO Auto-generated method stub
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

		double sumX = 0.0;
		double sumY = 0.0;
		
		double sumX2 = 0.0;
		double sumY2 = 0.0;
		
		int count=0;
		
		double sumXY = 0.0;
		
		double sumX_XY = 0.0;
		double sumY_XY = 0.0;
		double sumX2_XY = 0.0;
		double sumY2_XY = 0.0;

		while (true) {
			if (v_X.getDimensionOfIndex(XIndex) == v_Y
					.getDimensionOfIndex(YIndex)) {
				
				sumX+= v_X.getValueOfIndex(XIndex);
				sumY+= v_Y.getValueOfIndex(YIndex);

				sumX2 += v_X.getValueOfIndex(XIndex)
						* v_X.getValueOfIndex(XIndex);
				
				sumY2 += v_Y.getValueOfIndex(YIndex)
						* v_Y.getValueOfIndex(YIndex);
				
				
				sumXY += v_X.getValueOfIndex(XIndex)
				* v_Y.getValueOfIndex(YIndex);
				sumX_XY+= v_X.getValueOfIndex(XIndex);
				sumY_XY+= v_Y.getValueOfIndex(YIndex);
				
				sumX2_XY+=v_X.getValueOfIndex(XIndex)*v_X.getValueOfIndex(XIndex);
				sumY2_XY+=v_Y.getValueOfIndex(YIndex)*v_Y.getValueOfIndex(YIndex);
				
				count++;
				XIndex++;
				YIndex++;

			} else if (v_X.getDimensionOfIndex(XIndex) > v_Y
					.getDimensionOfIndex(YIndex)) {
				sumY2 += v_Y.getValueOfIndex(YIndex)
						* v_Y.getValueOfIndex(YIndex);
				sumY+= v_Y.getValueOfIndex(YIndex);
				
				YIndex++;
				

			} else if (v_X.getDimensionOfIndex(XIndex) < v_Y
					.getDimensionOfIndex(YIndex)) {
				sumX2 += v_X.getValueOfIndex(XIndex)
						* v_X.getValueOfIndex(XIndex);
				sumX+= v_X.getValueOfIndex(XIndex);
				
				XIndex++;
				
			}

			if (XIndex == V_X_LENGTH || YIndex == V_Y_LENGTH)
				break;
		}

		while (XIndex < V_X_LENGTH) {
			sumX2 += v_X.getValueOfIndex(XIndex) * v_X.getValueOfIndex(XIndex);
			sumX+= v_X.getValueOfIndex(XIndex);
			
			XIndex++;
		}

		while (YIndex < V_Y_LENGTH) {
			sumY2 += v_Y.getValueOfIndex(YIndex) * v_Y.getValueOfIndex(YIndex);
			sumY+= v_Y.getValueOfIndex(YIndex);
			
			YIndex++;
		}
		
		double meanX=sumX/V_X_LENGTH;
		double meanY=sumY/V_Y_LENGTH;
		
		System.out.println("sumX="+sumX2);
		System.out.println("meanX="+meanX);
		
		System.out.println("sumY="+sumX2);
		System.out.println("meanY="+meanY);
		
		System.out.println("sumX2="+sumX2);
		System.out.println("sumY2="+sumY2);
		
		System.out.println("count="+count);
		System.out.println("sumX_XY="+sumX_XY);
		System.out.println("sumY_XY="+sumY_XY);
		System.out.println("sumX2_XY="+sumX2_XY);
		System.out.println("sumY2_XY="+sumY2_XY);
		
		System.out.println("sumXY="+sumXY);
		
		double numerator=sumXY-meanX*sumY_XY-meanY*sumX_XY+count*meanX*meanY;
		double denominator=Math.sqrt(sumX2_XY-2*meanX*sumX_XY+count*meanX*meanX)*Math.sqrt(sumY2_XY-2*meanY*sumY_XY+count*meanY*meanY);
		
		double result=numerator/ denominator;
		
		System.out.println("result="+result);

		return result;
	}
}
