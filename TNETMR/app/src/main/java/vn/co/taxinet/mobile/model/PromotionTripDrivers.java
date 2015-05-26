package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class PromotionTripDrivers implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 175058668051154379L;
	private PromotionTrip promotionTrip;
	private String toCity;
	private Driver driver;
	private String fromCity;
	private String status;
	private String fromAddress;
	private String toAddress;
	private Integer numberOfSeats;
	private String timeStart;

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}



	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(Integer numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	public String getTimeStart() {
		return timeStart;
	}

	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	public PromotionTrip getPromotionTrip() {
		return promotionTrip;
	}

	public void setPromotionTrip(PromotionTrip promotionTrip) {
		this.promotionTrip = promotionTrip;
	}

}
