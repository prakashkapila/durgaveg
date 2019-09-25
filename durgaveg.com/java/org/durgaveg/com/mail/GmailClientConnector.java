package org.durgaveg.com.mail;

import java.io.File;
import java.io.FileInputStream;
/////java imports
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.Gmail.Users;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
//6098830026

// jaimee.gilmartin@mail.house.gov 

public class GmailClientConnector {
	private final String APPLICATION_NAME = "Gmail API Java Quickstart";
	private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private final String CREDENTIALS_FOLDER = "credentials"; // Directory to store user credentials.

	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved credentials/ folder.
	 */
	private  List<String> SCOPES = new ArrayList();
	// private final String CLIENT_SECRET_DIR = "client_secret.json";
	private final String CLIENT_SECRET_DIR = "credentials.json";

	public List<Message> listMessagesWithLabels( String userId, List<String> labelIds)
			throws IOException {
		//HashMap map = new HashMap();
		String x;
	//	int y  =1/0;
	//	x = y;
//		Map<Integer,String> capacity= null;
//		for(Entry<Integer, String> entry: capacity.entrySet())
//		{
//			
//		}
		//capacity.va.toArray(new Integer[capacity.keySet().size()]);
		 List<List<Integer>> ret = new ArrayList<List<Integer>>();
	       
		ListMessagesResponse response = service.users().messages().list(userId).setLabelIds(labelIds).execute();
 		List<Message> messages = new ArrayList<Message>();
		while (response.getMessages() != null) {
			messages.addAll(response.getMessages());
			if (response.getNextPageToken() != null) {
				String pageToken = response.getNextPageToken();
				response = service.users().messages().list(userId).setLabelIds(labelIds).setPageToken(pageToken)
						.execute();
			} else {
				break;
			}
		}

		for (Message message : messages) {
			System.out.println(message.toPrettyString());
		}

		return messages;
	}

	/**
	 * 95
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT
	 *            The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException
	 *             If there is no client_secret.
	 */
	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = GmailClientConnector.class.getResourceAsStream(CLIENT_SECRET_DIR);
		File g = new File(CLIENT_SECRET_DIR);
		System.out.println(g.getAbsolutePath());
		InputStreamReader reader = new InputStreamReader(new FileInputStream(g));
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
						.setAccessType("offline").build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("me");
	}

	NetHttpTransport HTTP_TRANSPORT = null;
	Gmail service = null;

	public void printMessages() throws IOException {
		String user = "me";
		ArrayList<String> lbls = new ArrayList<String>();
		lbls.add("INBOX");
 		List<Message> msgs = listMessagesWithLabels(user,lbls );
	}

	public void printLabels() throws IOException {
		String user = "me";
 		ListLabelsResponse listResponse = service.users().labels().list(user).execute();
 		// ListMessagesResponse resp = service.users().messages().list(user).execute();
		List<Label> labels = listResponse.getLabels();
		labels.forEach(x -> {
 			System.out.println(x.getId() + "  " + x.getName());
		});
	}
	
	public void init() throws IOException, GeneralSecurityException {
		// Build a new authorized API client service.
		SCOPES.add(GmailScopes.MAIL_GOOGLE_COM);
		SCOPES.add(GmailScopes.GMAIL_LABELS);
		SCOPES.add(GmailScopes.GMAIL_METADATA);
		SCOPES.add(GmailScopes.GMAIL_READONLY);
		SCOPES.add(GmailScopes.MAIL_GOOGLE_COM);
		
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();
		
		// Print the labels in the user's account.
		
	}

	public static void main(String... args) {
		GmailClientConnector conn = new GmailClientConnector();
		try {
			conn.init();
			conn.printLabels();
			//conn.printMessages();
		} catch (IOException | GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
