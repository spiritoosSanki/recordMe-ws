package com.ninsina.recordMe.core;

public class Session {

	public String sid;
	public long datetime;
	
	public Session() {
	}
	
	public Session(String sid, long date) {
		this.sid = sid;
		this.datetime = date;
	}

	
	
	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	@Override
	public String toString() {
		return "Session [sid=" + sid + ", datetime=" + datetime + "]";
	}
}
