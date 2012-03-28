package AFanTi.Estimate;

import AFanTi.Neighborhood.Neighborhood;

public class GeneralRatingComputer implements RatingComputer{

	@Override
	public float computeRating(float[] ratingArrary, double[] similarityArrary) {
		// TODO Auto-generated method stub
		double ratingSum=0.0;
		double similaritySum=0.0;
		
		for(int i=0;i<ratingArrary.length;i++ )
		{
			similaritySum+=similarityArrary[i];
			ratingSum+=ratingArrary[i]*similarityArrary[i];
		}
		
		double estimatedRating=ratingSum/similaritySum;
		return (float)estimatedRating;
	}


	

}
