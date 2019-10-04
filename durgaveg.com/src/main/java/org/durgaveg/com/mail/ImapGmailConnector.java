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
		 * @throws Exception 
		 */
		public void login() throws Exception {
			login("imap.gmail.com", "prakashkapila@gmail.com",
					"primeminister");
		}
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
			 return folder.getMessages();
		}
		public Message[] getMessages(int start,int end) throws Exception {
			if(folder == null)
			{
				login("imap.gmail.com", "prakashkapila@gmail.com",
						"primeminister");
			}	
			 return folder.getMessages(start, end);
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
		
		public Message[] getAllMessages() throws Exception{
			 login("imap.gmail.com", "ur@email.com",
					"password");
			System.out.println(" mail count is"+getMessageCount());
			return getMessages();
		}
		 
	}
 

