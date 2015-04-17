package com.ninsina.recordMe.ws.users;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.ninsina.recordMe.core.ObjectEngine;
import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.core.SecurityEngine;
import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.Term;
import com.ninsina.recordMe.sdk.User;

public class UsersService {

	private static String TYPE_USERS = "users";
	
	public String login(String login, String password) throws RecMeException {
		
		try {
			List<User> res = ObjectEngine.search(
					Arrays.asList(Arrays.asList(
						new Term("email", Term.OPERATOR_EQ, login),
						new Term("password", Term.OPERATOR_EQ, password)
					)),
					TYPE_USERS,
					0,
					1,
					User.class
			);
			
			if(res.size() == 1) {
				return SecurityEngine.generateAndRegisterSid(res.get(0));
			} else {
				throw new RecMeException(400, "Wrong login or password");
			}
			
		} catch (Exception e) {
			throw new RecMeException();
		}
	}
	
	public User get(String sid, String id) {
		
		return null;
	}
	
	public User get(String id) throws RecMeException {
		try {
			User user = ObjectEngine.getObject(id, TYPE_USERS, User.class);
			if(user == null) {
				throw new RecMeException(404, "User not found");
			}
			return user;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	public void create(String sessionId, User user) throws RecMeException {
		try {
			checkParam(user);
			user.validToken = generateToken(user);
			user.validDate = RecordMe.getIso8601UTCDateString(null);
			user.valid = false;
			//TODO send mail
			ObjectEngine.putObject(user, TYPE_USERS);
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	private String generateToken(User user) {
		return user.id + "_" + UUID.randomUUID().toString();
	}

	private String userIdFromToken(String token) throws RecMeException {
		if(token == null) {
			throw new RecMeException(400, "Bad token (null)");
		}
		String[] split = token.split("_");
		if(split.length != 2) {
			throw new RecMeException(400, "Bad token");
		}
		return split[0];
	}
	
	private void checkParam(User user) throws RecMeException {
		if(user.id == null) {
			user.id = UUID.randomUUID().toString();
		}
		//TODO test email format etc...
	}

	public void validate(String token) throws RecMeException {
		try {
			User user = get(userIdFromToken(token));
			if(user == null) {
				throw new RecMeException(400, "Bad token");
			}
			if(!user.validToken.equals(token)) {
				throw new RecMeException(400, "Bad token");
			}
			user.valid = true;
			ObjectEngine.putObject(user, TYPE_USERS);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

}
