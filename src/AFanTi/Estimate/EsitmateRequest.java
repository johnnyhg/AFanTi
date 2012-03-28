package AFanTi.Estimate;

public class EsitmateRequest {
	
	public int  part_K;
	public long callSerial;
	public EstimatedRatingReceiverProxy receiver;
	
	public long[] itemIDs;
	public long userID;
	
	public float[] esitmatedRatings;
	
	
	public long waitAtTime; //nano 
	
	
}
