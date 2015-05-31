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

	public static String TYPE_USERS = "users";
	
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
			
		} catch(RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}
	
	//TODO filter fields like passwords
	// and filter depending on user connected
	public User get(String sid, String userId) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sid, User.TYPE_ADMIN, User.TYPE_FOREIGN_ADMIN, User.TYPE_USER);
		try {
			if(userId == null || "null".equals(userId) || currentUser.id.equals(userId)) {
				currentUser.validToken = "";
				return currentUser;
			}
			
			if(currentUser.type == User.TYPE_ROOT || currentUser.type == User.TYPE_ADMIN) {
				return uncheckedGet(userId);
			} else if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				List<User> tmp = uncheckedSearch(
						Arrays.asList(Arrays.asList(
								new Term("id", Term.OPERATOR_EQ, userId),
								new Term("creatorId", Term.OPERATOR_EQ, currentUser.id))
						), 
						0, 
						1
				);
				if(tmp.size() != 1) {
					throw new RecMeException(401, "Wrong privileges. Foreign admin can get only himself and user he created.");
				} else {
					User user = tmp.get(0);
					user.validToken = "";
					return user;
				}
			} else if(currentUser.type == User.TYPE_USER && currentUser.id.equals(userId)) {
				currentUser.validToken = "";
				return currentUser;
			} else {
				throw new RecMeException(401, "Wrong privileges. A user can only get himself");
			}
		} catch(RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}
	
	public User uncheckedGet(String id) throws RecMeException {
		try {
			return ObjectEngine.getObject(id, TYPE_USERS, User.class);
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	//TODO different users type can update different field
	public void update(String sessionId, User user) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sessionId, User.TYPE_ADMIN, User.TYPE_FOREIGN_ADMIN, User.TYPE_USER);
		try {
			User oldUser = uncheckedGet(user.id);
			if(oldUser == null) {
				throw new RecMeException(404, "User does not exist");
			}
			if(currentUser.type == User.TYPE_ADMIN) {
				if(user.type == User.TYPE_ROOT) {
					throw new RecMeException(401, "Admin can only update users, foreign admins and himself");
				}
			} else if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				if(user.type != User.TYPE_USER && user.type != User.TYPE_FOREIGN_ADMIN) {
					throw new RecMeException(401, "Foreign admin can only update users and himself");
				}
			}
			
			if(!oldUser.password.equals(user.oldPassword)) {
				throw new RecMeException(400, "Password and old password don't match"); 
			}
			
			User updated = null;
			if(currentUser.id.equals(user.id)) {
				updated = copySelf(oldUser, user);
			} else {
				if(currentUser.type == User.TYPE_USER) {
					throw new RecMeException(401, "Users can only update themselves");
				}
				if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
					if(oldUser.foreignAdminIds == null) {
						throw new RecMeException();
					}
					if(!oldUser.foreignAdminIds.contains(currentUser.id)) {
						throw new RecMeException(401, "Foreign admin can only update user that has authorized them.");
					}
				}
				
				updated = copyLowerUser(currentUser, oldUser, user);
			}
			
			//TODO send update mail
			ObjectEngine.updateObject(updated, TYPE_USERS);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}
	
	private User copyLowerUser(User currentUser, User oldUser, User user) throws RecMeException {
		User copy = new User();
		copy.id = oldUser.id;
		copy.foreignAdminIds = oldUser.foreignAdminIds;
		copy.email = user.email;
		copy.firstName = user.firstName;
		copy.lastName = user.lastName;
		copy.password = user.password;
		copy.oldPassword = user.password;
		copy.properties = user.properties;
		copy.birth = user.birth;
		copy.inscriptionDate = user.inscriptionDate;
		if(user.type != oldUser.type) {
			if(currentUser.type == User.TYPE_ROOT) {
				
			} else if(currentUser.type == User.TYPE_ADMIN) {
				if(user.type != User.TYPE_FOREIGN_ADMIN || user.type != User.TYPE_USER) {
					throw new RecMeException(401, "Admin can only change privilege to FOREIGN_ADMIN or USER");
				}
			} else if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				if(user.type != User.TYPE_USER) {
					throw new RecMeException(401, "foreign admin can only change privilege USER");
				}
			}
		}
		copy.type = oldUser.type; // no changing type allowed
		
		if(user.valid != oldUser.valid) {
			if(currentUser.type != User.TYPE_ROOT || currentUser.type != User.TYPE_ADMIN) {
				throw new RecMeException(401, "Not enough privilege to change validation");
			}
		}
		copy.valid = user.valid;
		copy.validDate = oldUser.validDate;
		copy.validToken = oldUser.validToken;
		
		return copy;
	}

	private User copySelf(User oldUser, User user) throws RecMeException {
		User copy = new User();
		copy.id = oldUser.id;
		copy.foreignAdminIds = oldUser.foreignAdminIds;
		copy.email = user.email;
		copy.firstName = user.firstName;
		copy.lastName = user.lastName;
		copy.password = user.password;
		copy.oldPassword = user.password;
		copy.properties = user.properties;
		copy.birth = user.birth;
		copy.inscriptionDate = user.inscriptionDate;
		if(user.type != oldUser.type) {
			throw new RecMeException(401, "Cannot not change your own privileges");
		}
		copy.type = oldUser.type;
		if(user.valid != oldUser.valid) {
			throw new RecMeException(401, "Cannot not change your own validation");
		}
		copy.valid = oldUser.valid;
		copy.validDate = oldUser.validDate;
		copy.validToken = oldUser.validToken;
		
		return copy;
	}

	public void create(String sessionId, User user) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sessionId, User.TYPE_ADMIN);
		try {
			checkParam(user);
			if(currentUser.type == User.TYPE_ADMIN) {
				if(user.type != User.TYPE_USER && user.type != User.TYPE_FOREIGN_ADMIN) {
					throw new RecMeException(401, "Admin can only create users and foreign admins");
				}
			} else if(currentUser.type == User.TYPE_FOREIGN_ADMIN) {
				if(user.type != User.TYPE_USER) {
					throw new RecMeException(400, "Foreign admin can only create users");
				}
				user.foreignAdminIds.add(currentUser.id);
			}
			if(uncheckedGet(user.id) != null) {
				throw new RecMeException(409, "User already exist");
			}
			
			List<User> users = uncheckedSearch(Arrays.asList(Arrays.asList(new Term("email", Term.OPERATOR_EQ, user.email))), 0, 1);
			if(users.size() != 0) {
				throw new RecMeException(400, "Choose another email");
			}
			
			user.oldPassword = user.password;
			
			user.validToken = generateToken(user);
			user.validDate = RecordMe.getIso8601UTCDateString(null);
			user.valid = false;
			//TODO send mail
			ObjectEngine.putObject(user, TYPE_USERS);
		} catch (RecMeException e) {
			throw e;
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
		if(user.password == null || user.password.length() < 8) {
			throw new RecMeException(400, "Password must be at least 8 characters long");
		}
		//TODO test email format etc...
	}

	public void validate(String token) throws RecMeException {
		try {
			User user = uncheckedGet(userIdFromToken(token));
			if(user == null) {
				throw new RecMeException(400, "Bad token");
			}
			if(!user.validToken.equals(token)) {
				throw new RecMeException(400, "Bad token");
			}
			user.valid = true;
			user.validToken = "";
			ObjectEngine.putObject(user, TYPE_USERS);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}

	public List<User> uncheckedSearch(List<List<Term>> terms, int offset, int bucketSize) throws RecMeException {
		return ObjectEngine.search(terms, TYPE_USERS, offset, bucketSize, User.class);
	}

	public void remove(String sessionId, String userId) throws RecMeException {
		User currentUser = SecurityEngine.checkUserAccess(sessionId, User.TYPE_ADMIN, User.TYPE_USER);
		try {
			if(currentUser.type != User.TYPE_USER && SecurityEngine.userIdFromSid(sessionId).equals(userId)) {
				throw new RecMeException(401, "Can not remove self");
			}
			User user = uncheckedGet(userId);
			if(user == null) {
				throw new RecMeException(404, "User does not exist");
			}
			if(currentUser.type == User.TYPE_ADMIN) {
				if(user.type != User.TYPE_USER && user.type != User.TYPE_FOREIGN_ADMIN) {
					throw new RecMeException(401, "Admin can only remove users and foreign admins");
				}
			}
			if(currentUser.type == User.TYPE_FOREIGN_ADMIN && !user.foreignAdminIds.contains(currentUser.id)) {
				throw new RecMeException(404, "User does not exist"); // general error message. To not hint at users id outside the scope
			}
			//TODO send mail ?
			ObjectEngine.removeObject(userId, TYPE_USERS);
		} catch (RecMeException e) {
			throw e;
		} catch (Exception e) {
			throw new RecMeException();
		}
	}
	
}
