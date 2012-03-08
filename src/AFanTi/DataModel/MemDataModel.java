package AFanTi.DataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.ylj.common.IOoperation;
import org.ylj.common.Precondition;


import com.google.common.base.Preconditions;

public class MemDataModel implements DataModel {

	
	//GenericUserPreferenceArray
	 HashMap<Long ,UserPreferenceArray> map_by_UserId=new HashMap<Long,UserPreferenceArray>();   
	 
	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public LongPrimitiveIterator getItemIDs() throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastIDSet getItemIDsFromUser(long arg0) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getMaxPreference() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMinPreference() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumItems() throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumUsers() throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumUsersWithPreferenceFor(long... arg0) throws TasteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long getPreferenceTime(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Float getPreferenceValue(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferenceArray getPreferencesForItem(long arg0)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferenceArray getPreferencesFromUser(long arg0)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LongPrimitiveIterator getUserIDs() throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPreferenceValues() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removePreference(long arg0, long arg1) throws TasteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPreference(long arg0, long arg1, float arg2)
			throws TasteException {
		// TODO Auto-generated method stub

	}

	public int loadFromDir(File dataDir,String regex) throws IOException {
		if (!dataDir.exists() || !dataDir.isDirectory()) {
			throw new FileNotFoundException("Dir:" + dataDir.toString());
		}
		
		File[] dataFiles=IOoperation.getAllSubFiles(dataDir);
		int preference_count =0;
		for(File dataFile : dataFiles)
		{
			preference_count=+loadFromFile(dataFile,regex);
		}
		
		return preference_count;
	}

	
	public int loadFromFile(File dataFile,String regex) throws IOException {
		int preference_count =0;
		Precondition.checkArgument(dataFile != null, "dataFile is null");

		if (!dataFile.exists() || dataFile.isDirectory()) {
			throw new FileNotFoundException(dataFile.toString());
		}

		Preconditions
				.checkArgument(dataFile.length() > 0L, "dataFile is empty");
		
		FileInputStream fis= new FileInputStream(dataFile);
		
		InputStreamReader isb =new InputStreamReader(fis,"gbk");
		
		BufferedReader rb= new BufferedReader(isb);
		
		String line=null;
		
		while((line=rb.readLine())!=null)
		{
			String[] tokens=line.split(regex);
			if(tokens.length<3)
				break;
			
			long usr_id=Long.parseLong(tokens[0]);
			long item_id=Long.parseLong(tokens[1]);
			float preferenceValue=Float.parseFloat(tokens[2]);
			
			UserPreferenceArray userPreferenceArray= map_by_UserId.get(usr_id);
			if(userPreferenceArray==null)
			{
				userPreferenceArray=new UserPreferenceArray(8);
				userPreferenceArray.setUserID(0, usr_id);
				
				userPreferenceArray.setItemID(0, item_id);
				userPreferenceArray.setValue(0, preferenceValue);
				userPreferenceArray.used=1;
				
				map_by_UserId.put(usr_id, userPreferenceArray);
				
			}
			else
			{
				if(userPreferenceArray.used < userPreferenceArray.length())
				{
					userPreferenceArray.setItemID(userPreferenceArray.used , item_id);
					userPreferenceArray.setValue(userPreferenceArray.used , preferenceValue);
					userPreferenceArray.used++;
					
				}
				else  //expand
				{
					UserPreferenceArray newUserPreferenceArray =new UserPreferenceArray(userPreferenceArray.length()+8);
					
					//copy
					for(int i=0;i<userPreferenceArray.length();i++)
					{
						newUserPreferenceArray.setItemID(i,userPreferenceArray.getItemID(i));
						newUserPreferenceArray.setValue(i, userPreferenceArray.getValue(i));				
					}
					newUserPreferenceArray.setUserID(0, userPreferenceArray.getUserID(0));
	
					newUserPreferenceArray.setItemID(userPreferenceArray.length(), item_id);
					newUserPreferenceArray.setValue(userPreferenceArray.length(), preferenceValue);
					
					newUserPreferenceArray.used=userPreferenceArray.length()+1;
					
					
					map_by_UserId.put(usr_id, newUserPreferenceArray);
					
					
					
				}
			}
					
			IOoperation.printStringArrary(tokens);
			
			
		}
		
		
		
				
		return preference_count;
	}
	public boolean processFile(File file)
	{
		return false;
	}
	public boolean processLine(String line)
	{
		
		return false;
	}

}
