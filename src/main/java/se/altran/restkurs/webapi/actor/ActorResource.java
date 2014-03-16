package se.altran.restkurs.webapi.actor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import se.altran.restkurs.actor.Actor;
import se.altran.restkurs.actor.IActorService;
import se.altran.restkurs.webapi.movie.MovieResource;

import com.google.inject.Inject;

@Path("/actors")
public class ActorResource {

	@Inject
	private IActorService actorService;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActors(	@QueryParam("firstName") String firstName, 
								@DefaultValue("-1") @QueryParam("offset") int offset, 
								@DefaultValue("-1") @QueryParam("limit") int limit) {
		
		List<Actor> actors;
		
		if (firstName != null && !firstName.isEmpty()) {
			actors = actorService.getActorsWithFirstName(firstName);
		} else if (offset != -1 && limit != -1) {
			actors = actorService.getActorsWithPagination(offset, limit);
		} else {
			actors = actorService.getActors();
		}
		
		List<ActorBean> actorBeans = ActorBeanHelper.asActorBeans(actors);
		return Response.ok(actorBeans).build();
	}
	
	
	@GET
	@Path("/{actorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ActorBean getActor(@PathParam("actorId") String actorId) {
		Actor actor = actorService.getActor(actorId);
		return actor.asActorBean();
	}
	
	@GET
	@Path("/{actorId}/movies")
	@Produces(MediaType.APPLICATION_JSON)
	public List<MovieLinkBean> getMoviesForActor(@Context UriInfo uriInfo, @PathParam("actorId") String actorId) {
		Actor actor = actorService.getActor(actorId);
		ActorBean actorBean = actor.asActorBean();
		List<MovieLinkBean> movieLinks = createMovieLinks(uriInfo, actorBean);
		return movieLinks;
	}


	private List<MovieLinkBean> createMovieLinks(UriInfo uriInfo, ActorBean actorBean) {
		List<String> movieIds = actorBean.getMovies();
		List<MovieLinkBean> movieLinks = new ArrayList<>();
		for (String movieId : movieIds) {
			URI movieUri = uriInfo.getBaseUriBuilder().path(MovieResource.class).path(movieId).build();
			movieLinks.add(new MovieLinkBean(movieId, movieUri.getPath()));
		}
		return movieLinks;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addMovie(@Context UriInfo uriInfo, ActorBean actorBean) {
		String id = actorService.createActor(actorBean);
		
		// Build URI to the created movie, and return it
		URI actorUri = uriInfo.getBaseUriBuilder().path(ActorResource.class).path(id).build();
		return Response.created(actorUri).build();
	}
	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteMovies(@HeaderParam("Authorization") String userToken) {
		List<Actor> actors = actorService.deleteActors(userToken);
		List<ActorBean> actorBeans = ActorBeanHelper.asActorBeans(actors);
		return Response.ok(actorBeans).build();
	}
}
