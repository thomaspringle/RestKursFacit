package se.altran.restkurs.movie;

import java.util.List;

import se.altran.restkurs.webapi.movie.MovieBean;

public interface IMovieService {

	public List<Movie> getMovies();

	public Movie getMovie(String movieId);

	public String createMovie(MovieBean movieBean);

	public List<Movie> deleteAllMovies();

	public Movie deleteMovie(String movieId);

	public Movie updateMovie(MovieBean movieBean);

}