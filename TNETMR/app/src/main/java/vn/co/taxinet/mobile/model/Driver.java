package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class Driver implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8026349913914275141L;
	private String id;
	private String firstName;
	private String lastName;
	private String image;
	private Double longitude;
	private Double latitude;
	private double openKM;
	private double openKMPrice;
	private double firstKM;
	private double firstPrice;
	private double nextKM;
	private double nextKMPrice;
	private double watingPrice;
	private String email;
	private String phoneNumber;
	private String type;
	
	public void setType(String type) {
		this.type = type;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
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
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public double getOpenKM() {
		return openKM;
	}
	public void setOpenKM(double openKM) {
		this.openKM = openKM;
	}
	public double getOpenKMPrice() {
		return openKMPrice;
	}
	public void setOpenKMPrice(double openKMPrice) {
		this.openKMPrice = openKMPrice;
	}
	public double getFirstKM() {
		return firstKM;
	}
	public void setFirstKM(double firstKM) {
		this.firstKM = firstKM;
	}
	public double getFirstPrice() {
		return firstPrice;
	}
	public void setFirstPrice(double firstPrice) {
		this.firstPrice = firstPrice;
	}
	public double getNextKM() {
		return nextKM;
	}
	public void setNextKM(double nextKM) {
		this.nextKM = nextKM;
	}
	public double getNextKMPrice() {
		return nextKMPrice;
	}
	public void setNextKMPrice(double nextKMPrice) {
		this.nextKMPrice = nextKMPrice;
	}
	public double getWatingPrice() {
		return watingPrice;
	}
	public void setWatingPrice(double watingPrice) {
		this.watingPrice = watingPrice;
	}
	public String getType() {
		return type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Driver() {
		super();
	}


}
