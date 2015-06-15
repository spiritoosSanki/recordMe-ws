package com.ninsina.recordMe.sdk.record;

import java.util.HashMap;
import java.util.Map;

import com.ninsina.recordMe.sdk.Event;

/**
 * Represent any kind of record.
 * All other types of record are transformed in BasicRecord when stored.
 * 
 * Use {@link #measureProperties} to register all measurements. The values are used for statistics.
 * For all other values, use {@link #otherProperties}
 * */
public class BasicRecord {
	
	public String id;
	
	/** mandatory fields **/
	
	/**
	 * Id of the user the record belongs to.
	 * */
	public String userId;
	
	/**
	 * Date time of the record. 
	 * If {@link #endTime} is filled, it is considered as start date time of the record.
	 * */
	public String dateTime;
	
	public long generalTag = GENERAL_TAGS.DEFAULT;
	
	public long specificTag = SPECIFIC_TAGS.DEFAULT;
	
	/** origine of the record : device ? phone ? app ? */
	public String from;
	
	
	
	
	/** non mandatory **/
	
	/**
	 * A record can be attached to an {@link Event}. 
	 * */
	public String eventId;
	
	/**
	 * Properties for putting all measures. Will be used for statistics API.
	 * */
	public Map<String, Measure> measureProperties = new HashMap<String, Measure>();
	
	/**
	 * All other properties.
	 * */
	public Map<String, String> otherProperties = new HashMap<String, String>();

	public static double longitude = Double.NaN;
	public static double latitude = Double.NaN;
	
	
	/** Constants **/
	
	public static class GENERAL_TAGS {
		public static long DEFAULT = 0;
		
		public static long SPORT = 1;
		public static long CULTURE = 2;
		public static long FINANCE = 3;
		public static long HEALTH = 4;
	}
	
	public static class SPECIFIC_TAGS {
		
		public static long DEFAULT = 100;
		
		public static class SPORT {
			public static long FITNESS_TRAIL = 101;
			public static long RUNNING = 102;
			public static long POOL_SWIMMING = 103;
			public static long STRENGTH_TRAINING = 104;
		}
		
		public static class CULTURE {
			public static long MOVIE = 201;
			public static long SERIE = 202;
			public static long OPERA = 203;
			public static long BOOK = 204;
			public static long AUDIO_BOOK = 205;
		}
		
		public static class HEALTH {
			public static long WEIGHT = 401;
		}
	}

	
	
	@Override
	public String toString() {
		return "BasicRecord [id=" + id + ", userId=" + userId + ", dateTime="
				+ dateTime + ", generalTag=" + generalTag + ", specificTag="
				+ specificTag + ", from=" + from + ", eventId=" + eventId
				+ ", measureProperties=" + measureProperties
				+ ", otherProperties=" + otherProperties + "]";
	}
	
	
	
	
}
