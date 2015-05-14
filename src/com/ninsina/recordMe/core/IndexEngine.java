package com.ninsina.recordMe.core;

import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.model.IndexOptions;
import com.ninsina.recordMe.sdk.User;
import com.ninsina.recordMe.ws.users.UsersService;

//TODO in the future do all this in script when installing RecMe-ws
public class IndexEngine {
	
	
	
	public static void init(long ttl, String rootLogin, String rootPsw) {
		initIndex(ttl);
		initRoot(rootLogin, rootPsw);
	}
	
	private static void initRoot(String rootLogin, String rootPsw) {
		try {
			User user = new User();
			user.id = "recme-root";
			user.email = rootLogin;
			user.password = rootPsw;
			user.type = User.TYPE_ROOT;
			user.valid = true;
			ObjectEngine.putObject(user, UsersService.TYPE_USERS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initIndex(long ttl) {
		Document key = new Document(SecurityEngine.FIELD_TTL, 1);
		IndexOptions options = new IndexOptions();
		options.expireAfter(ttl, TimeUnit.MINUTES);
		ObjectEngine.database.getCollection(SecurityEngine.TYPE_SESSIONS).createIndex(key, options);
		
//		(new DB(ObjectEngine.client, ObjectEngine.dbName)).command("db." + SecurityEngine.TYPE_SESSIONS + ".createIndex( { \"createdAt\": 1 }, { expireAfterSeconds: " + (ttl * 60) + " } )");
		
	}
}
