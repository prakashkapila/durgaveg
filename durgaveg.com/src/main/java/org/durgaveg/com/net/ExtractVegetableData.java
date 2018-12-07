package org.durgaveg.com.net;

import java.io.*;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Serializable;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.util.LongAccumulator;
import org.durgaveg.com.vo.VegetablePriceVO;

import com.mongodb.spark.MongoSpark;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import scala.Tuple2;

public class ExtractVegetableData implements Serializable {
	private static final long serialVersionUID = 8195673612954119886L;
	static final boolean DEBUG = true;
	String vegPriceFile = "C:\\docs\\quotes\\29-11.txt";
	static LongAccumulator count = null;
	static SparkSession session;

	final String FILTER_STRING;// = "<tr>"; //"/public/quotes/main.html?symbol";

	public ExtractVegetableData(String filter) {
		FILTER_STRING = filter;
	}

	public ExtractVegetableData() {
		FILTER_STRING = "<tr>";
	}

	public static void setSparkSession(SparkSession sess) {
		session = sess;
		count = session.sparkContext().longAccumulator();
	}

	public SparkSession getSession() {
		if (session == null) {
			session = SparkSession.builder().master("local").appName("VegetablePrices")
					// .config("spark.driver.memory","4G")
					// .config("spark.driver.maxResultSize", "2G")
					.config("spark.driver.extraClassPath", "lib/spark-nlp-assembly-1.6.2.jar")
					// .config("spark.kryoserializer.buffer.max", "500m")
					.getOrCreate();

			if (count == null) {
				count = session.sparkContext().longAccumulator();
			}
		}
		return session;
	}

	public void filterLines(String file) {
		System.out.println("Reading from " + file);
		Dataset<String> todaysGained = getSession().read().textFile(file);
		filterMostGained(todaysGained);
	}

	public void filterMostGained(Dataset<String> todaysGained) {
		todaysGained.takeAsList(10).forEach(x -> System.out.println(x));
		Dataset<String> filtered = todaysGained.filter(new FilterFunction<String>() {
			private static final long serialVersionUID = 1L;
			boolean filteredVal = Boolean.FALSE;
 		public boolean call(String arg0) throws Exception {
				if (arg0 == null)
					return false;
 			if (arg0.toUpperCase().contains("<TR>"))
					filteredVal = Boolean.TRUE;
				if (filteredVal) {
					if (arg0.toUpperCase().contains("</TR>")) {
						filteredVal = Boolean.FALSE;
					}
				}
				return filteredVal;
			}
		});

		System.out.println("Total records are " + filtered.count());// print sample

		Dataset<Tuple2<Long, String>> datasetvals = filtered.map(new MapFunction<String, Tuple2<Long, String>>() {
			private static final long serialVersionUID = -222593271475357349L;

			public Tuple2<Long, String> call(String arg0) throws Exception {
				if (arg0.contains(FILTER_STRING)) {
					count.add(1);
				}
				Tuple2<Long, String> keyVal = new Tuple2<Long, String>(count.value(), arg0);
				return keyVal;
			}
		}, Encoders.tuple(Encoders.LONG(), Encoders.STRING()));
		datasetvals.show();
		JavaPairRDD<Long, String> values = filtered.toJavaRDD().mapToPair(new PairFunction<String, Long, String>() {

			private static final long serialVersionUID = -2225932714753573492L;

			public Tuple2<Long, String> call(String arg0) throws Exception {
				if (arg0.contains(FILTER_STRING)) {
					count.add(1);
				}
				Tuple2<Long, String> keyVal = new Tuple2<Long, String>(count.value(), arg0);
				return keyVal;
			}
		});

		if (DEBUG) {
			values.take(20).forEach(action -> {
				System.out.print("Key is " + action._1);
				System.out.print("\tValue are" + action._2);
			});
		}

		JavaPairRDD<Long, Iterable<String>> valuesByKey = values.groupByKey();

		JavaRDD<VegetablePriceVO> symbolsStore = valuesByKey
				.map(new Function<Tuple2<Long, Iterable<String>>, VegetablePriceVO>() {
					private static final long serialVersionUID = -5114189666849290122L;

					public VegetablePriceVO call(Tuple2<Long, Iterable<String>> arg0) throws Exception {
						VegetablePriceVO vo = new VegetablePriceVO();
						if(arg0 != null && arg0._2() != null) {
						vo.apply(arg0._2());
						}
						return vo;
 					}
				});

		String metaData = VegetablePriceVO.retrieveMetaData();
		System.out.println(metaData);

		Dataset<VegetablePriceVO> voset = new SQLContext(session).createDataset(symbolsStore.rdd(),
				Encoders.bean(VegetablePriceVO.class));
 		voset.show();
		insertRecords(voset, session);
	}

	public void insertRecords(Dataset<VegetablePriceVO> elements, SparkSession thisSession) {
		System.out.println("Total records are" + elements.count());
		MongoSpark.write(elements).option("collection", "VegPrices").mode(SaveMode.Append).save();
	}

	public static void main(String arg[]) { 
		Logger.getLogger("org.").setLevel(Level.ERROR);
		ExtractVegetableData data = new ExtractVegetableData();
		data.filterLines("c:/durgaveg/data/input/OnionPrices.html");
	}
}
