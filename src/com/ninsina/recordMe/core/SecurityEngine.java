package com.ninsina.recordMe.core;

import java.util.Date;
import java.util.UUID;

import com.ninsina.recordMe.sdk.User;
import com.ninsina.recordMe.ws.users.UsersService;

public class SecurityEngine {
	
	static String TYPE_SESSIONS = "sessions";
	static String FIELD_TTL = "datetime";
	
	private static UsersService usersService = new UsersService();
	
	public static User checkUserAccess(String sessionId, short... args) throws RecMeException {
		
		if(sessionId == null || "null".equals(sessionId)) {
			throw new RecMeException(401, "Request without session id");
		}
		
		try {
			Session session = ObjectEngine.getObject(sessionId, TYPE_SESSIONS, Session.class);
			if(session == null) {
				throw new RecMeException(401, "Session expired");
			}
			session.datetime = (new Date()).getTime();
			ObjectEngine.putObject(session, TYPE_SESSIONS);
		} catch(Exception e) {
			throw new RecMeException(401, "Session expired");
		}
		
		try {
			User user = usersService.uncheckedGet(userIdFromSid(sessionId));
			if(user.type == User.TYPE_ROOT) {
				return user;
			}
			if(user.valid) {
				for(short arg : args) {
					if(user.type == arg) {
						return user;
					}
				}
			}
		} catch(RecMeException e) {
			e.printStackTrace();
		}
		throw new RecMeException(401, "Unauthorized");
	}

	public static String generateAndRegisterSid(User user) throws Exception {
		String sid = generateSid(user);
		ObjectEngine.putObject(new Session(sid, (new Date()).getTime()), TYPE_SESSIONS);
		return sid;
	}
	
	public static String userIdFromSid(String sid) {
		return sid.split("_")[0];
	}
	
	private static String generateSid(User user) {
		return user.id + "_" + UUID.randomUUID().toString();
	}
	
}
