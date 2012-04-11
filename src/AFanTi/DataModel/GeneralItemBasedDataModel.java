package AFanTi.DataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.ylj.common.IOoperation;
import org.ylj.common.UTimeInterval;
import org.ylj.math.MathException;
import org.ylj.math.Vector;

public class GeneralItemBasedDataModel implements ItemBasedDataModel {

	Map<Long, Vector> itemsMap = new HashMap<Long, Vector>();
	Map<Long, long[]> usersMap = new HashMap<Long, long[]>();

	Set<Long> allItemIDs_cached;
	Set<Long> allUserIDs_cached;

	Properties properties = new Properties();

	ReadWriteLock rwLock = new ReentrantReadWriteLock();
	Lock dataModelReadLock = rwLock.readLock();
	Lock dataModelWriteLock = rwLock.writeLock();

	private static Logger logger = Logger
			.getLogger(GeneralItemBasedDataModel.class.getName());

	public GeneralItemBasedDataModel() {

		// logger.addAppender(new ConsoleAppender());

	}

	public void loadFromDir(String dir_path) {

		File dir = new File(dir_path);
		int beginTime = UTimeInterval.startNewInterval();
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

		logger.info("Loading Files Complete . Get Rating " + rating_count
				+ ". cost:" + UTimeInterval.endInterval(beginTime) + "'us");
		logger.info("Get Item:" + getAllItemIDs().size() + ",Get User:"
				+ getAllUserIDs().size());

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
				// System.out.println("to set userID :" + userID + " itemID="
				// + itemID + " rating=" + rating);
				// System.out.flush();
				this.setRating(userID, itemID, rating);
				logger.debug("Find a  record  :" + aline);
				// System.out.println("Find a  record  :" + aline);
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

	@Override
	public Float getRating(long userID, long itemID) {

		Float result=null;
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			Vector itemV = itemsMap.get(itemID);

			if (itemV == null)
				result =null;
			else
				result=itemV.getValueOfDimension(userID);

		} finally {
			dataModelReadLock.unlock();
		}
		return  result;

	}

	@Override
	public void setRating(long userID, long itemID, float rating) {

		// do itemsMap
		while (dataModelWriteLock.tryLock() == false)
			;
		try {
			Vector itemV = itemsMap.get(itemID);
			if (itemV == null) {
				itemV = new Vector(itemID, 1);
				itemV.setDimensionAndValueOfIndex(0, userID, rating);

				itemsMap.put(itemID, itemV);
				allItemIDs_cached = null;

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
									expandedVector.setDimensionAndValueOfIndex(
											i, userID, rating);
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
				allUserIDs_cached = null;

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
		} finally {
			dataModelWriteLock.unlock();
		}

	}

	@Override
	public void removeRating(long userID, long itemID) {

		// do itemsMap
		while (dataModelWriteLock.tryLock() == false)
			;
		try {
			Vector itemV = itemsMap.get(itemID);

			if (itemV == null)
				return;
			if (!itemV.containDimension(userID))
				return;

			if (itemV.getLength() == 1) {
				itemsMap.remove(itemID);
				allItemIDs_cached = null;
			} else {
				// shrink
				Vector shrinkedVector = new Vector(itemID,
						itemV.getLength() - 1);

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
				if (items.length == 1) {
					usersMap.remove(userID);
					allUserIDs_cached = null;
				} else {
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

		} finally {
			dataModelWriteLock.unlock();
		}

	}

	@Override
	public Vector getItemVector(long itemID) {

		Vector result=null;
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			result= itemsMap.get(itemID);

		} finally {
			dataModelReadLock.unlock();
		}
		return result;

	}

	@Override
	public long[] getAllUsersRatedTheItem(long itemID) {

		long[] result=null;
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			
			Vector itemV = itemsMap.get(itemID);
			if (itemV == null)
				result=null;
			else
				result= itemV.getDimensionArrary();
			
		} finally {
			dataModelReadLock.unlock();
		}
		return result;
	}

	@Override
	public long[] getAllItemsRatedByUser(long userID) {
		long[] result;
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			result = usersMap.get(userID);
			
		} finally {
			dataModelReadLock.unlock();
		}
		return result;
	}

	public void print() {

	}

	@Override
	public boolean containItem(long itemID) {
		
		// TODO Auto-generated method stub
		Vector result=null;
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			result= itemsMap.get(itemID);
		} finally {
			dataModelReadLock.unlock();
		}
			if (result == null)
				return false;
			else
				return true;

		
	}

	@Override
	public boolean containUser(long userID) {
		// TODO Auto-generated method stub
		long[] result=null;
		
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			result = usersMap.get(userID);
			
		} finally {
			dataModelReadLock.unlock();
		}
			if (result == null)
				return false;
			else
				return true;
		
	}

	@Override
	public Set<Long> getAllItemIDs() {

		if (allItemIDs_cached != null) {
			logger.info("hit allItemIDs_cached!");
			return allItemIDs_cached;

		}

		// allItemIDs_cached=new TreeSet<Long>(itemsMap.keySet());
		while (dataModelReadLock.tryLock() == false)
			;
		try {
			allItemIDs_cached = new HashSet<Long>(itemsMap.keySet());

		} finally {
			dataModelReadLock.unlock();
		}

		return allItemIDs_cached;
	}

	@Override
	public Set<Long> getAllUserIDs() {

		if (allUserIDs_cached != null) {
			// logger.info("hit allUserIDs_cached!");
			return allUserIDs_cached;
		}
		while (dataModelReadLock.tryLock() == false)
			;
		try {

			allUserIDs_cached = new HashSet<Long>(usersMap.keySet());

		} finally {
			dataModelReadLock.unlock();
		}
		return new HashSet<Long>(allUserIDs_cached);

	}
}
