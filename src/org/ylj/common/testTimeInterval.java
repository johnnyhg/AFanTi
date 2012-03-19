package org.ylj.common;

import java.util.HashMap;
import java.util.Map;

public class testTimeInterval {
	public static void main(String[] args) throws InterruptedException {
		
		Map<Long, String> map = new HashMap<Long, String>();

		for (int j = 0; j < 1000; j++) {
			UTimeInterval.startNewInterval();
			int l = 0;

			for (int i = 0; i < 100000; i++) {

				l = l + i;
				map.put((long) i, String.valueOf(i));

			}
			long timeinterval = UTimeInterval.endInterval();
			System.out.println("Put cost="+timeinterval / 100000);

			l = 0;
			UTimeInterval.startNewInterval();
			for (int i = 0; i < 100000; i++) {

				l = l + i;
				map.get(i);

			}
			timeinterval = UTimeInterval.endInterval();
			System.out.println("Get cost="+timeinterval / 100000);
			map.clear();
		}
	}

}
