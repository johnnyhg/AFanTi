package AFanTi.Neighborhood;

import org.ylj.math.Vector;

public class Neighborhood {
	public Vector vector;
	public double similarity;
	
	public String toString()
	{
		String str="Sim="+this.similarity+" "+vector.toString()+"\n";
		return str;
	}
}
