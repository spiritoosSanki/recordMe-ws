package com.ninsina.recordMe.core;

import java.util.concurrent.TimeUnit;

import org.bson.Document;
import com.mongodb.client.model.IndexOptions;

public class IndexEngine {
	
	
	//TODO in the future put index creation in a script when creating the DBs
	public static void initIndexes(long ttl) {
		Document key = new Document(SecurityEngine.FIELD_TTL, 1);
		IndexOptions options = new IndexOptions();
		options.expireAfter(ttl, TimeUnit.MINUTES);
		ObjectEngine.database.getCollection(SecurityEngine.TYPE_SESSIONS).createIndex(key, options);
	}
}
