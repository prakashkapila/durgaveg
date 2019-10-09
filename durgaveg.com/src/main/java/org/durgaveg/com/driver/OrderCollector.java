package org.durgaveg.com.driver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.durgaveg.com.mail.ImapGmailConnector;
import org.durgaveg.com.net.SparkParent;
import org.durgaveg.com.vo.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderCollector extends SparkParent {
	static final Logger logger;
	static {
		logger = LoggerFactory.getLogger(OrderCollector.class);
	}
	ImapGmailConnector msgCon = new ImapGmailConnector();
	Dataset<RawMessage> orderMessages = null;
	public List<Message> fetchMessages(int start, int end) throws Exception{
		return Arrays.asList(msgCon.getMessages(start, end));
	}
	static int msgCountIncr=500;

	public void init() {
		super.initSession();
		orderMessages = getSession().emptyDataset(Encoders.bean(RawMessage.class));
		try {
			msgCon.login();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
	int ctr=0;

	protected void addOrdersMesg(Stream<Message> messages){
		List<RawMessage> msgR = messages.map(map->{
			ctr++;
			if(ctr % 1000 ==0)
			{
				logger.info("Processed "+ctr+" messages");
			}
			RawMessage rm = new RawMessage();
			try {
				rm.setDate(map.getSentDate());
				rm.setSubject(map.getSubject());
				rm.setHtmlMessage(String.valueOf(map.getContent()));
			} catch (IOException | MessagingException e) {
				e.printStackTrace();
			}
			return rm;
		}).collect(Collectors.toList());
		long cnt = msgR.size();
		logger.info("orders to add are "+cnt);
		if( cnt < 1)
			return;
		orderMessages.union(getSession().createDataset(msgR, Encoders.bean(RawMessage.class)));
	}
	int proces=0;
	public void getAllMessages() throws Exception {
		init(); 
		int cnt = msgCon.getMessageCount();
		Message[] messgs =  msgCon.getMessages(10000,cnt);
		logger.info("total messages are "+messgs.length);
		CopyOnWriteArrayList<Message> st = new CopyOnWriteArrayList<Message>((messgs));
		Stream<Message> filtered = st.parallelStream().filter(pred->{
			proces++;
			if(proces % 500 ==0) {
				logger.info("Processed "+proces+" messages");
			}		
			try { 
				if(!pred.getFolder().isOpen())
				{
					pred.getFolder().open(Folder.READ_ONLY);
				}
				String str = pred.getSubject();	
				if(StringUtils.isBlank(str))
					return false;
				return str.contains("durgaveg.com") ;
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return false;
		});
		addOrdersMesg(filtered);
		printSample(orderMessages.takeAsList(5));
	}
	public void getAllMessagesOld() throws Exception {
		init(); 
		int cnt = msgCon.getMessageCount();
		Message[] messgs =  msgCon.getMessages(10000,cnt);
		logger.info("total messages are "+messgs.length);
		long ctr = 0;
		while(ctr < messgs.length) {
			if(ctr+msgCountIncr > messgs.length ) {
				msgCountIncr = (int) (messgs.length-1-ctr);
			}
			Message[] subist = new Message[msgCountIncr];
			System.arraycopy(messgs, (int) ctr, subist, 0, msgCountIncr);
			ConcurrentLinkedQueue<Message> st= new java.util.concurrent.ConcurrentLinkedQueue<Message>(Arrays.asList(subist));	
			Stream<Message> filtered = st.parallelStream().filter(pred->{
				try { 
					if(!pred.getFolder().isOpen())
					{
						pred.getFolder().open(Folder.READ_ONLY);
					}
					String str = pred.getSubject();	
					if(StringUtils.isBlank(str))
						return false;
					return str.contains("durgaveg.com") ;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				return false;
			});
			addOrdersMesg(filtered);
			logger.info(" Total messages processed  "+(ctr+msgCountIncr)+ " of "+messgs.length);
			ctr +=msgCountIncr;
		}
		printSample(orderMessages.takeAsList(5));
	}
	protected RawMessage getRawMessage(Message msg) {
		RawMessage ret = new RawMessage();
		try {
			if(!msg.getFolder().isOpen())
			{
				msg.getFolder().open(Folder.READ_ONLY);
			}
			String sub = msg.getSubject();
			if(StringUtils.isBlank(sub) || (!sub.contains("durgaveg")))
				return null;
			ret.setDate(msg.getSentDate());
			ret.setSubject(sub);
			ret.setHtmlMessage(String.valueOf(msg.getContent()));

		} catch (MessagingException | IOException e) {

			e.printStackTrace();
		}
		return ret;
	}
	
	public void processSerially() {
		init(); 
		CopyOnWriteArrayList<RawMessage> msgs = new CopyOnWriteArrayList<>();
		Dataset<RawMessage> resultRows = null;
 		try {
			Message[] allmsgs = msgCon.getAllMessages(); 
			for(int i=0;i< allmsgs.length;i++)
			{
				RawMessage raw =getRawMessage(allmsgs[i]);
				if(raw != null) {
					msgs.add(raw);
				}
				if(i%1000 ==0) {
					logger.info("processed rows " + i);
					Dataset<RawMessage> res = getSession().createDataset(msgs, Encoders.bean(RawMessage.class));
					resultRows = resultRows == null ?res
							:resultRows.union(res);
				}
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
 	} 
	
	protected void printSample(List<RawMessage> samples)
	{
		samples.forEach(x->logger.info(x.getHtmlMessage()));
	}

	public static void main(String arg[])
	{
		OrderCollector driver = new OrderCollector();
		try {
			driver.processSerially();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
