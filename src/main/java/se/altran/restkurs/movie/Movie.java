package se.altran.restkurs.movie;

import java.util.UUID;

import se.altran.restkurs.webapi.movie.MovieBean;

public class Movie {

	private final String id;
	private String title;
	private int year;
	
	public Movie(String title, int year) {
		this.title = title;
		this.year = year;
		id = UUID.randomUUID().toString();
	}


	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateYear(int year) {
		this.year = year;
	}
	
	public MovieBean asMovieBean() {
		return new MovieBean(id, title, year);
	}
	
	public boolean hasId(String movieId) {
		return this.id.equals(movieId);
	}
	
	public String getId() {
		return id;
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
		Movie other = (Movie) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
