package se.altran.restkurs.actor;

import java.util.ArrayList;
import java.util.List;

import se.altran.restkurs.webapi.actor.ActorBean;

import com.google.inject.Singleton;

@Singleton
public class ActorService implements IActorService {

	private List<Actor> actors = new ArrayList<>();
	
	public ActorService() {
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
	}

	public List<Actor> getActors() {
		return new ArrayList<>(actors);
	}

	@Override
	public Actor getActor(String actorId) {

		for (Actor actor : actors) {
			if (actor.hasId(actorId)) {
				return actor;
			}
		}
		throw new RuntimeException("Actor not found!");
	}

	@Override
	public String createActor(ActorBean actorBean) {
		Actor actor = new Actor(actorBean.getFirstName(), actorBean.getLastName(), actorBean.getMovies());
		actors.add(actor);
		return actor.getId();
	}

	@Override
	public List<Actor> getActorsWithFirstName(String firstName) {
		List<Actor> result = new ArrayList<>();
		for (Actor actor : actors) {
			if (actor.hasFirstName(firstName)) {
				result.add(actor);
			}
		}
		return result;
	}

	@Override
	public List<Actor> getActorsWithPagination(int offset, int limit) {
		if (actors.size() < offset+limit) {
			if (actors.size() < offset) {
				return new ArrayList<>();
			}
		}
		return actors.subList(offset, offset+limit);
	}

	@Override
	public List<Actor> deleteActors(String userToken) {
		actors.clear();
		return actors;
	}
	
}
