package org.durgaveg.com.merge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable; 

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.durgaveg.com.vo.MergeVegeVO;

public class ProductMerge implements Serializable {

	private static final long serialVersionUID = -1107939350402449422L;

	public void filterLines(String file) {
		System.out.println("Reading from " + file);
		Dataset<Row> todaysGained = getSession().read().option("header", "TRUE").option("inferSchema", "true")
				.csv(file);

		filterData(todaysGained);
	}

	private void filterData(Dataset<Row> todaysGained) {
		todaysGained.printSchema();
		todaysGained.takeAsList(5).forEach(action -> System.out.println(action.mkString()));
		Dataset<MergeVegeVO> vos = todaysGained.map(new MapFunction<Row, MergeVegeVO>() {
			private static final long serialVersionUID = 5001536011643521226L;

			@Override
			public MergeVegeVO call(Row arg0) throws Exception {
				return MergeVegeVO.getInstance(arg0);
			}
		}, Encoders.bean(MergeVegeVO.class));
		
		Dataset<Row> namedGroup = vos.groupBy("name").agg(functions.sum("qty"),functions.avg("productCurrentPrice") 
				//,functions.col("qty").multiply(functions.col("productCurrentPrice"))
				)
				.withColumnRenamed("name", "groupedName")
				.withColumnRenamed("sum(qty)", "sumQty")
				.withColumnRenamed("avg(productCurrentPrice)", "UnitCost")
				;
		namedGroup = namedGroup.withColumn("TotalCost", functions.expr("sumQty * UnitCost"));
		namedGroup.show(50);

		save(namedGroup);
	}

	public void save(Dataset<Row> rows)
	{
		File output = new File("outputNew.csv");
		System.out.println("aving to file"+output.getAbsolutePath());
		try {
			CSVWriter.writer= new FileWriter(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		rows.foreach(new CSVWriter());
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

	public static void main(String arg[]) {
		String file = arg.length == 0 ? "C:/durgaveg/data/orders-2018-09-23-22-26-48.csv" : arg[0];
		Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
		ProductMerge merge = new ProductMerge();
		merge.filterLines(file);
	}
}

class CSVWriter implements ForeachFunction<Row> {
	private static final long serialVersionUID = 3006969330382771980L;
	static FileWriter writer;
 
	@Override
	public void call(Row arg0) throws Exception {
		writer.write(arg0.get(0).toString() + ',' + arg0.getDouble(1) + "\n");
		writer.flush();
	}
}