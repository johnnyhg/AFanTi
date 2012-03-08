package org.ylj.common;

public class Precondition {

	  public static void checkArgument(boolean expression) {
		    if (!expression) {
		      throw new IllegalArgumentException();
		    }
		  }
	  
	  
	public static void checkArgument(boolean expression, String errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
	

}
