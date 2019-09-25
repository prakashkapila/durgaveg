package org.durgaveg.com.mail;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class CheckingMails {

   public static void check(String host, String storeType, String user,
      String password) 
   {
      try {

      //create properties field
      Properties properties = new Properties();

      properties.put("mail.pop3.host", host);
      properties.put("mail.pop3.port", "995");
      properties.put("mail.pop3.starttls.enable", "true");
      Session emailSession = Session.getDefaultInstance(properties);
  
      //create the POP3 store object and connect with the pop server
      Store store = emailSession.getStore("pop3s");
      store.connect(host, user, password);
      Folder fold[] = store.getPersonalNamespaces();
      
      //create the folder object and open it
      Folder emailFolder = store.getFolder("Durgaveg");
      emailFolder.open(Folder.READ_ONLY);

      int end = emailFolder.getMessageCount();
      // retrieve the messages from the folder in an array and print it
      Message[] messages = emailFolder.getMessages();
      System.out.println("messages.length---" + messages.length);
      List<Message> msgList = Arrays.asList(messages);
      List<Message> nlist = msgList.stream().filter(x->{
		try {
			System.out.println(x.getSubject());
			return x.getSubject().contains("durgaveg.com");
		} catch (MessagingException e) {
	 		e.printStackTrace();
		}
		return false;
	}).collect(Collectors.toList());
      for (int i = 0, n = msgList.size(); i < n; i++) {
         Message message = msgList.get(i);
         System.out.println("---------------------------------");
         System.out.println("Email Number " + (i + 1));
         System.out.println("Subject: " + message.getSubject());
         System.out.println("From: " + message.getFrom()[0]);
         System.out.println("Text: " + message.getContent().toString());

      }

      //close the store and folder objects
      emailFolder.close(false);
      store.close();

      } catch (NoSuchProviderException e) {
         e.printStackTrace();
      } catch (MessagingException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {

      String host = "pop.gmail.com";//"pop.mail.yahoo.com";// change accordingly
      String mailStoreType = "pop3";
      String username ="m";
      String password = "";// change accordingly

      check(host, mailStoreType, username, password);

   }

}