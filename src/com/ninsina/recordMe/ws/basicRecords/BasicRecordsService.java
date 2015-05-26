package com.ninsina.recordMe.ws.basicRecords;

import java.util.Date;
import java.util.UUID;

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
			checkParam(record, true);
			if(ObjectEngine.getObject(record.id, TYPE_RECORD, BasicRecord.class) == null) {
				throw new RecMeException(409, "Record with same id already exists.");
			}
			checkRights(currentUser, record);
			
			ObjectEngine.putObject(record, TYPE_RECORD);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException(); 
		}
	}

	private void checkRights(User currentUser, BasicRecord record) throws RecMeException {
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
	}

	private void checkParam(BasicRecord record, boolean create) throws RecMeException {
		if(create && record.id == null) {
			record.id = UUID.randomUUID().toString();
		}
		
		if(record.from == null || "".equals(record.from)) {
			throw new RecMeException(400, "'from' is null.");
		}
		
		Date now = new Date();
		Date start;
		try {
			start = RecordMe.getDateFromIso8601UTCString(record.dateTime);
			if(start.after(now)) {
				throw new RecMeException(400, "'dateTime' is in the future.");
			}
		} catch (Exception e1) {
			throw new RecMeException(400, "'dateTime' field is malformed. Format supported is ISO 8601 : 2015-05-09T16:08:41Z");
		}
		try {
			if(record.endTime != null) {
				Date end = RecordMe.getDateFromIso8601UTCString(record.endTime);
				if(end.after(now)) {
					throw new RecMeException(400, "'endTime' is in the future.");
				}
				if(start.after(end)) {
					throw new RecMeException(400, "'endTime' is after dateTime.");
				}
			}
		} catch (Exception e1) {
			throw new RecMeException(400, "'endTime' field is malformed. Format supported is ISO 8601 : 2015-05-09T16:08:41Z");
		}
		
		if(record.generalTag == GENERAL_TAGS.DEFAULT) {
			throw new RecMeException(400, "You must specify a primary tag");
		}
		
		if(record.specificTag == SPECIFIC_TAGS.DEFAULT) {
			throw new RecMeException(400, "You must specify a secondary tag");
		}
		
	}

	public void remove(String sid, String recordId) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sid, User.TYPE_ADMIN, User.TYPE_FOREIGN_ADMIN, User.TYPE_USER);
		
		try {
			BasicRecord record = ObjectEngine.getObject(recordId, TYPE_RECORD, BasicRecord.class);
			if(record == null) {
				throw new RecMeException(404, "Record does not exist");
			}
			
			if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				User owner = usersService.uncheckedGet(record.userId);
				if(!owner.foreignAdminIds.contains(currentUser.id)) {
					throw new RecMeException(401, "Not enough privilege");
				}
			} else if(currentUser.type == User.TYPE_USER) {
				if(!record.userId.equals(currentUser.id)) {
					throw new RecMeException(401, "Not enough privilege");
				}
			}
			
			ObjectEngine.removeObject(recordId, TYPE_RECORD);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	public void update(String sid, BasicRecord record) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sid, User.TYPE_ADMIN, User.TYPE_FOREIGN_ADMIN, User.TYPE_USER);
		
		try {
			checkParam(record, false);
			checkRights(currentUser, record);
			
			ObjectEngine.updateObject(record, TYPE_RECORD);
			
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	public BasicRecord get(String sid, String recordId) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sid, User.TYPE_ADMIN, User.TYPE_FOREIGN_ADMIN, User.TYPE_USER);
		
		try {
			BasicRecord record = ObjectEngine.getObject(recordId, TYPE_RECORD, BasicRecord.class);
			if(record == null) {
				throw new RecMeException(404, "Record does not exist");
			}
			
			if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				User owner = usersService.uncheckedGet(record.userId);
				if(!owner.foreignAdminIds.contains(currentUser.id)) {
					throw new RecMeException(401, "Not enough privilege");
				}
			} else if(currentUser.type == User.TYPE_USER) {
				if(!record.userId.equals(currentUser.id)) {
					throw new RecMeException(401, "Not enough privilege");
				}
			}
			
			return record;
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

}
