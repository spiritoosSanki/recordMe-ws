package com.ninsina.recordMe.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class DocAutoMapper {
	private Object obj = null;
	private Document doc = null;
	
	public DocAutoMapper(Object object) {
		obj = object;
	}
	public DocAutoMapper(Document document) {
		doc = document;
	}
	
	public Document toDocument() throws IllegalArgumentException, IllegalAccessException {
		Document docz = new Document();
		
		Class<? extends Object> clazz = obj.getClass();
		for(Field field : clazz.getDeclaredFields()) {
			if(field.getType().isPrimitive() || field.getType().equals(Date.class)) {
				docz.put(field.getName(), field.get(obj));
			} else if(field.getType().equals(List.class)) {
				List<Document> docs = new ArrayList<Document>();
				for(Object o: (List<Object>) field.get(obj)) {
					docs.add(new DocAutoMapper(o).toDocument());
				}
				docz.put(field.getName(), docs);
			} else if(field.getType().equals(Map.class)) {
				docz.put(field.getName(), map2doc(field.get(obj), field.getType().getTypeParameters()[0].getClass()));
			} else {
				docz.put(field.getName(), new DocAutoMapper(field.get(obj)).toDocument());
			}
			docz.put(field.getName(), field.get(obj));
		}
		
		return docz;
	}
	
	private <M> Map<M, Object> map2doc(Object map, Class<M> clazz) throws IllegalArgumentException, IllegalAccessException {
		HashMap<M, Object> resMap = new HashMap<M, Object>();
		Map<M, Object> tmpMap = (Map<M, Object>) map;
		for(M key : tmpMap.keySet()) {
			resMap.put(key, new DocAutoMapper(tmpMap.get(key)).toDocument());
		}
		
		return resMap;
	}
	
	public <T> T toObject(Class<T> clazz) throws InstantiationException, IllegalAccessException {
		T objz = clazz.newInstance();
		for(Field field : clazz.getDeclaredFields()) {
			if(field.getType().isPrimitive() || field.getType().equals(Date.class)) {
				field.set(objz, doc.get(field.getName(), field.getType()));
			} else if(field.getType().equals(List.class)) {
				
			} else if(field.getType().equals(Map.class)) {
				
			} else {
				field.set(objz, new DocAutoMapper(doc.get(field.getName(), Document.class)).toObject(field.getType()));
			}
			
		}
		
		return objz;
	}
}
