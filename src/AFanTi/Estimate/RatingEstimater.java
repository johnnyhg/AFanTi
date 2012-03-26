package AFanTi.Estimate;

import AFanTi.Neighborhood.Neighborhood;

public interface RatingEstimater {
	float estimateRating(float[] ratingArrary,double[] similarityArrary);
	float estimateRating(Neighborhood[] Neighborhoods);
}
