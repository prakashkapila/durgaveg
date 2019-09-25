package org.durgaveg.com.net;
import org.apache.log4j.Level;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.clustering.KMeans;

import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;
import org.durgaveg.com.net.constants.DurgavegConstants;
import org.apache.spark.ml.linalg.Vector;
 
public class AlgoImpls extends SparkParent implements DurgavegConstants{
	StructType structType;
	private void runStreamVersion(StructType schema) {
		
		Dataset<Row> staticRows = 
				session.readStream()
				.schema(schema)
				.format("csv")
				.option("header","true")
				.option("inferSchema", "true")
				.load("C:\\analytics\\data\\Spark-The-Definitive-Guide-master\\data\\retail-data\\by-day\\*.csv");
		staticRows.createOrReplaceGlobalTempView("RetailData");
		staticRows.printSchema();
		runQuery(staticRows,true);
	}
	private Dataset<Row> runStaticVersion(String fileName) {
		 Dataset<Row> staticRows = 
				session
				.read()
				.format("csv")
				.option("header","true")
				.option("inferSchema", "true")
				.load(fileName);
				//.load("C:\\analytics\\data\\Spark-The-Definitive-Guide-master\\data\\retail-data\\by-day");
		staticRows.createOrReplaceGlobalTempView("RetailData");
		staticRows.printSchema();
		runQuery(staticRows,false);
		structType = staticRows.schema();
		return staticRows;
	}
	
	private void runQuery(Dataset<Row> rows,boolean stream) {
		StructType schema = rows.schema();
			rows.createOrReplaceTempView("retaildata");
		
		Dataset<Row> queryResult=
		rows
		.sparkSession().sql("select * from retaildata")
				//,functions.expr("Quantity * UnitPrice").as("UnitTotal")
				
		.groupBy(
				new Column("FirstName"),
				functions.window(new Column("OrderDate"),"1 day" ))
		.sum("Order Subtotal Amount")
		;
		if(!stream)
		queryResult.show();
		else
		{
			try {
				queryResult.writeStream()
				.format("console")
				.queryName("CustomerPurchases")
				.outputMode("complete")
				.start()
				.awaitTermination();
			} catch (StreamingQueryException e) {
				e.printStackTrace();
			}
		}
		
	}
	public void init() {
		Dataset<Row> rawRows = runStaticVersion(DECSALES);
		Dataset<Row> transformedRows =transformToVector(rawRows,"DayOfWeek"); 
		applKMeans(transformedRows);
 	}
	
	public void applKMeans(Dataset<Row> transformed)
	{
		KMeans algo = new KMeans().setSeed(1L).setK(20);
		
		Dataset<Row> set[] = transformed.randomSplit(new double[] {0.8,02});
		Dataset<Row> trainSet = set[0];
		Dataset<Row> testSet = set[1];
		
		System.out.println("Tainset sample data is");
		trainSet.show();
		System.out.println("Testset sample data is");
		testSet.show();
		
		KMeansModel algomodal = algo.fit(trainSet);
		Dataset<Row> predictions = algomodal.transform(testSet);
		Vector[] clusters =algomodal.clusterCenters();
		for(int i=0;i<clusters.length;i++)
		{
			System.out.println("Cluster is"+clusters[i]);
		}
		double cost = algomodal.computeCost(testSet);
		System.out.println("Compute Cost is "+cost);
		System.out.println("Prediction Col is "+algomodal.getPredictionCol());
		 
	}
	public Dataset<Row> transformToVector(Dataset<Row> rows,String col){
		Dataset<Row> transformed = null;
		
		rows = rows.na().fill(0) // fill all null values with 0
		// Create a new Column DayOfWeek mentioning the date
		.withColumn("DayOfWeek", functions.date_format(new Column("OrderDate"), "EEEE"));
		
		StringIndexer indexer = new StringIndexer()
				.setInputCol(col)
				.setOutputCol(col+"Indx");
		OneHotEncoder encoder = new OneHotEncoder()
				.setInputCol(col+"Indx")
				.setOutputCol(col+"IndxEnc");
		VectorAssembler vector = new VectorAssembler()
				.setInputCols(new String[] {"UnitPrice","Quantity","DayOfWeekIndxEnc"})
				.setOutputCol("features");
				;
		Pipeline pipes = new Pipeline().setStages(new PipelineStage[] {indexer,encoder,vector});	
		PipelineModel model = pipes.fit(rows);
		transformed = model.transform(rows);
		return transformed;
	}
	public static void main(String areg[])
	{
		AlgoImpls algos = new AlgoImpls();
		
		algos.init();
	}
}