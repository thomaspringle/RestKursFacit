package se.altran.restkurs.webapi.movie;

import static org.junit.Assert.assertEquals;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.altran.restkurs.main.AltranREST;
import se.altran.restkurs.main.DomainModule;
import se.altran.restkurs.movie.IMovieService;
import se.altran.restkurs.webapi.HttpHelper;

import com.google.inject.AbstractModule;

public class MovieResourcePostTest {
    
	
	private Server server;
	private String uuid;
	private IMovieService movieService;
	
	@Before
	public void setUp() throws Exception {

		// Mock the MovieService with some test data
		uuid = UUID.randomUUID().toString();
		movieService = mock(IMovieService.class);
		when(movieService.createMovie(any(MovieBean.class))).thenReturn(uuid);
		
		// Start the server
		AbstractModule testModule = new DomainModule(movieService);
		server = AltranREST.startServer(8090, testModule);

	}
	
	@Test
	public void testMovies_POST_ReturnCreatedStatus() throws Exception {
		
		// Create POST command to /webapi/movies
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpPost httpPost = new HttpPost("/webapi/movies");
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted
		String data = "{\"title\": \"Gravity\", \"year\": 2013}";
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code 201 - Created", 201, statusCode);
		verify(movieService).createMovie(any(MovieBean.class));
	}

	@Test
	public void testMovies_POST_ReturnLocationToNewMovie() throws Exception {
		
		// Create POST command to /webapi/movies
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpPost httpPost = new HttpPost("/webapi/movies");
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted
		String data = "{\"title\": \"Gravity\", \"year\": 2013}";
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the Location header has been set with the movie just created
		String location = response.getHeaders("Location")[0].getValue();
		String newMovieUri = "http://127.0.0.1:8090/webapi/movies/" + uuid;
		assertEquals("A link to the new movie must be returned.", newMovieUri, location);
		
		verify(movieService).createMovie(any(MovieBean.class));
	}
	
	
	// POSTing incorrect JSON causes 400 - Bad Request
	@Test
	public void testMovies_POST_ReturnErrorStatus() throws Exception {
		
		// Create POST command to /webapi/movies
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpPost httpPost = new HttpPost("/webapi/movies");
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted - use invalid JSON to trigger an error 
		String data = "\"title\": \"Gravity\", \"year\": 2013";
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code: 400 - Bad Request", 400, statusCode);	
	}

	// You are not allowed to POST to an existing movie - use PUT instead
	@Test
	public void testMovies_POST_ExistingMovieNotAllowed() throws Exception {
		
		// Create POST command to /webapi/movies
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		String uri = "/webapi/movies/" + uuid;
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("Accept", "application/json");

		// Define data to be posted
		String data = String.format("{\"title\": \"Gravity\", \"year\": 2013, \"id\": \"%s\"}", uuid);
		httpPost.setEntity(new StringEntity(data, ContentType.create("application/json")));

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpPost);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Must not be possible to POST to existing movie, Expected Status Code: 405 - Method not allowed", 405, statusCode);	
	}
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
