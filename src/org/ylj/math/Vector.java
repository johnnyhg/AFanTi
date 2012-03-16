package org.ylj.math;

public class Vector {
	private static final int DIMENSION = 1;
	private static final int VALUE = 2;

	private  long vectorID;
	private  final long[] dimensions;
	private  final float[] values;

	private static int compareBy = DIMENSION;

	public Vector(int d_length) {
		this(0, d_length);
	}

	public Vector(long vID, int d_length) {
		
		vectorID = vID;

		dimensions = new long[d_length];
		values = new float[d_length];

	}

	public int getLength() {
		return dimensions.length;
	}

	public long getVectorID() {
		return vectorID;
	}

	public void setVectorID(long id) {
		vectorID = id;
	}
	public long[] getDimensionArrary()
	{
		return dimensions;
	}
	public long getDimensionOfIndex(int index) {
		return dimensions[index];
	}

	public void setDimensionOfIndex(int index, long value) {
		dimensions[index] = value;
	}
	public void setDimensionAndValueOfIndex(int index,long dimension , float value)
	{
		dimensions[index] = dimension;
		values[index]=value;
	}

	public float getValueOfIndex(int index) {
		return values[index];
	}

	public void setValueOfIndex(int index, float value) {
		values[index] = value;
	}

	public boolean containDimension(long dimension) {
		for (int i = 0; i < dimensions.length; i++) {
			if (dimension == dimensions[i])
				return true;
		}
		return false;
	}

	public Float getValueOfDimension(long dimension)  {
		for (int i = 0; i < dimensions.length; i++) {
			if (dimension == dimensions[i])
				return values[i];
		}
		return null;
	
	}
	public void setValueOfDimension(long dimension,float value) throws MathException {
		for (int i = 0; i < dimensions.length; i++) {
			if (dimension == dimensions[i])
			{
				values[i]=value;
				return;
			}
		}
		throw new MathException("can't find dimension  value of  dimension:"
				+ dimension);
	}
	public void sortByValue() {
		setCompareBy(VALUE);
		qick_sort(0, dimensions.length);
	}

	public void sortByDimension() {
		setCompareBy(DIMENSION);
		qick_sort(0, dimensions.length);
	}

	private void setCompareBy(int byWhat) {

		Vector.compareBy = byWhat;

	}

	private void swap(int index_1, int index_2) {
		if (index_1 < 0 || index_2 < 0 || index_1 >= dimensions.length
				|| index_2 >= dimensions.length)
			return;
		if (index_1 == index_2)
			return;

		dimensions[index_1] = dimensions[index_1] ^ dimensions[index_2];
		dimensions[index_2] = dimensions[index_1] ^ dimensions[index_2];
		dimensions[index_1] = dimensions[index_1] ^ dimensions[index_2];

		float temp = values[index_1];
		values[index_1] = values[index_2];
		values[index_2] = temp;

	}

	/**
	 * qick_sor(0,arrary.length)
	 * 
	 * @param index_begin
	 * @param index_end
	 */

	private boolean biggerThan(int index_0, int index_1) {
		
		if (compareBy == DIMENSION) {
			if (dimensions[index_0] > dimensions[index_1])
				return true;
			else
				return false;
		} else {
			if (values[index_0] > values[index_1])
				return true;
			else
				return false;
		}

	}

	private void qick_sort(int index_begin, int index_end) {

		if (index_begin == index_end)
			return;

		int current_index = index_begin;
		
		int bigger_index = index_end - 1;
		
		//System.out.println("qick_sort( "+index_begin+" ,"+index_end+")");
		
	
		while (true) {
			
		//	System.out.println("current_index="+current_index+" bigger_index="+bigger_index);
			/*
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			if (current_index == bigger_index) {

				qick_sort(index_begin, current_index);
				qick_sort(current_index + 1, index_end);
				return;
			}

			if (biggerThan(current_index, current_index + 1)) {

				swap(current_index, current_index + 1);
				current_index++;
				
			} else {

				swap(bigger_index, current_index + 1);
				bigger_index--;
			}
		}
	}

	public String toString()
	{
		String vect_str=new String("#vectorID=" + vectorID + "");
		for (int i = 0; i < dimensions.length; i++) {
			vect_str=vect_str+" [" + dimensions[i] + "," + values[i] + "]";
		}
		vect_str=vect_str+"#";
		return vect_str;
	}
	public void print() {
		/*
		System.out.print("#vectorID=" + vectorID + "");
		for (int i = 0; i < dimensions.length; i++) {
			System.out.print(" [" + dimensions[i] + "," + values[i] + "]");
		}
		*/
		System.out.println(toString());
		
	}
}
