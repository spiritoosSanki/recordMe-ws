package com.ninsina.recordMe.sdk;

import java.util.HashMap;
import java.util.Map;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.params.ConnManagerParams;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRoute;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRouteBean;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpRequestRetryHandler;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.sdk.record.BasicRecord;

@SuppressWarnings("deprecation")
public class RecordMe {
	private DefaultHttpClient client;
	public String webServiceURL;
	public boolean debug = false;
	private final int BUFFER_SIZE = 8192;
	
	private org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();
	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'";
	private static final String DATE_FORMAT_WITH_SECONDS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final String DATE_FORMAT_WITH_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public Users Users = new Users();
	public BasicRecords BasicRecords = new BasicRecords();
	
	

	public RecordMe(String webServiceURL) {
		this(webServiceURL, false, null, 0, null, null);
	}
	
	public RecordMe(String webServiceURL, boolean debug, String proxyHost, int proxyPort, String proxyUsername,
			String proxyPassword) {
		this.webServiceURL = webServiceURL;
		this.debug = debug;
		if (debug)
			System.out.println("setting recordMe-ws url: " + webServiceURL);


		HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        //HttpProtocolParams.setUseExpectContinue(params, true);
        
        // timeouts
        HttpConnectionParams.setStaleCheckingEnabled(params, false);
        HttpConnectionParams.setConnectionTimeout(params, 15 * 1000);
        HttpConnectionParams.setSoTimeout(params, 15 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, BUFFER_SIZE);

        ConnPerRoute connPerRoute = new ConnPerRouteBean(128);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        ConnManagerParams.setMaxTotalConnections(params, 1000);

		final DefaultHttpClient tmpClient = new DefaultHttpClient();
        ClientConnectionManager mgr = tmpClient.getConnectionManager();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
        
//		client = new org.apache.http.impl.client.DefaultHttpClient(params);
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, true));
        
        if (proxyHost != null) {
			if (proxyUsername != null) {
	        	
				if (debug) System.out.println("setting proxy credentials: " + proxyUsername + ":" + proxyPassword);
        	
				client.getCredentialsProvider().setCredentials(
	        	    new AuthScope(proxyHost, proxyPort),
	        	    new UsernamePasswordCredentials(proxyUsername, proxyPassword));

			}
        	
        	if (debug) System.out.println("setting proxy location: " + proxyHost + ":" + proxyPort);
			
        	org.apache.http.HttpHost proxy = new org.apache.http.HttpHost(proxyHost, proxyPort);
        	client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

        }
        
        if (debug)
			System.out.println("request manager ready");
	}
	
	
	/**
	 * Utility method to convert a java.util.Date into a string following ISO-8601 standard: yyyy-MM-dd'T'HH:mm'Z'
	 * 
	 * @param date
	 * @return
	 */
	public static String getIso8601UTCDateString(java.util.Date date) {
		if (date == null) {
			date = new java.util.Date();
		}
		java.text.DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT);
	    df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
	    return df.format(date);
	}
	
	/**
	 * Utility method to convert a java.util.Date into a string following ISO-8601 standard: yyyy-MM-dd'T'HH:mm:ss'Z'
	 * 
	 * @param date
	 * @return
	 */
	public static String getIso8601UTCDateStringWithSeconds(java.util.Date date) {
		if (date == null) {
			date = new java.util.Date();
		}
		java.text.DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT_WITH_SECONDS);
	    df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
	    return df.format(date);
	}
	
	/**
	 * Utility method to convert a java.util.Date into a string following ISO-8601 standard: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * 
	 * @param date
	 * @return
	 */
	public static String getIso8601UTCDateStringWithMilliSeconds(java.util.Date date) {
		if (date == null) {
			date = new java.util.Date();
		}
		java.text.DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT_WITH_MILLISECONDS);
	    df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
	    return df.format(date);
	}

	
	/**
	 * Utility method to convert a string following ISO-8601 standard: yyyy-MM-dd'T'HH:mm'Z' or yyyy-MM-dd'T'HH:mm:ss'Z' or yyyy-MM-dd'T'HH:mm:ss.SSS'Z' into a java.util.Date 
	 * 
	 * @param str
	 * @return
	 */
	public static java.util.Date getDateFromIso8601UTCString(String str) throws Exception {
		try {
			java.text.DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT);
			df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
			return df.parse(str);
		} catch(Exception e) {
		}
		
		try {
			java.text.DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT_WITH_SECONDS);
			df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
			return df.parse(str);
		} catch (Exception e) {
		}	
		
		java.text.DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT_WITH_MILLISECONDS);
		df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
		return df.parse(str);
	}
	
	
	public HttpClient getClient() {
		return client;
	}

	private String jsonify(Object obj) throws RecMeException {
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			if(debug) e.printStackTrace();
			throw new RecMeException();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T decodeContent(String json, Class<T> outputClass) throws RecMeException {
		
		T resp = null;
		if(outputClass.equals(String.class)) {
			return (T) json;
		}
		try {
//			org.codehaus.jackson.type.JavaType javaType = mapper.getTypeFactory().constructParametricType(
//					com.oym.links.sdk.LinksResponse.class, outputClass);
			resp = mapper.readValue(json, outputClass);
		} catch (Exception ex) {
		}
		if (resp == null) {
			throw new RecMeException(500, json);
		}
		return resp;
	}


	private String post(String path, String sessionId, String jsonObject) throws RecMeException {

		if (debug)
			System.out.println("calling recordMe-ws :" + (webServiceURL + path) + " with sessionId:" + sessionId
					+ " and json object:" + jsonObject);

		// POST
		try {
			HttpPost post = new HttpPost(webServiceURL + path);
			if(jsonObject != null) {
				post.setEntity(new StringEntity(jsonObject, "UTF-8"));
			}
			//post.getParams().setCookiePolicy(org.apache.http.client.params.CookiePolicy.IGNORE_COOKIES);
	        post.addHeader("Content-Type", "application/json; charset=UTF-8");
			post.addHeader("sessionId", sessionId);

			// get result
			return request(post);

		} catch (RecMeException e) {
			if (debug)
				System.out.println("an error occured: " + e.getMessage());
			throw e;
		} catch (Exception ex) {
			if (debug)
				System.out.println("an error occured: " + ex.getMessage());
			throw new RecMeException(500, ex.getMessage());
		} finally {
			//post.releaseConnection();
		}

	}

	private String put(String path, String sessionId, String jsonObject) throws RecMeException {

		if (debug)
			System.out.println("calling recordMe-ws :" + (webServiceURL + path) + " with sessionId:" + sessionId
					+ " and json object:" + jsonObject);

		// POST
		try {
			HttpPut put = new HttpPut(webServiceURL + path);
			if(jsonObject != null) {
				put.setEntity(new StringEntity(jsonObject, "UTF-8"));
			}
			//post.getParams().setCookiePolicy(org.apache.http.client.params.CookiePolicy.IGNORE_COOKIES);
	        put.addHeader("Content-Type", "application/json; charset=UTF-8");
			put.addHeader("sessionId", sessionId);

			// get result
			return request(put);

		} catch (RecMeException e) {
			if (debug)
				System.out.println("an error occured: " + e.getMessage());
			throw e;
		} catch (Exception ex) {
			if (debug)
				System.out.println("an error occured: " + ex.getMessage());
			throw new RecMeException(500, ex.getMessage());
		} finally {
			//post.releaseConnection();
		}

	}
	
	private String getReq(String path, String sessionId) throws RecMeException {

		if (debug)
			System.out.println("calling recordMe-ws :" + (webServiceURL + path) + " with sessionId:" + sessionId);

		HttpGet get = new HttpGet(webServiceURL + path);
		//post.getParams().setCookiePolicy(org.apache.http.client.params.CookiePolicy.IGNORE_COOKIES);
		get.addHeader("Content-Type", "application/json; charset=UTF-8");
		get.addHeader("sessionId", sessionId);

		// get result
		return request(get);
	}
	
	private String delete(String path, String sessionId, String jsonObject) throws RecMeException {

		if (debug)
			System.out.println("calling recordMe-ws :" + (webServiceURL + path) + " with sessionId:" + sessionId
					+ " and json object:" + jsonObject);

		HttpDelete del = new HttpDelete(webServiceURL + path);
		//post.getParams().setCookiePolicy(org.apache.http.client.params.CookiePolicy.IGNORE_COOKIES);
		del.addHeader("Content-Type", "application/json; charset=UTF-8");
		del.addHeader("sessionId", sessionId);

		// get result
		return request(del);
	}
	
	private String request(HttpRequestBase req) throws RecMeException {
		try {
			HttpResponse resp = client.execute(req);
			int statusCode = resp.getStatusLine().getStatusCode();
			final byte[] data = EntityUtils.toByteArray(resp.getEntity());
    		String content = new String(data, "UTF-8");
			if((statusCode >= 400) && (statusCode < 600)) {
	    		throw new RecMeException(statusCode, content);
			} else {
				if (debug)
					System.out.println("response content: " + content);
				return content;
			}
		} catch(RecMeException e) {
			throw e;
		} catch (Exception ex) {
			if (debug)
				System.out.println("an error occured: " + ex.getMessage());
			throw new RecMeException();
		} finally {
			try {
				req.releaseConnection();
			} catch (Exception ex) {
				if (debug)
					System.out.println("error : " + ex);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public class Users {

		public String login(String login, String password) throws RecMeException {
			Map<String, String> req = new HashMap<String, String>();
			req.put("login", login);
			req.put("password", password);
			String content = post("/users/login", null, jsonify(req));
			return decodeContent(content, String.class);
		}
		
		public void create(String sessionId, User user) throws RecMeException {
			post("/users", sessionId, jsonify(user));
		}
		
		public void update(String sessionId, User user) throws RecMeException {
			put("/users", sessionId, jsonify(user));
		}
		
		public void validate(String sessionId, String token) throws RecMeException {
			put("/users/validate/" + token, sessionId, null);
		}
		
		public void remove(String sessionId, String id) throws RecMeException {
			delete("/users/" + id, sessionId, null);
		}
		
		/**
		 * Get a user by his ID.
		 * Normal user can get only itself. So easier to use {@link #get(String)}
		 * */
		public User get(String sessionId, String userId) throws RecMeException {
			String content = getReq("/users/" + userId, sessionId);
			return decodeContent(content, User.class);
		}
		
		/**
		 * Get connected user.
		 * */
		public User get(String sessionId) throws RecMeException {
			String content = getReq("/users/null", sessionId);
			return decodeContent(content, User.class);
		}
	}
	
	
	public class BasicRecords {

		public void create(String sessionId, BasicRecord basicRecord) throws RecMeException {
			post("/basicRecords", sessionId, jsonify(basicRecord));
		}
		
		public void update(String sessionId, BasicRecord basicRecord) throws RecMeException {
			put("/basicRecords", sessionId, jsonify(basicRecord));
		}
		
		public void remove(String sessionId, String id) throws RecMeException {
			delete("/basicRecords/" + id, sessionId, null);
		}
		
		public User get(String sessionId, String id) throws RecMeException {
			String content = getReq("/basicRecords/" + id, sessionId);
			return decodeContent(content, User.class);
		}
		
	}
	
}
