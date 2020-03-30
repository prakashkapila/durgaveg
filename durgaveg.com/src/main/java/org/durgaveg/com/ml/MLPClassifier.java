package org.durgaveg.com.ml;

import java.io.File;
import java.io.IOException;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.impl.ActivationReLU;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class MLPClassifier {

	static String filenameTrain="";
	static String filenameTest="";
	public static void main(String arg[]) throws IOException, InterruptedException
	{
		int batchSize = 50;
		int seed = 123;
		double learningRate=0.005;
		int nEpochs=30;
		int numInputs=2;
		int numOutputs=2;
		int numofHiddenNodes=20;
		File train = new File("src/main/resources/classification/saturn_data_train.csv");
		File test = new File("src/main/resources/classification/saturn_data_eval.csv");
		System.out.println( train.getAbsolutePath()+train.exists());
		
		filenameTrain = train.getAbsolutePath();// new ClassPathResource("src/main/resources/classification/saturn_data_train.csv").getFile().getPath();
		filenameTest = test.getAbsolutePath();//new ClassPathResource("src/main/resources/classification/saturn_data_eval.csv").getFile().getPath();
	
		RecordReader rr = new CSVRecordReader();
		rr.initialize(new FileSplit(train));
		
		RecordReader rrTest = new CSVRecordReader();
		rr.initialize(new FileSplit(test));
		
		RecordReaderDataSetIterator trainIter= new RecordReaderDataSetIterator(rr,batchSize,0,2);
		RecordReaderDataSetIterator testIter= new RecordReaderDataSetIterator(rr,batchSize,0,2);
		
	MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
			.weightInit(WeightInit.XAVIER)
			.activation(new ActivationReLU())
			.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
			.updater(new Nesterovs(learningRate, 0.9))
			.list()
			.layer(0,new DenseLayer.Builder()
					.nIn(numInputs)
					.nOut(numofHiddenNodes)
					.weightInit(WeightInit.XAVIER)
					.activation(Activation.RELU)
					.build())
			.layer(1, new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
					.activation(Activation.SOFTMAX)
					.nIn(numofHiddenNodes)
					.nOut(numOutputs).build()).build()
			;
	conf.setBackpropType(BackpropType.Standard);
	MultiLayerNetwork model = new MultiLayerNetwork(conf);
	model.init();
	model.setListeners(new ScoreIterationListener(10));
	for(int i=0;i< nEpochs;i++)
	{
		model.fit(trainIter);
	}
	System.out.println("Evaluate Model");
	Evaluation eval = new Evaluation(numOutputs);
	while(testIter.hasNext())
	{
		DataSet t = testIter.next();
		INDArray features = t.getFeatures();
		INDArray labels = t.getLabels();
		INDArray prediccted = model.output(features, false);
		eval.eval(labels, prediccted);
	}
	System.out.println(eval.stats());
	plot(eval,model,rr,rrTest);		
	}

	private static void plot(Evaluation eval, MultiLayerNetwork model, RecordReader rr, RecordReader rrTest) throws IOException, InterruptedException {
		double xMin=-15,xMax=15,yMin=-15,yMax=15;
		int pointsPerAxis = 100;
		double[][] evalPoints = new double[pointsPerAxis*pointsPerAxis][2];// xy in all quadrants per axis.
		int count =0;
		for(int x=0;x<pointsPerAxis;x++) {
			for(int y=0;y<pointsPerAxis;y++)
			{
				double xi =x*(xMax-xMin)/pointsPerAxis + xMin;
				double yi = y*(yMax-yMin)/pointsPerAxis + yMin;
				evalPoints[count][0]=xi;
				evalPoints[count][1]=yi;
				count++;
			}
		}
		INDArray allXYPoints= Nd4j.create(evalPoints);
		INDArray predictionsAtXYPoints = model.output(allXYPoints);
		rr.initialize(new FileSplit(new File(filenameTrain)));
		rr.reset();
		int batchSize = 500;
			int testpoints = 100;
		RecordReaderDataSetIterator trainIter= new RecordReaderDataSetIterator(rr,batchSize,0,2);
		RecordReaderDataSetIterator testIter= new RecordReaderDataSetIterator(rrTest,testpoints,0,2);
		DataSet ds = trainIter.next();
	 	PlotUtil.plotTrainingData(ds.getFeatures(),ds.getLabels(), allXYPoints, predictionsAtXYPoints, pointsPerAxis);		
		DataSet dsTest = testIter.next();
		INDArray testPredicted = model.output(dsTest.getFeatures());
		PlotUtil.plotTestData(dsTest.getFeatures(), dsTest.getLabels(), testPredicted, allXYPoints,predictionsAtXYPoints,  pointsPerAxis);
		System.out.println("Finished Example");
	}
}
