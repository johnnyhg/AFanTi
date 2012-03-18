package AFanTi.Estimate;

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
	

}
