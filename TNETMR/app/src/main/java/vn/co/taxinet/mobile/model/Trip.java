package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class Trip implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6731593080885053046L;

	private String tripId;
	private double startLatitude;
	private double startLongitude;
	private double stopLatitude;
	private double stopLongitude;
	private String fromAddress;
	private String toAddress;
	private String paymenMethod;
	private String status;
	private Price price;
	private String completionTime;
	private double distance;
	private double fee;
	private Driver driver;
	private String fromCity;
	private String toCity;

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public double getStartLatitude() {
		return startLatitude;
	}

	public void setStartLatitude(double startLatitude) {
		this.startLatitude = startLatitude;
	}

	public double getStartLongitude() {
		return startLongitude;
	}

	public void setStartLongitude(double startLongitude) {
		this.startLongitude = startLongitude;
	}

	public double getStopLatitude() {
		return stopLatitude;
	}

	public void setStopLatitude(double stopLatitude) {
		this.stopLatitude = stopLatitude;
	}

	public double getStopLongitude() {
		return stopLongitude;
	}

	public void setStopLongitude(double stopLongitude) {
		this.stopLongitude = stopLongitude;
	}

	public String getPaymenMethod() {
		return paymenMethod;
	}

	public void setPaymenMethod(String paymenMethod) {
		this.paymenMethod = paymenMethod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public String getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(String completionTime) {
		this.completionTime = completionTime;
	}

	public Trip() {
		super();
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public double getFee() {
		return fee;
	}

	public void setFee(double fee) {
		this.fee = fee;
	}

}
