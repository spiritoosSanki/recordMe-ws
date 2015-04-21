package com.ninsina.recordMe.ws.users;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	@Path("/login")
	@Consumes("application/json")
	@Produces("application/json")
	public Response login(Map<String, String> request) {
		long start = System.nanoTime();
		log.debug("login start;POST;0;0;;");
		String login = request.get("login");
		String password = request.get("password");
		Response res = null;
		try {
			res = Response.status(200).entity(usersService.login(login, password)).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
//		log.debug("login end;POST;{};{};{}", res.getStatus(), System.nanoTime() - start, login + "," + password);
		return res;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(@HeaderParam("sessionId") String sessionId, User user) {
		try {
			usersService.create(sessionId, user);
			return Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
	
	@PUT
	@Path("/validate/{token}")
	@Consumes("application/json")
	@Produces("application/json")
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
	@Consumes("application/json")
	@Produces("application/json")
	public Response update(@HeaderParam("sessionId") String sessionId, User user) {
		try {
			usersService.update(sessionId, user);
			return Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
	
	@GET
	@Path("{userId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response get(@HeaderParam("sessionId") String sessionId, @PathParam("userId") String userId) {
		try {
			return Response.status(200).entity(usersService.get(sessionId, userId)).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            return Response.status(e.status).entity(e.msg).build();
		}
	}
}
