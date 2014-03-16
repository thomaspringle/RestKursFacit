package se.altran.restkurs.webapi.actor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;

import se.altran.restkurs.actor.Actor;
import se.altran.restkurs.actor.IActorService;
import se.altran.restkurs.main.AltranREST;
import se.altran.restkurs.main.DomainModule;

import com.google.inject.AbstractModule;

public class ActorResourceTestTemplate {

	private Server server;
	private List<Actor> actors;
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
		
		actorService = mock(IActorService.class);
		when(actorService.getActors()).thenReturn(actors);

		// Start the server
		AbstractModule testModule = new DomainModule(actorService);
		server = AltranREST.startServer(8090, testModule);

	}
	
	
	@After
	public void tearDown() {
		try {
			server.stop();
		} catch (Exception ignore) { }
	}
}
