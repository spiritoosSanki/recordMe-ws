package com.ninsina.recordMe.ws.users;

import java.util.Arrays;
import java.util.List;

import com.ninsina.recordMe.core.ObjectEngine;
import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.core.SecurityEngine;
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
					User.class
			);
			
			if(res.size() == 1) {
				return SecurityEngine.generateAndRegisterSid(res.get(0));
			} else {
				throw new RecMeException(401, "Wrong login or password");
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

}
