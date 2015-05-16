package com.ninsina.recordMe.ws.basicRecords;

import java.util.Date;

import com.ninsina.recordMe.core.ObjectEngine;
import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.core.SecurityEngine;
import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.User;
import com.ninsina.recordMe.sdk.record.BasicRecord;
import com.ninsina.recordMe.sdk.record.BasicRecord.GENERAL_TAGS;
import com.ninsina.recordMe.sdk.record.BasicRecord.SPECIFIC_TAGS;
import com.ninsina.recordMe.ws.users.UsersService;

public class BasicRecordsService {
	
	private static String TYPE_RECORD = "record";
	
	private static UsersService usersService = new UsersService();

	public void create(String sid, BasicRecord record) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sid, User.TYPE_ADMIN, User.TYPE_FOREIGN_ADMIN, User.TYPE_USER);
		try {
			checkParam(record);
			if(currentUser.type == User.TYPE_ROOT || currentUser.type == User.TYPE_ADMIN) {
				if(record.userId == null) {
					throw new RecMeException(400, "User id is null");
				}
				if(usersService.uncheckedGet(record.userId) == null) {
					throw new RecMeException(404, "User does not exist");
				}
			} else if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				if(record.userId == null) {
					throw new RecMeException(400, "User id is null");
				}
				User targetUser = usersService.uncheckedGet(record.userId);
				if(targetUser == null) {
					throw new RecMeException(404, "User does not exist");
				}
				if(!targetUser.foreignAdminIds.contains(record.userId)) {
					throw new RecMeException(401, "No privilege for this user");
				}
			} else if(currentUser.type == User.TYPE_USER) {
				record.userId = currentUser.id;
			}
			
			ObjectEngine.putObject(record, TYPE_RECORD);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	private void checkParam(BasicRecord record) throws RecMeException {
		Date now = new Date();
		try {
			Date date = RecordMe.getDateFromIso8601UTCString(record.dateTime);
			if(date.after(now)) {
				throw new RecMeException(400, "dateTime is in the future.");
			}
		} catch (Exception e1) {
			throw new RecMeException(400, "dateTime field is malformed. Format supported is ISO 8601 : 2015-05-09T16:08:41Z");
		}
		try {
			Date end = RecordMe.getDateFromIso8601UTCString(record.endTime);
			if(end.after(now)) {
				throw new RecMeException(400, "endTime is in the future.");
			}
		} catch (Exception e1) {
			throw new RecMeException(400, "endTime field is malformed. Format supported is ISO 8601 : 2015-05-09T16:08:41Z");
		}
		
		if(record.generalTag == GENERAL_TAGS.DEFAULT) {
			throw new RecMeException(400, "You must specify a primary tag");
		}
		
		if(record.specificTag == SPECIFIC_TAGS.DEFAULT) {
			throw new RecMeException(400, "You must specify at least one secondary tag");
		}
		
	}

	public void remove(String sessionId, String recordId) throws RecMeException {
		try {
			
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	public void update(String sessionId, BasicRecord record) throws RecMeException {
		try {
			
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	public BasicRecord get(String sessionId, String recordId) throws RecMeException {
		try {
			
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

}
