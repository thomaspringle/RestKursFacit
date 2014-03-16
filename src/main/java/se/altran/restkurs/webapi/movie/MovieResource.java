package se.altran.restkurs.webapi.movie;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.altran.restkurs.movie.IMovieService;
import se.altran.restkurs.movie.Movie;
import se.altran.restkurs.movie.MovieNotFoundException;
import se.altran.restkurs.webapi.actor.ActorResource;

import com.google.inject.Inject;

@Path("/movies")
public class MovieResource {

	
	@Inject
	private IMovieService movieService;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<MovieBean> movies() {
		// Anti corruption layer
		List<MovieBean> movieBeans = MovieBeanHelper.asMovieBeans(movieService.getMovies());
		return movieBeans;
	}


	@GET
	@Path("/{movieId}")
	@Produces(MediaType.APPLICATION_JSON)
	public MovieBean movie(@PathParam("movieId") String movieId) {
		Movie movie = movieService.getMovie(movieId);
		return movie.asMovieBean();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response movieObj(@Context UriInfo uriInfo, MovieBean movieBean) {

		String id = movieService.createMovie(movieBean);
		
		// Build URI to the created movie, and return it
		URI movieUri = uriInfo.getBaseUriBuilder().path(MovieResource.class).path(id).build();
		return Response.created(movieUri).build();
	}

	@PUT
	@Path("/{movieId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMovie(@PathParam("movieId") String movieId, MovieBean movieBean) {
		try {
			Movie movie = movieService.updateMovie(movieBean);
			return Response.ok(movie.asMovieBean()).build();
		} catch (MovieNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public List<MovieBean> deleteMovies() {
		List<Movie> movies = movieService.deleteAllMovies();
		return MovieBeanHelper.asMovieBeans(movies);
	}
	
	@DELETE
	@Path("/{movieId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMovie(@PathParam("movieId") String movieId) {
		try {
			Movie movie = movieService.deleteMovie(movieId);
			return Response.ok(movie.asMovieBean()).build();
		} catch (MovieNotFoundException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@Path("/actors")
	public Class<ActorResource> getActorResource() {
        return ActorResource.class;
    }
}
