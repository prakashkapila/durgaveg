package org.durgaveg.com.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import org.apache.spark.sql.Row;

public class RawMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String subject;
	String htmlMessage;
	String from;
	String date;
	public static final String delim = "!#";
	public String getSubject() {
		HashMap<String,String> x;
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
	public static RawMessage init(String row) {
		String [] values = row.split(delim);
		RawMessage  val = new RawMessage();
		if(values.length <2)
		{
			return null;
		}
		val.subject = values.length> 0 ? values[0]:"NA";
		val.date = values.length> 1 ? values[1]:"NA";
		val.htmlMessage = values.length> 2 ? values[2]:"NA";
		return val;
	}
}
