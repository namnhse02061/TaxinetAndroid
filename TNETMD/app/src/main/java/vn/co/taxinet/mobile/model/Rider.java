package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class Rider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8937723639356653815L;
	private String id;
	private String firstName;
	private String lastName;
	private String image;
	private String phone;
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Rider() {
		super();
	}



}
