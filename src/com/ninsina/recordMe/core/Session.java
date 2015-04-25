package com.ninsina.recordMe.core;

public class Session {

	public String id;
	public long datetime;
	
	public Session() {
	}
	
	public Session(String sid, long date) {
		this.id = sid;
		this.datetime = date;
	}

	
	
	public String getSid() {
		return id;
	}

	public void setSid(String sid) {
		this.id = sid;
	}

	public long getDatetime() {
		return datetime;
	}

	public void setDatetime(long datetime) {
		this.datetime = datetime;
	}

	@Override
	public String toString() {
		return "Session [sid=" + id + ", datetime=" + datetime + "]";
	}
}
