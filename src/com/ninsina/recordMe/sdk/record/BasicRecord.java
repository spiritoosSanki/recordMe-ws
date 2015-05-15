package com.ninsina.recordMe.sdk.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicRecord {
	
	public String id;
	
	/** mandatory fields **/
	
	public String userId;
	
	public String dateTime;
	
	/** Two record of the same prime tag can not overlap one another */
	public long primaryTag = PRIMARY_TAG.DEFAULT;
	
	/** Can overlap */
	public List<Long> secondaryTags = new ArrayList<Long>();
	
	/** origine of the record : device ? phone ? */
	public static String from;
	
	/** non mandatory **/
	
	public String endTime;
	
	public Map<String, ValUnit> properties = new HashMap<String, ValUnit>();
	
	
	
	/** Constants **/
	
	public static class PRIMARY_TAG {
		public static long DEFAULT = 0;
		
		public static long SPORT = 1;
		public static long CULTURE = 2;
		public static long FINANCE = 3;
		public static long HEALTH = 4;
	}
	
	public static class SECONDARY_TAG {
		
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
		}
	}
	
}
