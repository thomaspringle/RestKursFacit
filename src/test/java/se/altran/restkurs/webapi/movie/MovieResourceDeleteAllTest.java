package se.altran.restkurs.webapi.movie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.altran.restkurs.main.AltranREST;
import se.altran.restkurs.main.DomainModule;
import se.altran.restkurs.movie.IMovieService;
import se.altran.restkurs.movie.Movie;
import se.altran.restkurs.webapi.HttpHelper;

import com.google.inject.AbstractModule;

public class MovieResourceDeleteAllTest {
    	
	private ArrayList<Movie> movies;
	private Server server;
	private IMovieService movieService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the MovieService with some test data
		Movie sunesSommar = new Movie("Sunes Sommar", 2012);
		Movie gravity = new Movie("Gravity", 2013);
		movies = new ArrayList<>();
		movies.add(sunesSommar);
		movies.add(gravity);
		
		movieService = mock(IMovieService.class);
		when(movieService.getMovies()).thenReturn(movies);

		// Start the server
		AbstractModule testModule = new DomainModule(movieService);
		server = AltranREST.startServer(8090, testModule);

	}

	// DELETE must return 200 - OK if the request was successful
	// http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7
	@Test
	public void testMovies_DELETE_correctStatusCode() throws Exception {
		

		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpDelete httpDelete = new HttpDelete("/webapi/movies");

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpDelete);
		
		// Verify that the correct status code has been set
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals("Expected Status Code 200 - OK", 200, statusCode);	

	}
	
	@Test
	public void testMovies_DELETE_returnEmptyMovies() throws Exception {
		
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpDelete httpDelete = new HttpDelete("/webapi/movies");

		// Execute method and receive response
		HttpResponse response = httpHelper.executeMethod(httpDelete);
		String responseData = HttpHelper.responseData(response);
		
		// Verify that the movie list is empty
		List<MovieBean> movies = new ObjectMapper().readValue(responseData, new TypeReference<List<MovieBean>>(){});
		assertTrue("Movies are empty after deletion", movies.isEmpty());
		verify(movieService).deleteAllMovies();
	}

	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
