package org.durgaveg.com.vo;

import java.io.Serializable;
import java.util.Date;

public class RawMessage implements Serializable {

	String subject;
	String htmlMessage;
	String from;
	String date;
	static final String delim = "!#";
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getHtmlMessage() {
		return htmlMessage;
	}
	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setDate(Date sentDate) {
		// TODO Auto-generated method stub
		this.date = sentDate != null ? sentDate.toString():null;
	}
	 
	public String toString() {
		 return new StringBuilder().append(subject).append(delim).append(date).append(delim).append(htmlMessage).toString();
	}
}
