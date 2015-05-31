package com.ninsina.recordMe.sdk.record;

/**
 * Basic measurement object. Used by the statistics processing.
 * Used in {@link BasicRecord#measureProperties}. 
 * */
public class Measure {
	
	public String value;
	/** Unit of the measure. User {@link UNIT}. */
	public String unit;
	
	
	
	public Measure() {
		
	}
	
	
	public static class UNIT {
		public static final String DISTANCE = "m";
		public static final String DURATION = "ms";
		public static final String WEIGHT = "g";
		public static final String ENERGY = "j";
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
