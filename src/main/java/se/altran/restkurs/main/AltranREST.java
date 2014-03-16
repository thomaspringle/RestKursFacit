package se.altran.restkurs.main;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

public class AltranREST {
	
	private static final int PORT = 8080;
	private static final String CONTEXT_PATH = "/webapi";
	
	private static final Logger LOG = LoggerFactory.getLogger(AltranREST.class);
	
	public static void main(String[] args) throws Exception {
		DomainModule guiceModule = new DomainModule();
		
        final Server server = startServer(PORT, guiceModule);
        LOG.info("Jetty server app started with WADL available at {}application.wadl", CONTEXT_PATH);
        LOG.info("Server successfully started with context root {} and port {}", CONTEXT_PATH, PORT);
        try {
        	server.join();
        } catch (Exception e) {
        	server.stop();
        	e.printStackTrace();
        }
    }
	
    public static Server startServer(int port, AbstractModule domainModule) throws Exception {
    	LOG.info("Starting server with context root {} and port {}", CONTEXT_PATH, port);
    	setJettyLogLvl();
    	
    	Injector injector = createGuiceInjector(domainModule);
		
    	ServletContextHandler handler = createServletContextHandler(injector);
        
        Server server = new Server(port);
        server.setHandler(handler);
    	server.start();
    	return server;
    }

	private static Injector createGuiceInjector(final AbstractModule domainModule) {
		AbstractModule abstractModule = new AbstractModule() {
            @Override
            protected void configure() {
                binder().requireExplicitBindings();
				install(new RESTServletModule());
                bind(GuiceFilter.class);
            }
        };
        
		Injector injector = Guice.createInjector(abstractModule, domainModule);
		return injector;
	}

	private static ServletContextHandler createServletContextHandler(Injector injector) {
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    	handler.setContextPath(CONTEXT_PATH);

    	FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
        handler.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));
		return handler;
	}

    private static void setJettyLogLvl() {
        final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("org.eclipse.jetty");
        if (!(logger instanceof ch.qos.logback.classic.Logger)) {
            return;
        }
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        logbackLogger.setLevel(ch.qos.logback.classic.Level.WARN);		
	}

}
