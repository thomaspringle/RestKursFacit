package se.altran.restkurs.webapi.actor;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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

public class ActorResourceGetPaginationTest {

	private static final String ACTORS_JSON = "actors_pagination.json";
	
	private Server server;
	private List<Actor> actors;
	private IActorService actorService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the ActorService with some test data
		String actorsJSON = getActorsJsonFromFile();
		List<ActorBean> deserializedActors = deserializeActors(actorsJSON);
		actors = createActors(deserializedActors);
		
		actorService = mock(IActorService.class);
		int offset = 10;
		int limit = 5;
		when(actorService.getActorsWithPagination(offset, limit)).thenReturn(actors);
		
		// Start the server
		AbstractModule testModule = new DomainModule(actorService);
		server = AltranREST.startServer(8090, testModule);

	}

	// "GET /actors?offset=10&limit=5" must return 5 Actors	
	@Test
	public void testActors_GET_filteredActors() throws Exception {
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpGet httpGet = new HttpGet("/webapi/actors?offset=10&limit=5");

		// Execute the request and get a response
		HttpResponse httpResponse = httpHelper.executeMethod(httpGet);
		String responseData = HttpHelper.responseData(httpResponse);
		
		// Verify the returned data
		List<ActorBean> parsedActors = deserializeActors(responseData);

		assertEquals("Five Actors must be returned", 5, parsedActors.size());
		
		verify(actorService).getActorsWithPagination(10, 5);
	}
	

	/**
	 * Create Actor-objects from ActorBean-objects
	 * @param actorBeans List of ActorBeans to be converted
	 * @return List of Actors
	 */
	private List<Actor> createActors(List<ActorBean> actorBeans) {
		List<Actor> result = new ArrayList<>();
		for (ActorBean actorBean : actorBeans) {
			result.add(new Actor(actorBean.getFirstName(), actorBean.getLastName(), actorBean.getMovies()));
		}
		return result;
	}

	/**
	 * De-serialize JSON-data to ActorBean-objects 
	 * @param jsonActors
	 * @return
	 */
	private List<ActorBean> deserializeActors(String jsonActors) throws JsonParseException, JsonMappingException, IOException {
		List<ActorBean> actors = new ObjectMapper().readValue(jsonActors, new TypeReference<List<ActorBean>>(){});
		return actors;
	}

	/**
	 * Read JSON-data from actors.json
	 * @return actors.json as String
	 * @throws IOException If file not found
	 */
	private String getActorsJsonFromFile() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(ACTORS_JSON)));
		 
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
