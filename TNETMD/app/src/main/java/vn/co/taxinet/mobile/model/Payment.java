package vn.co.taxinet.mobile.model;

public class Payment {
	private int id;
	private String cardNumber;
	private String cvv;
	private String type;
	private String expiredMonth;
	private String expiredYear;
	private String bankName;
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExpiredMonth() {
		return expiredMonth;
	}

	public void setExpiredMonth(String expiredMonth) {
		this.expiredMonth = expiredMonth;
	}

	public String getExpiredYear() {
		return expiredYear;
	}

	public void setExpiredYear(String expiredYear) {
		this.expiredYear = expiredYear;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


}
