package com.ninsina.recordMe.core;

import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.logging.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class MemoryCache {
	
	private static HazelcastInstance hz;

	static Logger logger = Logger.getLogger(MemoryCache.class);

	public static void init(int sessionTTL) {
		logger.info("initializing memory cache...");
		if (hz == null) {
			try {
				Config cfg = new com.hazelcast.config.ClasspathXmlConfig("hazelcast.xml");
				hz = Hazelcast.newHazelcastInstance(cfg);
								
			} catch (Exception ex) {
				logger.info("hazelcast error during initializing", ex);
			}
		}
//		MapConfig mapConfig = hz.getConfig().getMapConfig(SESSIONS);
//		mapConfig.setMaxIdleSeconds(sessionTTL*60);
		logger.info("memory cache ready!");
	}
	
	public static void shutdown() {
		try {
			hz.getLifecycleService().shutdown();
		} catch (Exception ex) {
			logger.info("hazelcast error during shutdown", ex);
		}
	}
	
	public static Object get(String mapName, String key) {
		return hz.getMap(mapName).get(key);
	}

	public static void put(String mapName, String key, Object obj) {
		hz.getMap(mapName).put(key, obj);
	}
	
	public static void put(String mapName, String key, Object obj, long ttl) {
		hz.getMap(mapName).put(key, obj, ttl, TimeUnit.MINUTES);
	}
	
	public static void remove(String mapName, String key) {
		hz.getMap(mapName).remove(key);
	}

	public static int size(String mapName) {
		return hz.getMap(mapName).size();
	}

}
