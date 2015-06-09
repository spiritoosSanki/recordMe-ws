package com.ninsina.recordMe.sdk;

public class Term {

	public static final short OPERATOR_EQ = 1;
	public static final short OPERATOR_NE = 2;
	public static final short OPERATOR_GT = 3;
	public static final short OPERATOR_GTE = 4;
	public static final short OPERATOR_LT = 5;
	public static final short OPERATOR_LTE = 6;

	public static final short TYPE_STRING = 1;
	public static final short TYPE_INT = 2;
	public static final short TYPE_FLOAT = 3;
	public static final short TYPE_BOOLEAN = 4;
	
	
	public String property;
	public short operator;
	public short type;
	public String value;
	
	public Term() {
		
	}
	
	public Term(String property, short operator, short type, String value) {
		this.property = property;
		this.operator = operator;
		this.type = type;
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
