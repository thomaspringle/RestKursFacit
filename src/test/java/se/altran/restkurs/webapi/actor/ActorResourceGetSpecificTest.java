package se.altran.restkurs.webapi.actor;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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

public class ActorResourceGetSpecificTest {

	private Server server;
	private List<Actor> actors;
	
	private String uuidPeterHaber;
	private IActorService actorService;
	
	@Before
	public void setUp() throws Exception {
	
		// Mock the ActorService with some test data
		actors = new ArrayList<>();
		ArrayList<String> peterHaberMovies = new ArrayList<String>();
		peterHaberMovies.add("Sunes Sommar");
		peterHaberMovies.add("Beck");
		
		ArrayList<String> sandraBullockMovies = new ArrayList<String>();
		sandraBullockMovies.add("Speed");
		sandraBullockMovies.add("28 Days");
		sandraBullockMovies.add("crash");
		sandraBullockMovies.add("Gravity");
		
		Actor sandraBullock = new Actor("Sandra", "Bullock", sandraBullockMovies);
		Actor peterHaber = new Actor("Peter", "Haber", peterHaberMovies);

		actors.add(sandraBullock);
		actors.add(peterHaber);
		uuidPeterHaber = peterHaber.getId();
		
		actorService = mock(IActorService.class);
		when(actorService.getActor(uuidPeterHaber)).thenReturn(peterHaber);
		// Start the server
		AbstractModule testModule = new DomainModule(actorService);
		server = AltranREST.startServer(8090, testModule);

	}
	
	// "GET /webapi/actors/+uuidPeterHaber" must return the Actor Peter Haber
	@Test
	public void testActors_GET_specificActorsExist() throws Exception {
		HttpHelper httpHelper = new HttpHelper("127.0.0.1", 8090);
		HttpGet httpGet = new HttpGet("/webapi/actors/"+uuidPeterHaber);

		// Execute the request and get a response
		HttpResponse httpResponse = httpHelper.executeMethod(httpGet);
		String responseData = HttpHelper.responseData(httpResponse);
		
		// Verify the returned data
		ActorBean peterHaber = new ObjectMapper().readValue(responseData, ActorBean.class);
		
		assertEquals("The correct first name must be set", "Peter", peterHaber.getFirstName());
		assertEquals("The correct last name must be set", "Haber", peterHaber.getLastName());
		assertEquals("The correct movies must be set", 2, peterHaber.getMovies().size());
		
		verify(actorService).getActor(uuidPeterHaber);
	}
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
