package se.altran.restkurs.actor;

import java.util.List;
import java.util.UUID;

import se.altran.restkurs.webapi.actor.ActorBean;

public class Actor {

	private final String id;
	private String firstName;
	private String lastName;
	private List<String> movies;
	
	public Actor(String firstName, String lastName, List<String> movies) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.movies = movies;
		
		id = UUID.randomUUID().toString();
	}
	
	
	public ActorBean asActorBean() {
		return new ActorBean(id, firstName, lastName, movies);
	}

	public String getId() {
		return id;
	}

	public boolean hasId(String actorId) {
		return this.id.equals(actorId);
	}

	public boolean hasFirstName(String firstName) {
		return this.firstName.equals(firstName);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
