package vn.co.taxinet.mobile.model;

public class TaxiCompany {
	private String name;
	private String phone_number;

	public TaxiCompany() {
		super();
	}

	public TaxiCompany(String name, String phone_number) {
		super();
		this.name = name;
		this.phone_number = phone_number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

}
