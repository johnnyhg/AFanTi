package AFanTi.Estimate;

import AFanTi.Neighborhood.Neighborhood;

public class GeneralRatingEstimater implements RatingEstimater{

	@Override
	public float estimateRating(float[] ratingArrary, double[] similarityArrary) {
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

	@Override
	public float estimateRating(Neighborhood[] Neighborhoods) {
		
		double ratingSum=0.0;
		double similaritySum=0.0;
		
		for(int i=0;i<Neighborhoods.length;i++ )
		{
			similaritySum+=Neighborhoods[i].similarity;
			ratingSum+=Neighborhoods[i].rating*Neighborhoods[i].similarity;
		}
		
		double estimatedRating=ratingSum/similaritySum;
		return (float)estimatedRating;
	}
	

}
