package AFanTi.Neighborhood;



import java.lang.reflect.Constructor;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveArrayIterator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import AFanTi.DataModel.NewGenericDataModel;

public class UserNeighborhoodServer extends UnicastRemoteObject implements
		UserNeighborhoodSevice {

	NewGenericDataModel dataModel;

	public UserNeighborhoodServer(NewGenericDataModel datamodel)
			throws RemoteException {

		dataModel = datamodel;

	}

	@Override
	public UsersWithSimilarity[] getNearestNUserNeighborhood(
			String similarityClass, int N_Neighbors,
			long userID, GenericUserPreferenceArray userPreferences) {
		
		System.out.println("calls  getNearestNUserNeighborhood("+similarityClass+","+N_Neighbors+","+userID+","+userPreferences+")");

		System.out.flush();
		
		long setTempUserID = -1;
		PreferenceArray userPreference_in_model = null;
		try {
			userPreference_in_model = dataModel.getPreferencesFromUser(userID);
			
		} catch (TasteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (userPreference_in_model == null) {
			// set temp user in model
			
			dataModel.setUserPreference(setTempUserID, userPreferences);
			setTempUserID = userID;
		}
		else
		{
			userPreferences=(GenericUserPreferenceArray) userPreference_in_model;
		}

		UserSimilarity similarity=null;
		UserNeighborhood neighborhood=null;
		try {
			//java refection
		
		
			//PearsonCorrelationSimilarity s= new PearsonCorrelationSimilarity(dataModel);
			/*
			Class c = Class.forName("org.apache.mahout.cf.taste.impl.similarity."+similarityClass);
	
			Class[] ptype = new Class[] { DataModel.class };
			Constructor ctor = c.getConstructor(ptype);
			Object[] obj = new Object[] {(DataModel)dataModel};
			
			Object object = ctor.newInstance(obj);
			
			similarity=(UserSimilarity)object;
			
			*/
			similarity=new UncenteredCosineSimilarity((DataModel)dataModel);
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			if (setTempUserID >= 0) {
				dataModel.removeUserPreference(setTempUserID);
			}
			
			e.printStackTrace();
			return  null;
		}

		try {
		
			
			neighborhood= new NearestNUserNeighborhood(
					N_Neighbors, similarity, dataModel);
			
			long[] neighborhoodIDs = neighborhood.getUserNeighborhood(userID);
			
			if (neighborhoodIDs == null || neighborhoodIDs.length == 0)
				return null;
			
			
			neighborhood= new NearestNUserNeighborhood(
					N_Neighbors, similarity, dataModel);
			
			UsersWithSimilarity[] neighborhoods = new UsersWithSimilarity[neighborhoodIDs.length];
			
			
			for (int i = 0; i < neighborhoodIDs.length; i++) {
	
				
				neighborhoods[i]=new UsersWithSimilarity();
				
				GenericUserPreferenceArray userArray=(GenericUserPreferenceArray) dataModel.getPreferencesFromUser(userID);
				GenericUserPreferenceArray auserArray=(GenericUserPreferenceArray) dataModel.getPreferencesFromUser(neighborhoodIDs[i]);
				printGenericUserPreferenceArray(userArray);
				printGenericUserPreferenceArray(auserArray);
				
				System.out.println(userID+","+neighborhoodIDs[i]);
				System.out.println("similarity="+similarity.userSimilarity(userID,
						neighborhoodIDs[i]));
				neighborhoods[i].Similarity = similarity.userSimilarity(userID,
						neighborhoodIDs[i]);
				
			
				neighborhoods[i].preferenceArray = (GenericUserPreferenceArray) dataModel
						.getPreferencesFromUser(neighborhoodIDs[i]);
			}
		
			
			
			// remove temp user
			if (setTempUserID >= 0) {
				dataModel.removeUserPreference(setTempUserID);
			}
			printNeighbors(neighborhoods);
			return neighborhoods;

		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void printGenericUserPreferenceArray(GenericUserPreferenceArray auser )
	{
		auser.getUserID(0);
		System.out.print("# userID="+auser.getUserID(0)+" ");
		
		auser.length();
		for(int i=0;i<auser.length();i++)
		{
			System.out.print("[");
			System.out.print(auser.getItemID(i));
			System.out.print(",");
			System.out.print(auser.getValue(i));
			System.out.print("]");
		}
		System.out.print("#\n");
	}
	public static void printNeighbors(UsersWithSimilarity[] neighbors )
	{
		if(neighbors==null)
			return;
		for(UsersWithSimilarity auser:neighbors)
		{
			System.out.print("userID="+auser.preferenceArray.getUserID(0));
			System.out.print("#Similarity="+auser.Similarity);
			System.out.print("#perences=");
			for(int i=0;i<auser.preferenceArray.length();i++)
			{
				System.out.print("[");
				System.out.print(auser.preferenceArray.getItemID(i));
				System.out.print(",");
				System.out.print(auser.preferenceArray.getValue(i));
				System.out.print("]");
			}
			System.out.print("#\n");
		}
	}

}
