package se.altran.restkurs.actor;

import java.util.List;

import se.altran.restkurs.webapi.actor.ActorBean;

public interface IActorService {

	public List<Actor> getActors();

	public Actor getActor(String actorId);

	public String createActor(ActorBean actorBean);

	public List<Actor> getActorsWithFirstName(String firstName);

	public List<Actor> getActorsWithPagination(int offset, int limit);

	public List<Actor> deleteActors(String userToken);

}