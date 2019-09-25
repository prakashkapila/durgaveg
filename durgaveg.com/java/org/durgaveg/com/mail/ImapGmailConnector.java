package org.durgaveg.com.mail;

import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ImapGmailConnector {
	
		private Session session;
		private Store store;
		private Folder folder;

		// hardcoding protocol and the folder
		// it can be parameterized and enhanced as required
		private String protocol = "imaps";
		private String file = "INBOX";

		 

		public boolean isLoggedIn() {
			return store.isConnected();
		}

		/**
		 * to login to the mail host server
		 */
		public void login(String host, String username, String password)
				throws Exception {
			URLName url = new URLName(protocol, host, 993, file, username, password);

			if (session == null) {
				Properties props = null;
				try {
					props = System.getProperties();
				} catch (SecurityException sex) {
					props = new Properties();
				}
				session = Session.getInstance(props, null);
			}
			store = session.getStore(url);
			store.connect();
			folder = store.getFolder(url);

			folder.open(Folder.READ_WRITE);
		}

		/**
		 * to logout from the mail host server
		 */
		public void logout() throws MessagingException {
			folder.close(false);
			store.close();
			store = null;
			session = null;
		}

		public int getMessageCount() {
			int messageCount = 0;
			try {
				messageCount = folder.getMessageCount();
			} catch (MessagingException me) {
				me.printStackTrace();
			}
			return messageCount;
		}

		public Message[] getMessages() throws MessagingException {
			
			return folder.getMessages(22200,22279);
		}
		String subj="";
		public List<Message> processDurgaVeg() throws MessagingException {
			Message[] msgs = getMessages();
			
			List<Message> ret = Arrays.asList(msgs).stream().filter(x->{
				try {
					subj=x.getSubject();
					if(StringUtils.isEmpty(subj))
						return false;
					return subj.contains("durgaveg.com");
				} catch (MessagingException e) {
			 		e.printStackTrace();
				}
				return false;
			})
			.collect(Collectors.toList());
			return ret;
		} 
		// wip
		// create spark context
		// pass
		// on the messages
		// compile those messages into objects
		// convert objects into csv.
		
		public static void main(String arg[]) throws Exception
		{
			ImapGmailConnector mailService = new ImapGmailConnector();
			mailService.login("imap.gmail.com", "*********@gmail.com",
					"**********");
			int messageCount = mailService.getMessageCount();
			System.out.println(" mail count is"+messageCount);
			for(Message msg : mailService.processDurgaVeg()) {
				System.out.println(msg.getSubject()+" "+msg.getSentDate());
			//	System.out.println(msg.getContentType());
				System.out.println(msg.getContent());
			}
		}
	}
 

