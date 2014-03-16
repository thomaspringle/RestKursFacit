package se.altran.restkurs.webapi;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Provider
@Singleton  
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {  
  
    static final Logger LOG = LoggerFactory.getLogger(SecurityExceptionMapper.class);
      
    @Override  
    public Response toResponse(SecurityException e) {  
        LOG.error("Security exception.", e);  
        return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();  
    }  
  
}  