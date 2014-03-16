package se.altran.restkurs.movie;

import java.util.ArrayList;
import java.util.List;

import se.altran.restkurs.webapi.movie.MovieBean;

public class MovieService implements IMovieService {

	List<Movie> movies = new ArrayList<>();
	
	public MovieService() {
		movies.add(new Movie("Gravity", 2013));
	}
	
	public List<Movie> getMovies() {
		return new ArrayList<>(movies);
	}
	
	@Override
	public Movie getMovie(String movieId) {
		for (Movie movie : movies) {
			if (movie.hasId(movieId)) {
				return movie;
			}
		}
		throw new MovieNotFoundException("A movie with the specified UUID was not found.");
	}

	@Override
	public String createMovie(MovieBean movieBean) {
		Movie movie = new Movie(movieBean.getTitle(), movieBean.getYear());
		movies.add(movie);
		return movie.getId();
	}

	@Override
	public List<Movie> deleteAllMovies() {
		movies.clear();
		return new ArrayList<>(movies);
	}

	@Override
	public Movie deleteMovie(String movieId) {
		Movie movie = getMovie(movieId);
		movies.remove(movie);
		return movie;
	}

	@Override
	public Movie updateMovie(MovieBean movieBean) {
		Movie movie = getMovie(movieBean.getId());
		
		movie.updateTitle(movieBean.getTitle());
		movie.updateYear(movieBean.getYear());
		
		return movie;
	}
}
