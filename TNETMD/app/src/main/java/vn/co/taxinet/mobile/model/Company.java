package vn.co.taxinet.mobile.model;

public class Company implements Comparable<Company> {
	private int id;
	private String name;
	private String address;
	private String city;
	private String phone;
	private String postalCode;
	private String vatNumeber;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getVatNumeber() {
		return vatNumeber;
	}

	public void setVatNumeber(String vatNumeber) {
		this.vatNumeber = vatNumeber;
	}

	public Company(int id, String name, String address, String city,
			String phone, String postalCode, String vatNumeber) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.city = city;
		this.phone = phone;
		this.postalCode = postalCode;
		this.vatNumeber = vatNumeber;
	}

	public Company() {
		super();
	}

	@Override
	public int compareTo(Company company) {
		if (this.id != company.getId()) {
			return 0;
		}
		if (!this.address.equals(company.getAddress())) {
			return 0;
		}
		if (!this.city.equals(company.getCity())) {
			return 0;
		}
		if (!this.phone.equals(company.getPhone())) {
			return 0;
		}
		if (!this.postalCode.equals(company.getPostalCode())) {
			return 0;
		}
		if (!this.vatNumeber.equals(company.getVatNumeber())) {
			return 0;
		}
		if (!this.name.equals(company.getName())) {
			return 0;
		}

		return 1;
	}

}
