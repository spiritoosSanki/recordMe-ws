package com.ninsina.recordMe.sdk.stats;

public class Snapshot {
	public String id;
	public Object total;
	public Object min;
	public Object max;

	public Snapshot() {
	}
	
	public Snapshot(Object totalz, Object minz, Object maxz) {
		total = totalz;
		min = minz;
		max = maxz;
	}

	@Override
	public String toString() {
		return "Snapshot [total=" + total + ", min=" + min + ", max=" + max + "]";
	}
	
	
}
