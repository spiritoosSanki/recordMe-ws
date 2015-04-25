package com.ninsina.recordMe.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jboss.resteasy.logging.Logger;

import com.ninsina.recordMe.sdk.RecordMe;
import com.ninsina.recordMe.sdk.Term;
import com.ninsina.recordMe.sdk.User;
import com.ninsina.recordMe.ws.users.UsersService;

/**
 * Scan users and delete users that have not validated creation after 24 hours
 * */
public class UsersMonitor {

	/** in hours */
	private static final int VALIDATION_LIMIT = 24;
	
	private static final String USER_VALID_MONITOR = "USER_VALID_MONITOR";

	static Logger logger = Logger.getLogger(UsersMonitor.class);
	
	private static UsersMonitor instance;
	private static java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();

	private static UsersService usersService = new UsersService();
	
	public static void init() {
		if (instance == null) {
			instance = new UsersMonitor();
		}
	}
	
	public UsersMonitor() {
		executor.submit(check);
	}
	
	private static Runnable check = new Runnable() {
		public void run() {
			try {

			    logger.debug("scanning users...");
			    
			    //TODO refactor with the cache ?
			    //then delete
			    
			    int offset = 0;
				int bucketSize = 1000;
				List<User> users = new ArrayList<User>();
				boolean goOn = true;
				Date now = new Date();
				
				String then = RecordMe.getIso8601UTCDateString(new Date(now.getTime() - (VALIDATION_LIMIT * 60 * 60 * 1000)));
				while(goOn) {
					List<User> tmpUser = usersService.uncheckedSearch(
							Arrays.asList(Arrays.asList(
				    				new Term("valid", Term.OPERATOR_EQ, "false"),
				    				new Term("validDate", Term.OPERATOR_LT, then )
			    			)), 
			    			offset, 
			    			bucketSize);
					if(tmpUser.isEmpty()) {
						goOn = false;
					}
					users.addAll(tmpUser);
					offset += bucketSize;
				}
				
				//TODO bulk it
				for(User user : users) {

					if (MemoryCache.get(USER_VALID_MONITOR, user.id) == null) {
						// prevent other frontends to handle this alert too...
						try {
							MemoryCache.put(USER_VALID_MONITOR, user.id, "");
							ObjectEngine.removeObject(user.id, UsersService.TYPE_USERS);
						} catch(Exception e) {
							throw e;
						} finally {
							MemoryCache.remove(USER_VALID_MONITOR, user.id);
						}
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				logger.debug("scheduled users scan terminated, sleeping for 24 hours...");
				Thread.sleep(VALIDATION_LIMIT * 60 * 60 * 1000);

			} catch (Exception ex) {
				logger.debug("users monitor error", ex);
			} finally {
				// restart
				executor.submit(check);
			}
		}
	};
	
}
