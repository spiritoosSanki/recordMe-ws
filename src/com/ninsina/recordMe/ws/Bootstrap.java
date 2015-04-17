package com.ninsina.recordMe.ws;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.logging.Logger;

import com.ninsina.recordMe.core.IndexEngine;
import com.ninsina.recordMe.core.ObjectEngine;

public class Bootstrap extends Application {
	Logger log = Logger.getLogger(Bootstrap.class);
	
	public Bootstrap(@Context ServletContext servletContext) throws FileNotFoundException, IOException {
		log.info("***************************************");
        log.info("***************************************");
        log.info("--- Init RecordMe Web Service");
        
        String pathName = servletContext.getRealPath("/WEB-INF/classes/config.properties");
        log.debug("String paths: " + pathName);
        Properties properties = new Properties();
        

    	properties.load(new FileInputStream(pathName));
    	ObjectEngine.init(
    			properties.getProperty("mongo_uris"), 
    			properties.getProperty("db_name")
    	);
    	IndexEngine.initIndexes(Long.parseLong(properties.getProperty("initIndexes")));
        
	}
}
