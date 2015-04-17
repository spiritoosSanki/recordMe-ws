package com.ninsina.recordMe.sdk;

public class Session {

	public String sid;
	public String datetime;
	
	public Session() {
		
	}
	
	public Session(String sid, String datetime) {
		this.sid = sid;
		this.datetime = datetime;
	}

	
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
}
