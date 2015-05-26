package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class Journey  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5197493533818090361L;
	private String starting_point;
	private String stopping_point;

	public Journey() {
		super();
	}

	public Journey(String starting_point, String stopping_point) {
		super();
		this.starting_point = starting_point;
		this.stopping_point = stopping_point;
	}

	public String getStarting_point() {
		return starting_point;
	}

	public void setStarting_point(String starting_point) {
		this.starting_point = starting_point;
	}

	public String getStopping_point() {
		return stopping_point;
	}

	public void setStopping_point(String stopping_point) {
		this.stopping_point = stopping_point;
	}

}
