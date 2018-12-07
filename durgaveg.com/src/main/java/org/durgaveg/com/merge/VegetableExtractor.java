package org.durgaveg.com.merge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class VegetableExtractor {
	public void filterLines(String file) {
		
		System.out.println("Reading from " + file);
		HashMap<BASKET, Set<String>> filterData = getHashMap();
		Dataset<Row> todaysGained = getSession().read()
				.option("header", "TRUE")
				.option("inferSchema", "true")
				.csv(file);
		todaysGained = todaysGained.filter(new Column("Description").contains("RAW"));
		todaysGained.takeAsList(5).forEach(x->System.out.println(x));
		Dataset<Row> diabetecData = todaysGained.filter(new FilterFunctionDiabetes(filterData.get(BASKET.DIABE)));
		System.out.println("total diabetes count"+diabetecData.count());
		Dataset<Row> cholestorolData = todaysGained.filter(new FilterFunctionDiabetes(filterData.get(BASKET.CHOLES)));
		System.out.println("total diabetes count"+cholestorolData.count());
		Dataset<Row> nutritionData = todaysGained.filter(new FilterFunctionDiabetes(filterData.get(BASKET.NUT)));
		System.out.println("total diabetes count"+nutritionData.count());
		
		saveData(diabetecData,BASKET.DIABE);
		saveData(cholestorolData,BASKET.CHOLES);
		saveData(nutritionData,BASKET.NUT);
		
	}
	 
	private void saveData(Dataset<Row> diabetecData,BASKET basket) {
		File file = new File("C:/durgaveg/data/output/"+basket.name()+".csv");
		String[] fields = diabetecData.schema().fieldNames();
		FileWriter writer= null;
	 	try {
			file.createNewFile();
			System.out.println("Saving to file "+file.getAbsolutePath());
			writer = new FileWriter(file); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	 	switch(basket) {
	 	case DIABE:{
	 		DiabSaver.writer = writer;
	 		DiabSaver.fieldNames = fields;
	 		diabetecData.foreach(new DiabSaver());
	 		break;
	 	}
	 	case NUT:{
	 		NutSaver.writer = writer;
	 		diabetecData.foreach(new NutSaver());
	 	}
	 	case CHOLES:{
	 		CholesSaver.writer = writer;
	 		diabetecData.foreach(new CholesSaver());
	 	}
	 	}
		
	}

	private SparkSession getSession() {
		SparkSession session = SparkSession.builder().master("local").appName("VegetablePrices")
				// .config("spark.driver.memory","4G")
				// .config("spark.driver.maxResultSize", "2G")
				.config("spark.driver.extraClassPath", "lib/spark-nlp-assembly-1.6.2.jar")
				// .config("spark.kryoserializer.buffer.max", "500m")
				.getOrCreate();
		return session;
	}
enum BASKET {NUT,CHOLES,DIABE};
	public HashMap<BASKET,Set<String>> getHashMap(){
		HashMap<BASKET,Set<String>> vitMap = new HashMap<BASKET,Set<String>>();
		Set<String> set = new HashSet<String>();
		
		String ret = "";
		 	ret = "Carrots,Asperaugus,Lentils,"
			 +"Brocolli,Green Beans,Chickpeas,artichoke,Lettuce,almonds,"
			+"Brussels,Sprouts,Egg Plants,Brinjals,pumpkin,"	
 			+"split peas,Peppers,Amaranth,Avacados,	Celery,	Quinoa,"	
			+"Spinach,Mustard Greens";
			ret +="Berries,Citrus,Apricots,Apples";
			ret = ret.toUpperCase();
			set.clear(); 
			set.addAll(Arrays.asList(ret.split(",")));
 			vitMap.put(BASKET.DIABE, set.stream().collect(Collectors.toSet()));
			
		//b3 vitamin	
		ret = "peas, squash, carrots, corn, cabbage, Brussels sprouts ,sweet potatoes,"+
				"apples, pears, citrus fruits, berries, apricots, figs and prunes,";	
		ret +="avocados, asparagus, peas, potatoes, mushrooms, corn, artichokes ,lima beans";
		// niacin
		ret +=  "oranges ,grapefruit, guava, kiwi, blackberries, red peppers,"
				+ " kale, Brussels sprouts, broccoli,mangoes, passionfruit, pineapple, "
				+ "strawberries, amaranth leaves, bok choy, Swiss chard ,butternut squash";
		// Vitamin E
		ret +="spinach,beet root, chard, squash, parsnip, potatoes, spirulina,blackberries,"
				+ " blueberries, boysenberries, cranberries, guava, kiwi, mango, "
				+ "nectarines, papaya peaches";
		ret = ret.toUpperCase();
		set.clear();
		List<String> list = Arrays.asList(ret.split("\\,"));
		set.addAll(list);
		vitMap.put(BASKET.CHOLES,set.stream().collect(Collectors.toSet()));
 
		set = vitMap.get(BASKET.CHOLES);
		set.addAll(vitMap.get(BASKET.DIABE));
		vitMap.put(BASKET.NUT, set.stream().collect(Collectors.toSet()));
		return vitMap;
	}
	public static void main(String arg[]) {
		Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
		String file ="C:\\Users\\kapila\\Downloads\\USDA.csv";// arg.length == 0 ? "C:/durgaveg/data/orders-2018-09-23-22-26-48.csv" : arg[0];
		VegetableExtractor merge = new VegetableExtractor();
		merge.filterLines(file);
	}
}

class DiabSaver extends Saver{
	private static final long serialVersionUID = -9217472645549344318L;
	public DiabSaver() {
		init(fieldNames);
	}
}

class NutSaver extends Saver{
	private static final long serialVersionUID = -9217472645549344317L;
	public NutSaver() {
		init(fieldNames);
	} 
}
class CholesSaver extends Saver{
	private static final long serialVersionUID = -9217472645549344316L;
	public CholesSaver() {
		init(fieldNames);
	}
}

class Saver implements ForeachFunction<Row>{
	static FileWriter writer;
	public static String[] fieldNames;
	
	protected void init(String[] fields)
	{
		final StringBuilder ret =new StringBuilder();;
		Arrays.asList(fields).forEach(x->ret.append(String.valueOf(x)).append(","));
		ret.append("\n");
		try {
			writer.write(ret.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}
 	@Override
	public void call(Row arg0) throws Exception {
 		String str = "";
 		for(int i=0;i<arg0.size();i++)
 		{
 			 str += String.valueOf(arg0.get(i)).replace(",", " ").concat(",");
 		}
	 writer.write(str);	
	 writer.write("\n");
	 writer.flush();
	}
	
}
class FilterFunctionDiabetes implements FilterFunction<Row>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3310007233688314009L;
	final Set<String> filterValues = new HashSet<String>();
	public FilterFunctionDiabetes(Set<String> filterValues)
	{ 
		filterValues.forEach(x->this.filterValues.add(x.toUpperCase()));
	 }
	@Override
	public boolean call(Row arg0) throws Exception {
		String col = arg0.getString(1);
		String cols[] = col.split(",");
		
		for(int i=0;i<cols.length;i++)
		{
		if(filterValues.contains(cols[i].toUpperCase())|| cols[i].contains("CARRO"))
			return true;
		}
	 return false;
	}
}