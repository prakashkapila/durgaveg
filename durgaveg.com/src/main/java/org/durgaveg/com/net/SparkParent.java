package org.durgaveg.com.net;

import org.apache.log4j.Level;
import org.apache.spark.sql.SparkSession;

public abstract class SparkParent {
	static SparkSession session;
	static boolean DEBUG=false;
	public SparkParent() {
		if(!DEBUG)
		org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
		initSession();
	}
	public void initSession() {
		if (session == null) {
			session = SparkSession.builder().master("local").appName("VegetablePrices")
					// .config("spark.driver.memory","4G")
					// .config("spark.driver.maxResultSize", "2G")
					.config("spark.sql.shuffle.partitions","5")
					.config("spark.driver.extraClassPath", "lib/spark-nlp-assembly-1.6.2.jar")
					// .config("spark.kryoserializer.buffer.max", "500m")
					.getOrCreate();
 		}
		
	}
}
