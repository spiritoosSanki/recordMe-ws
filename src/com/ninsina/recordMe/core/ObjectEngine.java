package com.ninsina.recordMe.core;


import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Filters.ne;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import com.ninsina.recordMe.sdk.Term;

public class ObjectEngine {
	
	private static ObjectEngine instance;
	
	private static MongoClient client;
	public static MongoDatabase database;
	
	public ObjectEngine(String uris, String dbName) {
		client = new MongoClient(new MongoClientURI(uris));
		database = client.getDatabase(dbName);
	}

	public static void init(String uris, String dbName) {
		if(instance == null) {
			instance = new ObjectEngine(uris, dbName);
		}
	}
	
	public static <T> void putObject(T object, String type) throws Exception {
		Document document = (Document) JSON.parse(RecMeJSON.mapper.writeValueAsString(object));
		if(document.getString("id") != null) {
			document.put("_id", document.getString("id"));
		}
		ObjectEngine.database.getCollection(type).insertOne(document);
	}
	
	public static <T> T getObject(String id, String type, Class<T> outputClass) throws Exception {
		Document document = ObjectEngine.database.getCollection(type).find(eq("_id", id)).first();
		if(document == null) {
			return null;
		}
		return RecMeJSON.mapper.readValue(document.toJson(), outputClass);
	}
	
	public static <T> List<T> search(List<List<Term>> terms, String type, Class<T> outputClass) throws RecMeException {
		List<T> results = new ArrayList<T>();
		
		List<Bson> ors = new ArrayList<Bson>();
		for(List<Term> orTerms : terms) {
			List<Bson> ands = new ArrayList<Bson>();
			for(Term term : orTerms) {
				Bson operation = null;
				if(term.operator == Term.OPERATOR_EQ) {
					operation = eq(parseProperty(term.property), parseValue(term.value));
				} else if(term.operator == Term.OPERATOR_NE) {
					operation = ne(parseProperty(term.property), parseValue(term.value));
				} else if(term.operator == Term.OPERATOR_GT) {
					operation = gt(parseProperty(term.property), parseValue(term.value));
				} else if(term.operator == Term.OPERATOR_GTE) {
					operation = gte(parseProperty(term.property), parseValue(term.value));
				} else if(term.operator == Term.OPERATOR_LT) {
					operation = lt(parseProperty(term.property), parseValue(term.value));
				} else if(term.operator == Term.OPERATOR_LTE) {
					operation = lte(parseProperty(term.property), parseValue(term.value));
				} else {
					throw new RecMeException(400, "Term operator '" + term.operator +"' does not exist.");
				}
				ands.add(operation);
			}
			ors.add(and(ands));
		}
		
		Bson query = or(ors);
		
		MongoCursor<Document> cursor = ObjectEngine.database.getCollection(type).find(query).iterator();
		
		try {
		    while(cursor.hasNext()) {
		        results.add(RecMeJSON.mapper.readValue(cursor.next().toJson(), outputClass));
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    cursor.close();
		}
		
		return results;
	}

	private static String parseProperty(String property) {
		if("id".equals(property)) {
			return "_id";
		} else {
			return property;
		}
		
	}

	private static Object parseValue(String value) {
		try {
			return Float.parseFloat(value);
		} catch(Exception e) {
			try {
				return Integer.parseInt(value);
			} catch(Exception e2) {
				try {
					return Boolean.parseBoolean(value);
				} catch(Exception e3) {
					return value;
				}
			}
		}
	}
}
