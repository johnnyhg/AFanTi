package AFanTi.Neighborhood;

import java.io.Serializable;

import org.ylj.math.Vector;

public class Neighborhood implements Comparable,Serializable{
	public Vector vector;
	public double similarity;
	
	public Neighborhood(Vector v,double s)
	{
		vector=v;
		similarity=s;
	}
	public double getSimilarity()
	{
		return similarity;
	}
	public String toString()
	{
		String str="Sim="+this.similarity+" "+vector.toString()+"\n";
		return str;
	}
	@Override
	public int compareTo(Object other) {
		// TODO Auto-generated method stub
		
		
		Neighborhood otherN=(Neighborhood)other;
		int result=similarity>otherN.similarity?1:similarity<otherN.similarity?-1:0;
		return result;
	}
}
