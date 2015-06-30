package com.ninsina.recordMe.core;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.record.BasicRecord;
import com.ninsina.recordMe.sdk.record.Measure;
import com.ninsina.recordMe.sdk.stats.Snapshot;
import com.ninsina.recordMe.ws.users.UsersService;

public class BasicStatsProcessor {

	private static ExecutorService basicStatsProcessor = Executors.newFixedThreadPool(100);
	private static ExecutorService statsProcessor = Executors.newFixedThreadPool(500);
	
	private static final String PLATEFORM_STATS = "stats_plateform";
	private static final String USERS_STATS = "stats_users";

	public static void push(BasicRecord record) {
		push(null, record);
	}
	
	public static void push(final BasicRecord oldRecord, final BasicRecord record) {
		basicStatsProcessor.submit(new Runnable() {
			
			@Override
			public void run() {
				if(oldRecord == null) {
					for(String key : record.measureProperties.keySet()) {
						totalStats(record.dateTime, record.generalTag, record.specificTag, record.measureProperties.get(key));
						userStats(record.dateTime, record.generalTag, record.specificTag, record.measureProperties.get(key));
					}
				} else {
					
				}
			}

			private void userStats(String dateTime, long generalTag, long specificTag, Measure measure) {
				// TODO Auto-generated method stub
				
			}

			private void totalStats(String dateTime, long generalTag, long specificTag, Measure measure) {
				try {
					String yyyyMMdd = dateTime.split("T")[0];
					String[] split = yyyyMMdd.split("-");
					String yyyy = split[0];
					String yyyyMM = yyyy + "-" + split[1];
					Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT0"));
					cal.setTime(RecordMe.getDateFromIso8601UTCString(dateTime));
					cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
					String yyyyMMw = yyyyMM + cal.get(Calendar.DAY_OF_MONTH);
					String generalBaseId = generalTag + "";
					String specificBaseId = generalTag + "-" + specificTag;

					//Global stats
					String tDayId = "all" + "-d-" + yyyyMMdd + "-" + measure.unit;
					Snapshot tDay = ObjectEngine.getObject(tDayId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(tDayId, tDay, measure));
					
					String tWeekId = "all" + "-w-" + yyyyMMw + "-" + measure.unit;
					Snapshot tWeek = ObjectEngine.getObject(tWeekId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(tWeekId, tWeek, measure));
					
					String tMonthId = "all" + "-m-" + yyyyMM + "-" + measure.unit;
					Snapshot tMonth = ObjectEngine.getObject(tMonthId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(tMonthId, tMonth, measure));
					
					String tYearId = "all" + "-y-" + yyyy + "-" + measure.unit;
					Snapshot tYear = ObjectEngine.getObject(tYearId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(tYearId, tYear, measure));

					//Stats for genaral tag only
					String gDayId = generalBaseId + "-d-" + yyyyMMdd + "-" + measure.unit;
					Snapshot gDay = ObjectEngine.getObject(gDayId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(gDayId, gDay, measure));
					
					String gWeekId = generalBaseId + "-w-" + yyyyMMw + "-" + measure.unit;
					Snapshot gWeek = ObjectEngine.getObject(gWeekId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(gWeekId, gWeek, measure));
					
					String gMonthId = generalBaseId + "-m-" + yyyyMM + "-" + measure.unit;
					Snapshot gMonth = ObjectEngine.getObject(gMonthId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(gMonthId, gMonth, measure));
					
					String gYearId = generalBaseId + "-y-" + yyyy + "-" + measure.unit;
					Snapshot gYear = ObjectEngine.getObject(gYearId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(gYearId, gYear, measure));

					//Stats for specific tag
					String sDayId = specificBaseId + "-d-" + yyyyMMdd + "-" + measure.unit;
					Snapshot sDay = ObjectEngine.getObject(sDayId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(sDayId, sDay, measure));
					
					String sWeekId = specificBaseId + "-w-" + yyyyMMw + "-" + measure.unit;
					Snapshot sWeek = ObjectEngine.getObject(sWeekId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(sWeekId, sWeek, measure));
					
					String sMonthId = specificBaseId + "-m-" + yyyyMM + "-" + measure.unit;
					Snapshot sMonth = ObjectEngine.getObject(sMonthId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(sMonthId, sMonth, measure));
					
					String sYearId =  specificBaseId + "-y-" + yyyy + "-" + measure.unit;
					Snapshot sYear = ObjectEngine.getObject(sYearId, PLATEFORM_STATS, Snapshot.class);
					statsProcessor.submit(new SnapshotProcessor(sYearId, sYear, measure));
					
					
				} catch(Exception e) {
					//TODO log
				}
			}
		});
	}
	
	static class SnapshotProcessor implements Runnable {

		private static final String BASIC_STATS_SNAPSHOT = "BASIC_STATS_SNAPSHOT";
		private String id;
		private Snapshot snap;
		private Measure measure;
		
		public SnapshotProcessor(String idz, Snapshot snapz, Measure measurez) {
			id= idz;
			snap = snapz;
			measure = measurez;
		}
		
		@Override
		public void run() {
			
			if (MemoryCache.get(BASIC_STATS_SNAPSHOT, id) == null) {
				// prevent other servers to handle this snapshot at the same time...
				try {
					MemoryCache.put(BASIC_STATS_SNAPSHOT, id, "");
					
					boolean create;
					if(snap == null) {
						create = true;
						Object init = getInitValue(measure);
						snap = new Snapshot(init, init, init);
						snap.id = id;
					} else {
						create = false;
					}
					
					snap.total = add(snap.total, measure);
					snap.max = higher(snap.max, measure);
					snap.min = lower(snap.min, measure);
					
					if(create) {
						ObjectEngine.putObject(snap, PLATEFORM_STATS);
					} else {
						ObjectEngine.updateObject(snap, PLATEFORM_STATS);
					}
				} catch(Exception e) {
					//TODO log
				} finally {
					MemoryCache.remove(BASIC_STATS_SNAPSHOT, id);
				}
				
			} else {
				try {
					Thread.sleep(100);
					statsProcessor.submit(this);
				} catch (InterruptedException e) {
					//TODO log
				}
			}
		}

		private Object lower(Object min, Measure measure2) {
			// TODO Auto-generated method stub
			return null;
		}

		private Object higher(Object max, Measure measure2) {
			// TODO Auto-generated method stub
			return null;
		}

		private Object add(Object total, Measure measure2) {
			// TODO Auto-generated method stub
			return null;
		}

		private Object getInitValue(Measure measure2) {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}
}
