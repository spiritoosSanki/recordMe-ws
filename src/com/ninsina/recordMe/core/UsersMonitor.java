package com.ninsina.recordMe.core;

import org.jboss.resteasy.logging.Logger;

/**
 * Scan users and delete users that have not validated creation after 24 hours
 * */
public class UsersMonitor {

	static Logger logger = Logger.getLogger(UsersMonitor.class);
	
	private static UsersMonitor instance;
	private static java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();

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
			    
			    //TODO get users that have valid=false, test the validDate + 24h
			    //then delete

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				logger.debug("scheduled users scan terminated, sleeping for 24 hours...");
				Thread.sleep(24 * 60 * 60 * 1000);

			} catch (Exception ex) {
				logger.debug("users monitor error", ex);
			} finally {
				// restart
				executor.submit(check);
			}
		}
	};
	
}
