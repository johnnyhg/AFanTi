package AFanTi.Neighborhood;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;


	public interface UserNeighborhoodSevice extends  Remote{
		  
		 /**
		  * for user-base recommender
		  * @param user
		  * @param threshold
		  * @param N_user
		  * @return
		  */
	
		//UsersWithSimilarity[]  getNearestNUserNeighborhood(String similarityClass,double minSimilarity,int N_Neighbors ,long userID,GenericUserPreferenceArray userPreferences);
		UsersWithSimilarity[]  getNearestNUserNeighborhood(String similarityClass,int N_Neighbors ,long userID,GenericUserPreferenceArray userPreferences)throws RemoteException;
		/*
		UsersWithSimilarity[] getNearestNUserNeighborhood(
				String similarityClass, double minSimilarity, int N_Neighbors,
				long userID, GenericUserPreferenceArray userPreferences);
		*/
		//UsersWithSimilarity[]  getThresholdUserNeighborhood(String similarityClass,double threshold ,double samplingRate,Long userID,GenericUserPreferenceArray userPreferences);
		//UsersWithSimilarity[]  getThresholdUserNeighborhood(String similarityClass,double threshold ,long userID,GenericUserPreferenceArray userPreferences);
		/*
		 * for item-base recommender
		 */
		//ItemsWithSimilarity[] getItemNeighborhood(GenericItemPreferenceArray user,double threshold ,int N_user)throws RemoteException;
		  
		//ItemsWithSimilarity[] getItemNeighborhood(GenericItemPreferenceArray user,int N_Neighbors)throws RemoteException;
		  
		//ItemsWithSimilarity[] getItemNeighborhood(GenericItemPreferenceArray user,double threshold)throws RemoteException;
		
		  
		}


