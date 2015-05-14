package com.ninsina.recordMe.ws.users;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ninsina.recordMe.core.RecMeException;
import com.ninsina.recordMe.sdk.User;

@Path("/users")
public class UsersResource {
	
	private Logger log = LoggerFactory.getLogger(UsersResource.class);
	private static UsersService usersService = new UsersService();
	
	@POST
	@Path("/login")
	@Consumes("application/json")
	@Produces("application/json")
	public Response login(@Context HttpServletRequest httpReq, Map<String, String> request) {
		long start = System.nanoTime();
		String login = request.get("login");
		String password = request.get("password");
		Response res = null;
		try {
			res = Response.status(200).entity(usersService.login(login, password)).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};;users;POST;{};{};{}", new Object[] {httpReq.getRemoteAddr(), res.getStatus(), System.nanoTime() - start, login + "," + password});
		return res;
	}
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, User user) {
		long start = System.nanoTime();
		Response res = null;
		try {
			usersService.create(sessionId, user);
			res = Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};users;POST;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, user});
		return res;
	}
	
	@PUT
	@Path("/validate/{token}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response validate(@Context HttpServletRequest httpReq, @PathParam("token") String token) {
		long start = System.nanoTime();
		Response res = null;
		try {
			usersService.validate(token);
			res = Response.status(202).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};;users;PUT;{};{};{}", new Object[] {httpReq.getRemoteAddr(), res.getStatus(), System.nanoTime() - start, token});
		return res;
	}
	
	@DELETE
	@Path("/{userId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response remove(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, @PathParam("userId") String userId) {
		long start = System.nanoTime();
		Response res = null;
		try {
			usersService.remove(sessionId, userId);
			res = Response.status(202).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};users;DELETE;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, userId});
		return res;
	}
	
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response update(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, User user) {
		long start = System.nanoTime();
		Response res = null;
		try {
			usersService.update(sessionId, user);
			res = Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};users;PUT;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, user});
		return res;
	}
	
	@GET
	@Path("{userId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response get(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, @PathParam("userId") String userId) {
		long start = System.nanoTime();
		Response res = null;
		try {
			res = Response.status(200).entity(usersService.get(sessionId, userId)).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};users;GET;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, userId});
		return res;
	}
}
