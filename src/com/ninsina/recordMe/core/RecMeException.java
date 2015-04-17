package com.ninsina.recordMe.core;

public class RecMeException extends Exception {

	private static final long serialVersionUID = 6351593680013622669L;

	public int status = 500;
	public String msg  = "Unexpected error";

	public RecMeException(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}
	
	public RecMeException() {
	}
	
	@Override
	public String getMessage() {
		return "Status : " + status + "; Message : " + msg;
	}





	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	
}
