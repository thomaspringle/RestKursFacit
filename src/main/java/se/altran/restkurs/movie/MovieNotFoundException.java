package se.altran.restkurs.movie;

public class MovieNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5029557082643433621L;

	public MovieNotFoundException(String message) {
		super(message);
	}


}
