package com.ninsina.recordMe.sdk;

public class Term {

	public static short OPERATOR_EQ = 1;
	public static short OPERATOR_NE = 2;
	public static short OPERATOR_GT = 3;
	public static short OPERATOR_GTE = 4;
	public static short OPERATOR_LT = 5;
	public static short OPERATOR_LTE = 6;
	
	public String property;
	public short operator;
	public String value;
	
	public Term() {
		
	}
	
	public Term(String property, short operator, String value) {
		this.property = property;
		this.operator = operator;
		this.value = value;
	}
	
	
	
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public short getOperator() {
		return operator;
	}
	public void setOperator(short operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
