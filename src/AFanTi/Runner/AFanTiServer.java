package AFanTi.Runner;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Map;

import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.ylj.common.ReadConfigFileErrorException;
import org.ylj.common.ReadXMLConfig;

import AFanTi.DataModel.DataModelServer;
import AFanTi.DataModel.FileDataModel;
import AFanTi.DataModel.NewGenericDataModel;
import AFanTi.Neighborhood.UserNeighborhoodServer;
import AFanTi.Neighborhood.UserNeighborhoodSevice;
import AFanTi.Recommend.UserBasedRecommendServer;

public class AFanTiServer {

	public static String[] AllMemberRMI_Adr;
	public static int[] AllMemberRMI_Port;

	public static int IamNth;

	public static UserNeighborhoodSevice[] remoteUserNeighborhoodServers;

	public static boolean ProcessConfigFile(String configFile) {

		Map<String, String> configMap = null;
		try {
			configMap = ReadXMLConfig.readConfig(configFile);

		} catch (ReadConfigFileErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		String totalMembers_str = configMap.get("cluster.member.total");

		int totalMembers = 0;
		if (totalMembers_str == null)
			return false;
		else {
			totalMembers = Integer.parseInt(totalMembers_str);
			System.out.println("totalMembers_str=" + totalMembers);

		}

		String iAmIsNth_str = configMap.get("cluster.member.me");

		if (iAmIsNth_str == null)
			return false;
		else {
			IamNth = Integer.parseInt(iAmIsNth_str);
			System.out.println("iAmIsNth_str=" + IamNth);

		}

		AFanTiServer.remoteUserNeighborhoodServers = new UserNeighborhoodSevice[totalMembers];
		AFanTiServer.AllMemberRMI_Adr = new String[totalMembers];
		AFanTiServer.AllMemberRMI_Port = new int[totalMembers];

		for (int i = 0; i < totalMembers; i++) {
			String iThMemberAdr_str = configMap.get("cluster.member." + i
					+ ".address");
			String iThMemberPort_str = configMap.get("cluster.member." + i
					+ ".RMI_port");

			if (iThMemberAdr_str == null || iThMemberPort_str == null)
				return false;
			// "//192.168.1.105:1099/Hello"

			AllMemberRMI_Adr[i] = iThMemberAdr_str;
			AllMemberRMI_Port[i] = Integer.parseInt(iThMemberPort_str);
		}

		return true;
	}

	public static void main(String[] argvs) {
		/*
		 * read config file
		 */
		String configFile = "config-v1.0-0.xml";
		ProcessConfigFile(configFile);
		// String perfenceDataDir=argvs[1];
		try {
			
			
			LocateRegistry.createRegistry(AllMemberRMI_Port[IamNth]);
			System.out.println("RMI server started.. RMI_PORT="+AllMemberRMI_Port[IamNth]);
			
			

			FileDataModel filemodel = new FileDataModel(new File(
					"E:\\DataSet\\ml-100k\\u1.base"));

			DataModel model = filemodel.toDataModel();
			
			
			
			/*
			 * RecommendServer
			 */
			UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(10,
					similarity, model);

		
			UserBasedRecommendServer userBasedRecommendServer = new UserBasedRecommendServer(
					model, similarity, neighborhood);

			Naming.rebind("RecommendServer", userBasedRecommendServer);
			System.out.println("bind RecommendServer at RMI locale:"+AllMemberRMI_Port[IamNth]);
			
			/*
			 * UserNeighborhoodServer 
			 */
			
			UserNeighborhoodServer userNeighborhoodServer = new UserNeighborhoodServer((NewGenericDataModel) model);
			Naming.rebind("UserNeighborhoodServer", userNeighborhoodServer);
			System.out.println("bind UserNeighborhoodServer at RMI locale:"+AllMemberRMI_Port[IamNth]);
			
			/*
			 * DataModelServer
			 * 
			 */
			
			DataModelServer dataModelServer= new DataModelServer((NewGenericDataModel) model);
			Naming.rebind("DataModelServer", dataModelServer);
			System.out.println("bind DataModelServer at RMI locale:"+AllMemberRMI_Port[IamNth]);
			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
