package AFanTi.Test;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;

import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import AFanTi.DataModel.FileDataModel;

public class testSimilarity {
	public static void main(String[] argvs) {

		FileDataModel filemodel = null;

		try {
			filemodel = new FileDataModel(new File(
					"E:\\DataSet\\ml-100k\\u1.base"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		DataModel model = filemodel.toDataModel();

		try {
			UserSimilarity similarity = new UncenteredCosineSimilarity(model);
			double vaule = similarity.userSimilarity(3, 1);

			System.out.println(vaule);
			 vaule = similarity.userSimilarity(42, 3);

			System.out.println(vaule);
			 vaule = similarity.userSimilarity(51, 3);

			System.out.println(vaule);
			
			
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
