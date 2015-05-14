package com.ninsina.recordMe.core;

import java.util.Date;

public class Session {

	/** Session id */
	public String id;
	public Date datetime;
	
	public Session() {
	}
	
	public Session(String sid, Date date) {
		this.id = sid;
		this.datetime = date;
	}

	
	
	public String getId() {
		return id;
	}

	public void setId(String sid) {
		this.id = sid;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date date) {
		datetime = date;
	}
	
	@Override
	public String toString() {
		return "Session [sid=" + id + ", datetime=" + datetime + "]";
	}

}
