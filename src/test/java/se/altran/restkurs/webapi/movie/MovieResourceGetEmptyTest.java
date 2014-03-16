package se.altran.restkurs.webapi.movie;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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

public class MovieResourceGetEmptyTest {
    	
	private ArrayList<Movie> movies;
	private Server server;
	private IMovieService movieService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the MovieService with test data
		movieService = mock(IMovieService.class);
		movies = new ArrayList<>();
		when(movieService.getMovies()).thenReturn(movies);

		// Start the server
		AbstractModule testModule = new DomainModule(movieService);
		server = AltranREST.startServer(8090, testModule);

	}
	
	@Test
	public void testMovies_GET_moviesAreEmpty() throws Exception {

		// Read the Movies resource as a JSON String
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpGet httpGet = new HttpGet("/webapi/movies");
		
		// Execute the request and get a response
		HttpResponse httpResponse = httpHelper.executeMethod(httpGet);
		String responseData = HttpHelper.responseData(httpResponse);
		
		// Verify the returned data
		List<MovieBean> parsedMovies = deserializeMovies(responseData);
		assertEquals("All movies were not found.", movies.size(), parsedMovies.size());
		verify(movieService).getMovies();
	}
	
	protected List<MovieBean> deserializeMovies(String jsonMovies) throws Exception {
		List<MovieBean> movies = new ObjectMapper().readValue(jsonMovies, new TypeReference<List<MovieBean>>(){});
		return movies;
	}
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
