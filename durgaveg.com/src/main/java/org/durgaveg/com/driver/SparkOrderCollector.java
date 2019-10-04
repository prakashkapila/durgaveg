package org.durgaveg.com.driver;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.durgaveg.com.mail.ImapGmailConnector;
import org.durgaveg.com.net.SparkParent;
import org.durgaveg.com.vo.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkOrderCollector extends SparkParent {
	Logger logger = LoggerFactory.getLogger(SparkOrderCollector.class);

	public void init() {
		super.initSession();

	}

	public void processBatch() {
		Dataset<RawMessage> finalResult = getSession().emptyDataset(Encoders.bean(RawMessage.class));
		ImapGmailConnector conn = new ImapGmailConnector();
		int count = conn.getMessageCount();
		int indx = 1000;
	 	try {
		conn.login();
		while(count>0) {
			logger.info("Processing last "+indx +" rows");
			Dataset<RawMessage> batch = processBatch(count-indx,count,conn);
			if (batch == null || batch.count() < 1)
				break;
			finalResult = finalResult.union(batch);
			logger.info("Total rows in the data set   "  +finalResult.count());
			count -=indx;
		}
		}catch(Exception esp) {esp.printStackTrace();}
	 	finalResult.write().csv("D:/shared/sample.csv");
	}
	
 	public Dataset<RawMessage> processBatch(int start,int end, ImapGmailConnector conn) throws Exception{
		CopyOnWriteArrayList<Message> cow = new CopyOnWriteArrayList<Message>(conn.getMessages(start, end));
		logger.info(" Total messages we got "+ cow.size());
		ExecutorService serviceExecutor = Executors.newWorkStealingPool(25);
		ExecutorCompletionService<RawMessage> service = new ExecutorCompletionService<>(serviceExecutor);
		int ctr = cow.size();
		int indx = 200;
		Dataset<RawMessage> resultList = getSession().emptyDataset(Encoders.bean(RawMessage.class));
		List<Callable<RawMessage>> handlers = new ArrayList<Callable<RawMessage>>();
		List<RawMessage> result = new CopyOnWriteArrayList<RawMessage>();
		int prev =0;
		while (ctr>0) {
	 		logger.info("processing next to "+indx+" total elements "+ctr);
			List<Message> sublist =cow.subList(ctr-indx, ctr-1); 
			sublist.forEach(action -> handlers.add(new MessageHandler(action)));
			handlers.forEach(service::submit);
			handlers.forEach(action -> {
				try {
					RawMessage obj = service.take().get();
					if (obj != null) {
						result.add(obj);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
			prev = result.size()-prev;
			if (result.size() == 0) {
				logger.info("No elements present in first " + ctr);
				break;
			}
			DateFormat dtformat =DateFormat.getDateInstance(); 
			String startDate = dtformat.format(cow.get(ctr-indx).getSentDate());
			String endDate = dtformat.format(cow.get(ctr-1).getSentDate());
			logger.info("Total Orders from "+startDate+" to end Date "+endDate+ " are "+prev );
//			cow.removeAll(sublist);
			ctr -=indx;
			if(result.size() > 500) {
				resultList = resultList.union(getSession().createDataset(result, Encoders.bean(RawMessage.class)));
				logger.info("total elements in dataset"+resultList.count());
				result.clear();
				prev=0;
			}
		}
		resultList = resultList.union(getSession().createDataset(result, Encoders.bean(RawMessage.class)));
		return resultList;
	}
	public void process() throws Exception {
		ImapGmailConnector conn = new ImapGmailConnector();
		conn.login();
		CopyOnWriteArrayList<Message> cow = new CopyOnWriteArrayList<Message>(conn.getMessages(10000,conn.getMessageCount()));
		logger.info(" Total messages we got "+ cow.size());
		ExecutorService serviceExecutor = Executors.newWorkStealingPool(25);
		ExecutorCompletionService<RawMessage> service = new ExecutorCompletionService<>(serviceExecutor);
		int ctr = cow.size();
		int indx = 200;
		Dataset<RawMessage> resultList = getSession().emptyDataset(Encoders.bean(RawMessage.class));
		List<Callable<RawMessage>> handlers = new ArrayList<Callable<RawMessage>>();
		List<RawMessage> result = new CopyOnWriteArrayList<RawMessage>();
		int prev =0;
		while (ctr>0) {
			//indx = ctr+indx > cow.size()?cow.size()-1:indx;
			
			logger.info("processing next to "+indx+" total elements "+ctr);
			List<Message> sublist =cow.subList(ctr-indx, ctr-1); 
			sublist.forEach(action -> handlers.add(new MessageHandler(action)));
			
			handlers.forEach(service::submit);
			handlers.forEach(action -> {
				try {
					RawMessage obj = service.take().get();
					if (obj != null) {
						result.add(obj);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
			prev = result.size()-prev;
			
			 if (result.size() == 0) {
				logger.info("No elements present in first " + ctr);
				break;
			}
			 DateFormat dtformat =DateFormat.getDateInstance(); 
			String startDate = dtformat.format(cow.get(ctr-indx).getSentDate());
			String endDate = dtformat.format(cow.get(ctr-1).getSentDate());
			logger.info("Total Orders from "+startDate+" to end Date "+endDate+ " are "+prev );
//			cow.removeAll(sublist);
			ctr -=indx;
			if(result.size() > 500) {
				resultList = resultList.union(getSession().createDataset(result, Encoders.bean(RawMessage.class)));
				logger.info("total elements in dataset"+resultList.count());
				result.clear();
				prev=0;
			}
		}
		resultList = resultList.union(getSession().createDataset(result, Encoders.bean(RawMessage.class)));
		resultList.write().csv("D:/shared/sample.csv");
	}
	public static void main(String arg[]) throws Exception
	{
		SparkOrderCollector spoc = new SparkOrderCollector();
		spoc.init();
//		spoc.process();
		spoc.processBatch();
	}
}

class MessageHandler implements Callable<RawMessage> {
	Message msg;

	public MessageHandler(Message msgs) {
		this.msg = msgs;
	}

	@Override
	public RawMessage call() throws MessagingException, IOException {
		
		String subj;
		try {
			subj = msg.getSubject();
		} catch (MessagingException e) {
			if(!msg.getFolder().isOpen())
			{
				msg.getFolder().open(Folder.READ_ONLY);
			}
			subj= msg.getSubject();
		}
		if (StringUtils.isEmpty(subj) || !(subj.contains("durgaveg"))) {
			return null;
		}
		RawMessage ret = new RawMessage();
		ret.setSubject(subj);
		ret.setDate(msg.getSentDate());
		ret.setHtmlMessage(String.valueOf(msg.getContent()));
		return ret;
	}
}