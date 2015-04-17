package com.ninsina.recordMe.ws;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Path("/")
public class Resource {

	public Resource() {
	}
	
	/**
	 * Handle XHR+CORS handshake with the client
	 * @param requestMethod
	 * @param requestHeaders
	 * @return
	 */
	@javax.ws.rs.OPTIONS
	@Path("/{path:.*}")
	public javax.ws.rs.core.Response handleCORSRequest(
			@HeaderParam("Access-Control-Request-Method") final String requestMethod,
			@HeaderParam("Access-Control-Request-Headers") final String requestHeaders) {
		final javax.ws.rs.core.Response.ResponseBuilder retValue = javax.ws.rs.core.Response.ok();

		if (requestHeaders != null) retValue.header("Access-Control-Allow-Headers", requestHeaders);
		if (requestMethod != null) retValue.header("Access-Control-Allow-Methods", requestMethod);
		retValue.header("Access-Control-Allow-Origin", "*"); 
		retValue.header("Access-Control-Allow-Credentials", "true");

		return retValue.build();
	}

}
