package com.ninsina.recordMe.ws.users;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.logging.Logger;

import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.core.SecurityEngine;
import com.ninsina.recordMe.sdk.User;

@Path("/users")
public class UsersResource {
	
	Logger log = Logger.getLogger(UsersResource.class);
	private static UsersService usersService = new UsersService();
	
	@POST
	public Response login(String login, String password) {
		try {
			usersService.login(login, password);
			return null;
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
	
	@POST
	public Response create(@HeaderParam("sessionId") String sessionId, User user) {
		try {
			SecurityEngine.checkUserRight(sessionId, User.TYPE_ADMIN);
			usersService.create(sessionId, user);
			return Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
	
	@PUT
	@Path("/validate/{token}")
	public Response validate(@PathParam("token") String token) {
		try {
			usersService.validate(token);
			return Response.status(202).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
	
	@PUT
	public Response update(@HeaderParam("sessionId") String sessionId, User user) {
		try {
			SecurityEngine.checkUserRight(sessionId, User.TYPE_ADMIN, User.TYPE_USER); // check : type_user can only update itself
			return null;
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
	
	@GET
	public Response get() {
		
		return null;
	}
}
