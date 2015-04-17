package com.ninsina.recordMe.ws.users;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.logging.Logger;

import com.ninsina.recordMe.core.RecMeException;

@Path("/users")
public class UsersResource {
	
	Logger log = Logger.getLogger(UsersResource.class);
	private static UsersService usersService = new UsersService();
	
	@POST
	public Response login(String login, String password) {
		try {
			usersService.login(login, password);
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
		
		return null;
	}
	
	@POST
	public Response create() {
		
		return null;
	}
	
	@PUT
	public Response update() {
		
		return null;
	}
	
	@GET
	public Response get() {
		
		return null;
	}
}
