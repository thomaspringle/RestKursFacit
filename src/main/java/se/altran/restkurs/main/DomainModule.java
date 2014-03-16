package se.altran.restkurs.main;

import se.altran.restkurs.actor.ActorService;
import se.altran.restkurs.actor.IActorService;
import se.altran.restkurs.movie.IMovieService;
import se.altran.restkurs.movie.MovieService;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;


public class DomainModule extends AbstractModule {
	
	private IMovieService movieService;
	private IActorService actorService;
	
	public DomainModule(IMovieService movieService) {
		this.movieService = movieService;
		actorService = new ActorService();
	}
	public DomainModule(IActorService actorService) {
		this.actorService = actorService;
		movieService = new MovieService();
	}
	
	public DomainModule() {
		movieService = new MovieService();
		actorService = new ActorService();
	}
	
    @Override
    protected void configure() {
    	
    }    
    
    @Provides @Singleton
    private IMovieService provideIMovieService() {
    	return movieService;
    }
    
    @Provides @Singleton
    private IActorService provideIActorService() {
    	return actorService;
    }
}