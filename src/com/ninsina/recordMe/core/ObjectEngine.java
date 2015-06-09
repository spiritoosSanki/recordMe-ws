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
import com.mongodb.client.result.UpdateResult;
import com.ninsina.recordMe.sdk.Term;


public class ObjectEngine {
	
	private static ObjectEngine instance;
	
	static MongoClient client;
	static MongoDatabase database;
	static String dbName;
	
	public ObjectEngine(String uris, String dbNamee) {
		client = new MongoClient(new MongoClientURI(uris));
		dbName = dbNamee;
		database = client.getDatabase(dbNamee);
	}

	public static void init(String uris, String dbName) {
		if(instance == null) {
			instance = new ObjectEngine(uris, dbName);
		}
	}
	
	public static <T> void putObject(T object, String type) throws Exception {
		Document document = null;
		if(object instanceof Session) {
			Session session = (Session) object;
		    document = new Document();
		    document.put("id", session.id);
		    document.put("datetime", session.datetime);
		} else {
			document = Document.parse(RecMeJSON.mapper.writeValueAsString(object));
		}
		document = marshalDoc(document);
		ObjectEngine.database.getCollection(type).insertOne(document);
	}
	
	public static <T> void updateObject(T object, String type) throws Exception {
		Document document = null;
		if(object instanceof Session) {
			Session session = (Session) object;
		    document = new Document();
		    document.put("id", session.id);
		    document.put("datetime", session.datetime);
		} else {
			document = Document.parse(RecMeJSON.mapper.writeValueAsString(object));
		}
		document = marshalDoc(document);
		UpdateResult res = ObjectEngine.database.getCollection(type).replaceOne(eq("_id", document.get("id")), document);
		if(res.getModifiedCount() != 1) {
			throw new RecMeException(404, "Object does not exist");
		}
	}
	
	public static void removeObject(String id, String type) throws Exception {
		ObjectEngine.database.getCollection(type).findOneAndDelete(eq("_id", id));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObject(String id, String type, Class<T> outputClass) throws Exception {
		Document document = ObjectEngine.database.getCollection(type).find(eq("_id", id)).first();
		if(document == null) {
			return null;
		}
		document = unmarshalDoc(document);
		if(type.equals(SecurityEngine.TYPE_SESSIONS)) {
			Session session = new Session(document.getString("id"), document.getDate("datetime"));
			return (T) session;
		}
		return RecMeJSON.mapper.readValue(document.toJson(), outputClass);
	}
	
	private static Document marshalDoc(Document document) {
		if(document.getString("id") != null) {
			document.put("_id", document.getString("id"));
		}
		return document;
	}
	
	private static Document unmarshalDoc(Document document) {
		document.remove("_id");
		return document;
	}
	
	public static <T> List<T> search(List<List<Term>> terms, String type, int offset, int bucketSize, Class<T> outputClass) throws RecMeException {
		List<T> results = new ArrayList<T>();
		
		List<Bson> ors = new ArrayList<Bson>();
		for(List<Term> orTerms : terms) {
			List<Bson> ands = new ArrayList<Bson>();
			for(Term term : orTerms) {
				Bson operation = null;
				if(term.operator == Term.OPERATOR_EQ) {
					operation = eq(parseProperty(term.property), parseValue(term));
				} else if(term.operator == Term.OPERATOR_NE) {
					operation = ne(parseProperty(term.property), parseValue(term));
				} else if(term.operator == Term.OPERATOR_GT) {
					operation = gt(parseProperty(term.property), parseValue(term));
				} else if(term.operator == Term.OPERATOR_GTE) {
					operation = gte(parseProperty(term.property), parseValue(term));
				} else if(term.operator == Term.OPERATOR_LT) {
					operation = lt(parseProperty(term.property), parseValue(term));
				} else if(term.operator == Term.OPERATOR_LTE) {
					operation = lte(parseProperty(term.property), parseValue(term));
				} else {
					throw new RecMeException(400, "Term operator '" + term.operator +"' does not exist.");
				}
				ands.add(operation);
			}
			ors.add(and(ands));
		}
		
		Bson query = or(ors);
		
		MongoCursor<Document> cursor = ObjectEngine.database.getCollection(type).find(query).skip(offset).limit(offset + bucketSize).iterator();
		
		try {
			Document doc = null;
		    while(cursor.hasNext()) {
		    	doc = cursor.next();
		    	doc = unmarshalDoc(doc);
		        results.add(RecMeJSON.mapper.readValue(doc.toJson(), outputClass));
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

	private static Object parseValue(Term term) {
		try {
			if(term.type == Term.TYPE_BOOLEAN) {
				return Boolean.parseBoolean(term.value);
			}
			if(term.type == Term.TYPE_FLOAT) {
				return Float.parseFloat(term.value);
			}
			if(term.type == Term.TYPE_INT) {
				return Integer.parseInt(term.value);
			}
		} catch(Exception e) {}
		return term.value;
	}
}
