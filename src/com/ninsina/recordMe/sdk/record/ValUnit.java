package com.ninsina.recordMe.sdk.record;

public class ValUnit {
	
	public String value;
	public String unit;
	
	public static final String DISTANCE = "m";
	public static final String DURATION = "ms";
	public static final String WEIGHT = "g";
	public static final String ENERGY = "j";
	
	
	public ValUnit() {
		
	}
	

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}


	@Override
	public String toString() {
		return "ValUnit [value=" + value + ", unit=" + unit + "]";
	}
	
	
}
