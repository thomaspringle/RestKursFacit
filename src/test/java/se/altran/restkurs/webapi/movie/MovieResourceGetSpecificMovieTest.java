package se.altran.restkurs.webapi.movie;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.map.ObjectMapper;
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

public class MovieResourceGetSpecificMovieTest {
    
	
	private Server server;
	private String uuidGravity;
	private IMovieService movieService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the MovieService with some test data
		Movie gravity = new Movie("Gravity", 2013);
		uuidGravity = gravity.getId();
		
		movieService = mock(IMovieService.class);
		when(movieService.getMovie(uuidGravity)).thenReturn(gravity);
		
		// Start the server
		AbstractModule testModule = new DomainModule(movieService);
		server = AltranREST.startServer(8090, testModule);
	}
	
	@Test
	public void testMovies_GET_specificMovie() throws Exception {
		
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		String uri = "/webapi/movies/" + uuidGravity;
		HttpGet httpGet = new HttpGet(uri);
		
		// Execute the request and get a response
		HttpResponse httpResponse = httpHelper.executeMethod(httpGet);

		// Read the Movie response data as a JSON String
		String responseData = HttpHelper.responseData(httpResponse);
		MovieBean gravity = deserializeMovie(responseData);

		assertEquals("Gravity must have correct title", "Gravity", gravity.getTitle());
		assertEquals("Gravity must have correct year", 2013, gravity.getYear());
		
		verify(movieService).getMovie(uuidGravity);
	}
	
	protected MovieBean deserializeMovie(String jsonMovie) throws Exception {
		MovieBean movie = new ObjectMapper().readValue(jsonMovie, MovieBean.class);
		return movie;
	}
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
