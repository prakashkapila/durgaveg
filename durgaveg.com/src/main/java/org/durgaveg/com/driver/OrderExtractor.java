package org.durgaveg.com.driver;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.durgaveg.com.net.SparkParent;
import org.durgaveg.com.vo.RawMessage;

public class OrderExtractor extends SparkParent {

	String baseDir = "D:\\shared\\durgaveg\\sample\\";
	public void init() {
		super.initSession();
	}
	 
	public void process() {
		String paths[] = new File(baseDir).list();
		Dataset<String> allRows = getSession().emptyDataset(Encoders.STRING());
		for(int i=0;i<paths.length;i++)
		{
			paths[i]=baseDir+paths[i];
		}
		Arrays.asList(paths).forEach(p->System.out.println(p));
//		Path path = Path.of(URI.create(paths[0]));
//		List<String> lines = Files.readAllLines(path);
//	
		//OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
		System.out.println(" Total Row Count is"+allRows.count());
		Dataset<RawMessage> msgs = allRows.map(RawMessage::init, Encoders.bean(RawMessage.class));
		
		System.out.println(" Total Row Count is"+msgs.count());
		msgs.show();
 	}
	
	public static void main(String arg[])
	{
		OrderExtractor oe = new OrderExtractor();
		oe.init();
		oe.process();
		
	}
}

class Extract {
	String row = "";
	int fieldcount = 0;
	List<RawMessage> ret = new ArrayList<RawMessage>();
	private List<RawMessage> getRows(List<String> allLines)
	{
		allLines.forEach(rows->{
			ret.add(RawMessage.init(row));
		});
		return ret;
	}
	
}