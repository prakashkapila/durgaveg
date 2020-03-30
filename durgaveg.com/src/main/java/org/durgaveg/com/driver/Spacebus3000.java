package org.durgaveg.com.driver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Spacebus3000 {

	List<String> address = new ArrayList<String>();
	HashMap<String,List<String>> forward = new HashMap<String,List<String>>();
	LinkedList<String> routes = new LinkedList<String>();
	final int FWD=0,REV=1;
	public void addAddress(String... arg)
	{
		Arrays.asList(arg).forEach(e->address.add(e.trim()));
	}
	private void add(String... fromto)
	{
		fromto[0] = fromto[0].trim();
		fromto[1] = fromto[1].trim();
		
		HashMap<String,List<String>> map = forward;
		if(!map.keySet().contains(fromto[0])) {
		 	ArrayList<String> vals = new ArrayList<String>();
			vals.add(fromto[1]);
			map.put(fromto[0], vals);
		}
		else {
		map.get(fromto[0]).add(fromto[1]);
		}
		
	}
	
	public void init(String... routesin) {
		for(String routesi:routesin)
		{
			String fromto[] = routesi.split(",+");
			addAddress(fromto);
			add(fromto); // forward
			add(fromto[1],fromto[0]); // reverse;
		}
	}
	
	private String find(String from, String path, String to,List<String> subtree) {
		path = path != null ? path+"->"+from:from;
		if( subtree.contains(to))
			path+="->"+to;
		else{
			for( String poss:subtree)
			{
				if( path.contains(to))
					break;
				List<String> next = new ArrayList<String>();
				next.addAll(forward.get(poss));
				next.remove(from);
				
				if( next == null || next.size()==0) {
					continue;
				}
				path = find(poss,path,to,next);
			}
		}
		return path;
	}
	
	public String containsRoute(String... route) {
		String ret = outputRoute(route);
		return ret.contains(route[1]) ? "YES": "NO";
 	}
	
	public String outputRoute(String... route) {
		String path= null;
		String from = route[0].trim();
		String to = route[1].trim();
		if( !address.contains(from) || !address.contains(to)) {
			return "No Route Possible";
		}
		if( forward.containsKey(from)) {
			//path= path != null ?path+"->"+from:from;
			List<String> subtree = forward.get(from);
			path = find(from,path,to,subtree);
			System.out.println(path);
		}
	return path;
	}
	
	public void readData(String file) throws IOException {
		file = file == null ? "spacebusroutes.txt":file;
		File f = new File(file);
		System.out.println("reading from "+f.getAbsolutePath());
		Path path = f.toPath();
	 	List<String> content = Files.readAllLines(path);
	 	init(content.toArray(new String[content.size()]));
	}
	
	 
	public static void main(String arg[])
	{
		//String args[] = inferArgs(arg);
		Spacebus3000 k3 = new Spacebus3000();
		
		try {
			k3.readData(arg[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println( arg[1]+" to "+arg[2]);
		System.out.print("\t"+k3.containsRoute(arg[1],arg[2]));
 	}
}


