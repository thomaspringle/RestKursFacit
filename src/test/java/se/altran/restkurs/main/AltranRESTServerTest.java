package se.altran.restkurs.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.eclipse.jetty.server.Server;
import org.junit.Test;

import se.altran.restkurs.webapi.HttpHelper;

import com.google.inject.AbstractModule;


public class AltranRESTServerTest {

	@Test
	public void serverTest_startServer() {
		
		try {
			AbstractModule guiceModule = new DomainModule();
			Server server = AltranREST.startServer(8090, guiceModule);

			// Verify that the server responds
			HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
			HttpGet httpGet = new HttpGet("/webapi");
			
			HttpResponse httpResponse = httpHelper.executeMethod(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			
			assertEquals("The server must return something to show that it is alive.", 404, statusCode);
			
			server.stop();
		} catch (Exception e) {
			
			e.printStackTrace();
			assertTrue("An exception must not be thrown.", false);
		}
	}
}
