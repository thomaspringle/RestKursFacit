package se.altran.restkurs.webapi.actor;

import javax.xml.bind.annotation.XmlElement;

public class MovieLinkBean {

	private String id;
	private String uri;

	MovieLinkBean(){};
	
	public MovieLinkBean(String id, String uri) {
		this.id = id;
		this.uri = uri;
	}

	@XmlElement
	public String getId() {
		return id;
	}

	@XmlElement
	public String getUri() {
		return uri;
	}
	
}
