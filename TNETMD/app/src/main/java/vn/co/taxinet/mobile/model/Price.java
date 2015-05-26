package vn.co.taxinet.mobile.model;

import java.io.Serializable;

public class Price implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6203214508305136863L;

	private double openKM;
	private double openKMPrice;
	private double firstKM;
	private double firstPrice;
	private double nextKM;
	private double nextKMPrice;
	private double waitingPrice;

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

	public double getWaitingPrice() {
		return waitingPrice;
	}

	public void setWaitingPrice(double waitingPrice) {
		this.waitingPrice = waitingPrice;
	}

	@Override
	public String toString() {
		return "Price [openKM=" + openKM + ", openKMPrice=" + openKMPrice
				+ ", firstKM=" + firstKM + ", firstPrice=" + firstPrice
				+ ", nextKM=" + nextKM + ", nextKMPrice=" + nextKMPrice
				+ ", waitingPrice=" + waitingPrice + "]";
	}
	
	

}
