package se.altran.restkurs.webapi.actor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.altran.restkurs.actor.IActorService;
import se.altran.restkurs.main.AltranREST;
import se.altran.restkurs.main.DomainModule;
import se.altran.restkurs.webapi.HttpHelper;

import com.google.inject.AbstractModule;

public class ActorResourcePostTest {

	private static final String ACTOR_JSON = "actor.json";
	private Server server;
	private String uuid = "actor_uuid";
	private IActorService actorService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the ActorService with some test data
		actorService = mock(IActorService.class);
		when(actorService.createActor(any(ActorBean.class))).thenReturn(uuid);

		// Start the server
		AbstractModule testModule = new DomainModule(actorService);
		server = AltranREST.startServer(8090, testModule);
	}
	
	// "POST /webapi/actors" must return Response with Status code 201.
	@Test
	public void testActors_POST_ReturnCreatedStatus() throws Exception {
		
		// Create POST command to /webapi/actors
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpPost httpPost = new HttpPost("/webapi/actors");
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted
		String data = getActorJsonFromFile();
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code 201 - Created", 201, statusCode);
		
		verify(actorService).createActor(any(ActorBean.class));
	}

	// "POST /webapi/actors" must return Response with Location header set to the created Actor. 
	// "Location: /webapi/actors/{new id}"
	@Test
	public void testMovies_POST_ReturnLocationToNewMovie() throws Exception {
		
		// Create POST command to /webapi/actors
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpPost httpPost = new HttpPost("/webapi/actors");
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted
		String data = getActorJsonFromFile();
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the Location header has been set with the movie just created
		String location = response.getHeaders("Location")[0].getValue();
		String newActorUri = "http://127.0.0.1:8090/webapi/actors/" + uuid;
		assertEquals("A link to the new actor must be returned.", newActorUri, location);
		verify(actorService).createActor(any(ActorBean.class));
	}
	
	// POSTing incorrect json data must return a Response with Status code 400
	@Test
	public void testMovies_POST_ReturnErrorStatus() throws Exception {
		
		// Create POST command to /webapi/actors
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpPost httpPost = new HttpPost("/webapi/actors");
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted
		String data = "{incorrect json message}";
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code: 400 - Bad Request", 400, statusCode);
	}
	
	private String getActorJsonFromFile() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(ACTOR_JSON)));
		 
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = r.readLine()) != null) {
			sb.append(line);
		}
		
		String data = sb.toString();
		return data;
	}
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
