package AFanTi.DataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.ylj.common.IOoperation;
import org.ylj.math.MathException;
import org.ylj.math.Vector;

public class ItemBasedDataModel {

	Map<Long, Vector> itemsMap = new HashMap<Long, Vector>();
	Map<Long, long[]> usersMap = new HashMap<Long, long[]>();

	Properties properties = new Properties();
	private static Logger logger = Logger.getLogger(ItemBasedDataModel.class
			.getName());

	public ItemBasedDataModel() {

		// logger.addAppender(new ConsoleAppender());
	}

	public void loadFromDir(String dir_path) {

		File dir = new File(dir_path);
		int rating_count = 0;
		if (!dir.exists() || !dir.isDirectory()) {
			logger.error("rating Dir:" + dir_path + " not finded.");
			return;
		}
		File[] files = IOoperation.getAllSubFiles(dir);
		for (int i = 0; i < files.length; i++) {
			File ratingFile = files[i];
			logger.info("(" + (i + 1) + "/" + files.length
					+ ")begin Loading File:" + ratingFile.getName());
			rating_count += loadFromFile(ratingFile);
		}
		logger.info("Loading Files Complete . Get Rating " + rating_count + ".");

	}

	public long loadFromFile(File ratingFile) {

		FileInputStream fis;
		BufferedReader reader = null;
		long rating_count = 0;
		try {
			fis = new FileInputStream(ratingFile);
			InputStreamReader isr;
			if (properties.getProperty("RatingFile.encoding") != null)
				isr = new InputStreamReader(fis,
						properties.getProperty("RatingFile.encoding"));
			else
				isr = new InputStreamReader(fis);
			reader = new BufferedReader(isr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

		try {
			String aline;
			int nu = 0;
			;
			while ((aline = reader.readLine()) != null) {
				++nu;
				String[] tokens = aline.split("[ \\t]");
				if (tokens.length < 3) {
					logger.warn("Bad rating record :" + aline + " File:"
							+ ratingFile.getName() + " nu:" + nu);
					continue;
				}
				long userID;
				long itemID;
				float rating;
				try {
					userID = Long.parseLong(tokens[0]);
					itemID = Long.parseLong(tokens[1]);
					rating = Float.parseFloat(tokens[2]);
				} catch (Exception e) {
					logger.warn("Bad rating record parse error:" + aline
							+ " File:" + ratingFile.getName() + " nu:" + nu);
					continue;
				}
			//	System.out.println("to set userID :" + userID + " itemID="
			//			+ itemID + " rating=" + rating);
				//System.out.flush();
				this.setRating(userID, itemID, rating);
				logger.debug("Find a  record  :" + aline);
				//System.out.println("Find a  record  :" + aline);
				rating_count++;
			}
			reader.close();
			fis.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Load File " + ratingFile.getName() + " complete. (rating="
				+ rating_count + ")");
		return rating_count;
	}

	public Float getRating(long userID, long itemID) {
		Vector itemV = itemsMap.get(itemID);

		if (itemV == null)
			return null;

		return itemV.getValueOfDimension(userID);

	}

	public void setRating(long userID, long itemID, float rating) {

		// do itemsMap
		Vector itemV = itemsMap.get(itemID);
		if (itemV == null) {
			itemV = new ItemVector(itemID, 1);
			itemV.setDimensionAndValueOfIndex(0, userID, rating);

			itemsMap.put(itemID, itemV);

		} else {
			if (itemV.containDimension(userID)) {
				// just modify the rating
				try {
					itemV.setValueOfDimension(userID, rating);
				} catch (MathException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				// expand Vector

				Vector expandedVector = new Vector(itemID,
						itemV.getLength() + 1);

				// keep order

				boolean inserted = false;
				int offset = 0;
				for (int i = 0; i < expandedVector.getLength(); i++) {

					// add at tail
					if (!inserted) {
						if (i == expandedVector.getLength() - 1) { // add at
																	// tail
							expandedVector.setDimensionAndValueOfIndex(i,
									userID, rating);
							break;
						} else {
							if (userID < itemV.getDimensionOfIndex(i)) {
								expandedVector.setDimensionAndValueOfIndex(i,
										userID, rating);
								inserted = true;
								offset = 1;
								continue;
							}
						}

					}

					expandedVector.setDimensionAndValueOfIndex(i,
							itemV.getDimensionOfIndex(i - offset),
							itemV.getValueOfIndex(i - offset));

				}

				itemsMap.put(itemID, expandedVector);

			}
		}

		// do usersMap
		long[] items = usersMap.get(userID);
		if (items == null) {
			items = new long[1];
			items[0] = itemID;
			usersMap.put(userID, items);
		} else {
			boolean needExpend = true;
			int insertIndex = 0;

			for (int i = 0; i < items.length; i++) {
				if (itemID > items[i])
					insertIndex = i + 1;
				if (items[i] == itemID) {
					needExpend = false;
					break;
				}

			}

			if (needExpend) {
				long[] expandedItems = new long[items.length + 1];
				// keep order

				int offset = 0;
				for (int i = 0; i < expandedItems.length; i++) {

					if (i == insertIndex) {
						expandedItems[i] = itemID;
						offset = 1;
					} else
						expandedItems[i] = items[i - offset];

				}
				usersMap.put(userID, expandedItems);
			}
		}

	}

	public void removeRating(long userID, long itemID) {

		// do itemsMap
		Vector itemV = itemsMap.get(itemID);
		if (itemV == null)
			return;
		if (!itemV.containDimension(userID))
			return;

		if (itemV.getLength() == 1)
			itemsMap.remove(itemID);
		else {
			// shrink
			Vector shrinkedVector = new Vector(itemID, itemV.getLength() - 1);

			// keep order

			int offset = 0;
			for (int i = 0; i < shrinkedVector.getLength(); i++) {

				if (userID == itemV.getDimensionOfIndex(i))
					offset = 1;

				shrinkedVector.setDimensionAndValueOfIndex(i,
						itemV.getDimensionOfIndex(i + offset),
						itemV.getValueOfIndex(i + offset));

			}
			itemsMap.put(itemID, shrinkedVector);

		}

		// do usersMap
		long[] items = usersMap.get(userID);
		if (items == null)
			return;

		boolean needShrink = false;
		int removeIndex = -1;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				needShrink = true;
				removeIndex = i;
				break;
			}
		}

		if (needShrink) {
			if (items.length == 1)
				itemsMap.remove(userID);
			else {
				long[] shrinkedItems = new long[items.length - 1];
				int offset = 0;
				for (int i = 0; i < shrinkedItems.length; i++) {
					if (i == removeIndex)
						offset = 1;
					shrinkedItems[i] = items[i + offset];
				}
				usersMap.put(userID, shrinkedItems);
			}
		}

	}

	public Vector getItemVector(long itemID) {
		return itemsMap.get(itemID);
	}

	public long[] getAllUsersRatedTheItem(long itemID) {
		Vector itemV = itemsMap.get(itemID);
		if (itemV == null)
			return null;
		return itemV.getDimensionArrary();
	}

	public long[] getAllItemsRatedByUser(long userID) {
		return usersMap.get(userID);
	}

	public void print() {

	}
}
