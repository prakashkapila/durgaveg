package org.durgaveg.com.driver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Stack;
import java.util.TreeMap;
//30-0576505 ein
public class interview {
	int duration = 0;
	int sum =0;
	String res = "";
	int indx;

	private ArrayList<Integer> IDsOfSongs(int rideDuration, ArrayList<Integer> songDurations, int noOfSongs) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		duration = rideDuration - 30;
		TreeMap<Integer,Integer> map = new TreeMap<Integer,Integer>();
		TreeMap<Integer,Integer> result = new TreeMap<Integer,Integer>();
		songDurations.forEach(dur-> map.put(dur, indx++)); // order the songs by their duration least to max.
		while(noOfSongs-- > 0)
		{
			Map.Entry<Integer,Integer> key = map.floorEntry(duration);
			if(key != null) {
				duration -= key.getKey();
				result.put(noOfSongs+1, key.getValue());
			}
			else
			{
				Integer highkey = result.lastKey();
				highkey = result.remove(highkey);
				Object x = map.remove(songDurations.get(highkey));
				noOfSongs++;
			}
		}
		ret.addAll(result.values());
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
		ip.add(120);
		iv.IDsOfSongs(110,ip,2);
	}
}
