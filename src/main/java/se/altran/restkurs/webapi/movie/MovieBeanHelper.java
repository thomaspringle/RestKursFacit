package se.altran.restkurs.webapi.movie;

import java.util.ArrayList;
import java.util.List;

import se.altran.restkurs.movie.Movie;

public class MovieBeanHelper {

	public static List<MovieBean> asMovieBeans(List<Movie> movies) {
		List<MovieBean> movieBeans = new ArrayList<>(movies.size());
		for (Movie movie : movies) {
			movieBeans.add(movie.asMovieBean());
		}
		return movieBeans;
	}
}
