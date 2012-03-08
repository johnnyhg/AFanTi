package AFanTi.DataModel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.common.iterator.CountingIterator;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class UserPreferenceArray implements PreferenceArray {

	private static final int ITEM = 1;
	private static final int VALUE = 2;
	private static final int VALUE_REVERSED = 3;

	private long userID;

	private final long[] itemIds;
	private final float[] values;
	public int used;

	public UserPreferenceArray(int size) {
		this.itemIds = new long[size];
		values = new float[size];
		this.userID = Long.MIN_VALUE; // as a sort of 'unspecified' value
		used=0;

	}

	/**
	 * This is a private copy constructor for clone().
	 */
	private UserPreferenceArray(long[] itemIds, long userId, float[] values) {
		this.itemIds = itemIds;
		this.userID = userId;
		this.values = values;
	}

	public int length() {
		return itemIds.length;
	}
	
	@Override
	public Preference get(int i) {
		return new PreferenceView(i);
	}

	public long[] getIDs() {
		return itemIds;
	}

	@Override
	public void set(int i, Preference pref) {
		userID = pref.getUserID();
		itemIds[i] = pref.getItemID();
		values[i] = pref.getValue();
	}

	public long getUserID(int i) {
		return userID;
	}

	public void setUserID(int i, long userId) {
		userID = userId;
	}

	public long getItemID(int i) {
		if (i < used)
			return itemIds[i];
		else
			return -1;
	}

	public void setItemID(int i, long itemuserId) {
		itemIds[i] = itemuserId;
	}

	/**
	 * @return all item itemitemuserIds
	 */

	public long[] getitemIDs() {
		return itemIds;
	}

	public float getValue(int i) {
		return values[i];
	}

	public void setValue(int i, float value) {
		values[i] = value;
	}

	public void sortByUser() {
	}

	public void sortByItem() {
		selectionSort(ITEM);
	}

	public void sortByValue() {
		selectionSort(VALUE);
	}

	public void sortByValueReversed() {
		selectionSort(VALUE_REVERSED);
	}

	public boolean hasPrefWithUserID(long userId) {
		return userID == userId;
	}

	public boolean hasPrefWithItemID(long itemId) {
		for (int i = 0; i < used; i++) {

			if (itemId == itemIds[i]) {
				return true;
			}
		}
		return false;
	}

	private void selectionSort(int type) {
		// I think this sort will prove to be too dumb, but, it's in place and
		// OK for tiny, mostly sorted data
		int max = length();
		boolean sorted = true;
		for (int i = 1; i < max; i++) {
			if (isLess(i, i - 1, type)) {
				sorted = false;
				break;
			}
		}
		if (sorted) {
			return;
		}
		for (int i = 0; i < max; i++) {
			int min = i;
			for (int j = i + 1; j < max; j++) {
				if (isLess(j, min, type)) {
					min = j;
				}
			}
			if (i != min) {
				swap(i, min);
			}
		}
	}

	private boolean isLess(int i, int j, int type) {
		switch (type) {
		case ITEM:
			return itemIds[i] < itemIds[j];
		case VALUE:
			return values[i] < values[j];
		case VALUE_REVERSED:
			return values[i] >= values[j];
		default:
			throw new IllegalStateException();
		}
	}

	private void swap(int i, int j) {
		long temp1 = itemIds[i];
		float temp2 = values[i];
		itemIds[i] = itemIds[j];
		values[i] = values[j];
		itemIds[j] = temp1;
		values[j] = temp2;
	}

	public UserPreferenceArray clone() {
		return new UserPreferenceArray(itemIds.clone(), userID, values.clone());
	}

	public boolean equals(Object other) {
		if (!(other instanceof UserPreferenceArray)) {
			return false;
		}
		UserPreferenceArray otherArray = (UserPreferenceArray) other;
		return userID == otherArray.userID
				&& Arrays.equals(itemIds, otherArray.itemIds)
				&& Arrays.equals(values, otherArray.values);
	}

	public String toString() {
		if (itemIds == null || itemIds.length == 0) {
			return "GenericUserPreferenceArray[{}]";
		}
		StringBuilder result = new StringBuilder(20 * itemIds.length);
		result.append("GenericUserPreferenceArray[useruserId:");
		result.append(userID);
		result.append(",{");
		for (int i = 0; i < itemIds.length; i++) {
			if (i > 0) {
				result.append(',');
			}
			result.append(itemIds[i]);
			result.append('=');
			result.append(values[i]);
		}
		result.append("}]");
		return result.toString();
	}
	
	  public Iterator<Preference> iterator() {
		    return Iterators.transform(new CountingIterator(length()),
		                               new Function<Integer, Preference>() {
		                                 @Override
		                                 public Preference apply(Integer from) {
		                                   return new PreferenceView(from);
		                                 }
		                               });
		  }

	private final class PreferenceView implements Preference {

		private final int i;

		private PreferenceView(int i) {
			this.i = i;
		}

		public long getUserID() {
			return UserPreferenceArray.this.getUserID(i);
		}

		public long getItemID() {
			return UserPreferenceArray.this.getItemID(i);
		}

		public float getValue() {
			return values[i];
		}

		public void setValue(float value) {
			values[i] = value;
		}

	}

}
