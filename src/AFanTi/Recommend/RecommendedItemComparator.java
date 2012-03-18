package AFanTi.Recommend;

import java.util.Comparator;

import AFanTi.Neighborhood.Neighborhood;

public class RecommendedItemComparator implements Comparator<RecommendedItem> {

	@Override
	public int compare(RecommendedItem item1, RecommendedItem item2) {
		// TODO Auto-generated method stub
		if(item1.estRating>item2.estRating)
			return 1;
		else if(item1.estRating<item2.estRating)
			return -1;
			
		return 0;
	}

}
