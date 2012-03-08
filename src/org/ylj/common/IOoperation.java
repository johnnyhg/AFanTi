package org.ylj.common;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class IOoperation {
	
	public static File[] getAllSubFiles(File dir) {
		if (dir == null || !dir.isDirectory())
			return null;

		List<File> allSubFiles = new LinkedList<File>();

		Queue<File> unProcessFiles = new LinkedList<File>();

		/*
		 * BFS
		 */
		// initial
		unProcessFiles.add(dir);

		while (true) {
			File curentFile = unProcessFiles.poll();
			if (curentFile == null)
				break;
			if (curentFile.isDirectory()) {
				for (File subFile : curentFile.listFiles()) {
					unProcessFiles.add(subFile);
				}
			} else {
				allSubFiles.add(curentFile);
			}
		}

		return (File[]) allSubFiles.toArray(new File[allSubFiles.size()]);

	}
	public static void printStringArrary(String[] strArrary) {
		
		System.out.print("@"+strArrary.length);
		System.out.print("#");
		for(String token:strArrary)
		{
			System.out.print(token+"#");
			
			
			
		}
		System.out.println();
	}
	
}
