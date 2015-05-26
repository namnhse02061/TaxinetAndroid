package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class PromotionTripRiders implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 175058668051154379L;
	private PromotionTrip promotionTrip;
	private String fromCity;
	private String fromAddress;
	private String toCity;
	private String toAddress;
	private String status;
	private Integer numberOfSeats;
	private String startTime;
	private Rider rider;

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public Rider getRider() {
		return rider;
	}

	public void setRider(Rider rider) {
		this.rider = rider;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public PromotionTrip getPromotionTrip() {
		return promotionTrip;
	}

	public void setPromotionTrip(PromotionTrip promotionTrip) {
		this.promotionTrip = promotionTrip;
	}

}
