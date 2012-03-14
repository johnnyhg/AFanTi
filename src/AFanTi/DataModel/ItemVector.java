package AFanTi.DataModel;

import org.ylj.math.MathException;
import org.ylj.math.Vector;

public class ItemVector extends Vector {

	public ItemVector(long vID, int d_length) {
		super(vID, d_length);
		// TODO Auto-generated constructor stub
	}

	public long getItemId() {
		return this.getVectorID();
	}

	public boolean containRatingOfUser(long userID) {
		return this.containDimension(userID);
	}

	public Float getRatingOfUser(long userID) {

		return this.getValueOfDimension(userID);

	}

	public void setRatingOfUser(long userID, float newRating) {
		if (this.containDimension(userID)) {

		} else {

		}
	}

}
