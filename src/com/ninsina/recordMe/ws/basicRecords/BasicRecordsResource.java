package com.ninsina.recordMe.ws.basicRecords;

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
import com.ninsina.recordMe.sdk.record.BasicRecord;

@Path("/basicRecords")
public class BasicRecordsResource {
	
	private Logger log = LoggerFactory.getLogger(BasicRecordsResource.class);
	private static BasicRecordsService basicRecordsService = new BasicRecordsService();
	
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Response create(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, BasicRecord record) {
		long start = System.nanoTime();
		Response res = null;
		try {
			basicRecordsService.create(sessionId, record);
			res = Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};basicRecords;POST;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, record});
		return res;
	}
	
	@DELETE
	@Path("/{recordId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response remove(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, @PathParam("recordId") String recordId) {
		long start = System.nanoTime();
		Response res = null;
		try {
			basicRecordsService.remove(sessionId, recordId);
			res = Response.status(202).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};basicRecords;DELETE;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, recordId});
		return res;
	}
	
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public Response update(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, BasicRecord  record) {
		long start = System.nanoTime();
		Response res = null;
		try {
			basicRecordsService.update(sessionId, record);
			res = Response.status(201).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};basicRecords;PUT;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, record});
		return res;
	}
	
	@GET
	@Path("{recordId}")
	@Consumes("application/json")
	@Produces("application/json")
	public Response get(@Context HttpServletRequest httpReq, @HeaderParam("sessionId") String sessionId, @PathParam("recordId") String recordId) {
		long start = System.nanoTime();
		Response res = null;
		try {
			res = Response.status(200).entity(basicRecordsService.get(sessionId, recordId)).build();
		} catch (RecMeException e) {
			log.debug("error: " + e.status + " msg: " + e.msg);
            res = Response.status(e.status).entity(e.msg).build();
		}
		log.info("{};{};basicRecords;GET;{};{};{}", new Object[] {httpReq.getRemoteAddr(), sessionId, res.getStatus(), System.nanoTime() - start, recordId});
		return res;
	}
	
	//TODO add a get with a list of ids
}
