package org.durgaveg.com.driver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.TreeMap;

public class interview {
	int duration = 0;
	int sum =0;
	String res = "";
	int indx;
	private ArrayList<Integer> IDsOfSongs(int rideDuration, ArrayList<Integer> songDurations) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		
		duration = rideDuration - 30;
		TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
		TreeMap<Integer,Integer> result = new TreeMap<Integer,Integer>();
		
		Stack<Integer> pops = new Stack<Integer>();
		 for (int i = 0; i < songDurations.size(); i++) {
			 if(songDurations.get(i) < duration)
			  	map.put(songDurations.get(i), i);
			}
		 
 		map.keySet().forEach(act->{
 			if( sum+act < duration)
 			{
 				pops.push(map.get(act));
 				sum+=act;
 			}
 			else 
 			{
 				sum -= pops.peek();
 				if( sum+ act <=duration)
 				{
 					pops.pop();
 					pops.push(map.get(act));
 				}
 			}
 	 	});
 		ret.addAll(pops);
		return ret;
	}

	public static void main(String aarg[]) {
		interview iv = new interview();
		//100,180,40,120,10
		ArrayList<Integer> ip = new ArrayList();
		ip.add(20);
		ip.add(70);
		ip.add(90);
		ip.add(30);
		ip.add(60);
		ip.add(110);
		iv.IDsOfSongs(110,ip);
	}
}
