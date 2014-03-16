package se.altran.restkurs.webapi.movie;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.altran.restkurs.main.AltranREST;
import se.altran.restkurs.main.DomainModule;
import se.altran.restkurs.movie.IMovieService;
import se.altran.restkurs.movie.Movie;
import se.altran.restkurs.movie.MovieNotFoundException;
import se.altran.restkurs.webapi.HttpHelper;

import com.google.inject.AbstractModule;

public class MovieResourceDeleteSpecificTest {
    	
	private ArrayList<Movie> movies;
	private Server server;
	private String uuidGravity;
	private IMovieService movieService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the MovieService with some test data
		Movie sunesSommar = new Movie("Sunes Sommar", 2012);
		Movie gravity = new Movie("Gravity", 2013);
		uuidGravity = gravity.getId();
		movies = new ArrayList<>();
		movies.add(sunesSommar);
		movies.add(gravity);

		movieService = mock(IMovieService.class);
		when(movieService.deleteMovie(uuidGravity)).thenReturn(gravity);
		when(movieService.deleteMovie("incorrectUUID")).thenThrow(new MovieNotFoundException("No movie with specified UUID found."));

		// Start the server
		AbstractModule testModule = new DomainModule(movieService);
		server = AltranREST.startServer(8090, testModule);
	}

	// DELETE must return 200 - OK if the request was successful
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7
	@Test
	public void testMovies_DELETE_correctStatusCode() throws Exception {
		
		// Build URI to specific movie and DELETE it
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		String uri = "/webapi/movies/" + uuidGravity; 
		HttpDelete httpDelete = new HttpDelete(uri);

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpDelete);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code 200 - OK", 200, statusCode);	
	}

	// DELETE must return 404 - Not Found if the movie does not exist
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7
	@Test
	public void testMovies_DELETE_notFoundStatusCode() throws Exception {
		
		// Build URI to movie that does not exist and DELETE it
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		String uri = "/webapi/movies/" + "incorrectUUID"; 
		HttpDelete httpDelete = new HttpDelete(uri);

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpDelete);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code 404 - Not Found", 404, statusCode);	
	}

	// DELETE must return 200 - OK with an entity describing the deleted item
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7
	@Test
	public void testMovies_DELETE_correctResponse() throws Exception {
		
		// Build URI to specific movie and DELETE it
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		String uri = "/webapi/movies/" + uuidGravity; 
		HttpDelete httpDelete = new HttpDelete(uri);

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpDelete);
		String responseData = HttpHelper.responseData(response);
		
		// Verify that the correct movie has been deleted
		MovieBean deletedMovie = new ObjectMapper().readValue(responseData, MovieBean.class);	
		assertEquals("Expected movie title Gravity", "Gravity", deletedMovie.getTitle());	
		verify(movieService).deleteMovie(uuidGravity);
	}
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
