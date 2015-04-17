package com.ninsina.recordMe.core;

import java.util.UUID;

import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.Session;
import com.ninsina.recordMe.sdk.User;
import com.ninsina.recordMe.ws.users.UsersService;

public class SecurityEngine {
	
	static String TYPE_SESSIONS = "sessions";
	static String FIELD_TTL = "datetime";
	
	private static UsersService usersService = new UsersService();
	
	public static User checkUserRight(String sessionId, short... args) throws RecMeException {
		
		try {
			User user = usersService.get(userIdFromSid(sessionId));
			//TODO for(args)
		} catch(RecMeException e) {
			throw new RecMeException(401, "Unauthorized");
		}
		throw new RecMeException(401, "Unauthorized");
	}

	public static String generateAndRegisterSid(User user) throws Exception {
		String sid = generateSid(user);
		ObjectEngine.putObject(new Session(sid , RecordMe.getIso8601UTCDateString(null)), TYPE_SESSIONS);
		return sid;
	}
	
	private static String userIdFromSid(String sid) {
		return sid.split("_")[0];
	}
	
	private static String generateSid(User user) {
		return user.id + "_" + UUID.randomUUID().toString();
	}
	
}
