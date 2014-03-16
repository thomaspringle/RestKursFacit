package se.altran.restkurs.webapi.actor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import se.altran.restkurs.actor.Actor;
import se.altran.restkurs.actor.IActorService;
import se.altran.restkurs.main.AltranREST;
import se.altran.restkurs.main.DomainModule;
import se.altran.restkurs.webapi.HttpHelper;

import com.google.inject.AbstractModule;

public class ActorResourceGetMovieLinksTest {

	private static final String ACTOR_JSON = "actor.json";
	
	private Server server;
	private Actor actor;
	private String uuid;
	private List<String> moviesIds;
	private IActorService actorService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the ActorService with some test data
		String actorsJSON = getActorJsonFromFile();
		ActorBean actorBean = deserializeActor(actorsJSON);
		actor = new Actor(actorBean.getFirstName(), actorBean.getLastName(), actorBean.getMovies());
		moviesIds = actorBean.getMovies();
		uuid = actor.getId();
		
		actorService = mock(IActorService.class);
		when(actorService.getActor(uuid)).thenReturn(actor);
		
		// Start the server
		AbstractModule testModule = new DomainModule(actorService);
		server = AltranREST.startServer(8090, testModule);

	}

	/**
	  "GET /webapi/actors/ + uuid + /movies" Must return a Response containing links to the Movies Resource:
	  [
	    {
	        "id": "35bc4f81-fc1e-467b-bdcc-8d18010aa346",
	        "uri": "/webapi/movies/35bc4f81-fc1e-467b-bdcc-8d18010aa346"
	    },
	    {
	        "id": "c94b8eb3-0c6d-4000-b226-2c77fcafb3d6",
	        "uri": "/webapi/movies/c94b8eb3-0c6d-4000-b226-2c77fcafb3d6"
	    }
	  ]		
	 */		
	@Test
	public void testActors_GET_movieLinks() throws Exception {
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpGet httpGet = new HttpGet("/webapi/actors/" + uuid + "/movies");

		// Execute the request and get a response
		HttpResponse httpResponse = httpHelper.executeMethod(httpGet);
		String responseData = HttpHelper.responseData(httpResponse);
		
		// Verify the returned data
		
		for (String movieId : moviesIds) {
			assertTrue("Link to the movie resource must be set, /movies/uuid", responseData.contains("/webapi/movies/" + movieId));	
		}
		
		verify(actorService).getActor(uuid);
	}
	

	private ActorBean deserializeActor(String jsonActor) throws JsonParseException, JsonMappingException, IOException {
		ActorBean actor = new ObjectMapper().readValue(jsonActor, ActorBean.class);
		return actor;
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
