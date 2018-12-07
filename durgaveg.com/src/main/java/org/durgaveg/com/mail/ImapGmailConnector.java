package org.durgaveg.com.mail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;

public class ImapGmailConnector {
	
	public void getEmails(String userName,String passwd)
	{
		Properties prop = new Properties();
		prop.put("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(prop);
		try {
			Store emailStore = session.getStore("imaps" );
			emailStore.connect("imap.gmail.com", "prakashkapila@gmail.com", "primeminister");
			Folder inbox = emailStore.getFolder("INBOX");
			Folder[] list = emailStore.getPersonalNamespaces();
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String arg[])
	{
		ImapGmailConnector imap = new ImapGmailConnector();
		imap.getEmails("", "");
	}
}
