package org.ylj.math;

public class testVextor {
	
	public static void main(String[] args)
	{
		Vector myVector=new Vector(10);
		myVector.setVectorID(12);
		
		myVector.setDimensionOfIndex(0, 1);
		myVector.setDimensionOfIndex(1, 2);
		myVector.setDimensionOfIndex(2, 3);	
		myVector.setDimensionOfIndex(3, 4);
		myVector.setDimensionOfIndex(4, 5);
		myVector.setDimensionOfIndex(5, 6);
		myVector.setDimensionOfIndex(6, 7);
		myVector.setDimensionOfIndex(7, 8);
		myVector.setDimensionOfIndex(8, 9);
		myVector.setDimensionOfIndex(9,10);
		
	
		myVector.setValueOfIndex(0, 10);
		myVector.setValueOfIndex(1, 9);
		myVector.setValueOfIndex(2, 8);	
		myVector.setValueOfIndex(3, 7);
		myVector.setValueOfIndex(4, 6);
		myVector.setValueOfIndex(5, 5);
		myVector.setValueOfIndex(6, 4);
		myVector.setValueOfIndex(7, 3);
		myVector.setValueOfIndex(8, 2);
		myVector.setValueOfIndex(9,1);
		
		myVector.print();
		//System.out.println(	myVector.getLength());
		System.out.println(	"sortByValue");
		myVector.sortByValue();
		myVector.print();
		System.out.println(	"sortByValue");
		myVector.sortByDimension();
		myVector.print();
		
	}
}
