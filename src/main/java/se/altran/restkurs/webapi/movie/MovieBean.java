package se.altran.restkurs.webapi.movie;

import javax.xml.bind.annotation.XmlElement;


public class MovieBean {
	
	private String id;
	private String title;
	private int year;
	
	MovieBean() {}
	
	public MovieBean(String id, String title, int year) {
		this.title = title;
		this.year = year;
		this.id = id;
	}

	@XmlElement
	public String getId() {
		return id;
	}
	
	@XmlElement
	public String getTitle() {
		return title;
	}

	@XmlElement
	public int getYear() {
		return year;
	}
}
