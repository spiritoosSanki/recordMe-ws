package com.ninsina.recordMe.ws.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/test")
public class TestResource {
	
	@GET
	public Response test() {
		return Response.status(200).entity("aaaaaa").build();
	}
}
