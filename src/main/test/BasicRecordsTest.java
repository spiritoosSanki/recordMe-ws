package main.test;

import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.User;
import com.ninsina.recordMe.sdk.record.BasicRecord;
import com.ninsina.recordMe.sdk.record.Measure;

public class BasicRecordsTest {

	private static RecordMe recMe;

	public static void main(String[] args) throws RecMeException {
		recMe = new RecordMe("http://localhost:9090/recordMe-ws");
		
		String sid = recMe.Users.login("sankargri.mm@gmail.com", "12345678");

//		create(sid);
		
//		get(sid);

		delete(sid);
		
		System.out.println("end");
	}

	private static void get(String sid) throws RecMeException {
		System.out.println(recMe.BasicRecords.get(sid, "firstRec"));
	}

	private static void delete(String sid) throws RecMeException {
		recMe.BasicRecords.remove(sid, "firstRec");
	}

	private static void create(String sid) throws RecMeException {
		BasicRecord br = new BasicRecord();
		br.id = "firstRec";
		br.dateTime = RecordMe.getIso8601UTCDateString(null);
		br.from = "test";
		br.generalTag = BasicRecord.GENERAL_TAGS.SPORT;
		br.specificTag = BasicRecord.SPECIFIC_TAGS.SPORT.RUNNING;
		br.measureProperties.put("duration", new Measure("18000", Measure.UNIT.DURATION));
		recMe.BasicRecords.create(sid, br);
	}
	
}
