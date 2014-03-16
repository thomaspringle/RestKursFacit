package se.altran.restkurs.webapi.actor;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class ActorBean {

	private String id;
	private String firstName;
	private String lastName;
	private List<String> movies;
	
	ActorBean() {}

	public ActorBean(String id, String firstName, String lastName, List<String> movies) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.movies = new ArrayList<>(movies);
	}
	

	@XmlElement
	public String getId() {
		return id;
	}
	
	@XmlElement
	public String getFirstName() {
		return firstName;
	}
	
	@XmlElement
	public String getLastName() {
		return lastName;
	}
	
	@XmlElement
	public List<String> getMovies() {
		return movies;
	}
}
