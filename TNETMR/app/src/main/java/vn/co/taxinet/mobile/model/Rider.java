package vn.co.taxinet.mobile.model;

public class Rider {
	private String id;
	private String firstname;
	private String lastname;
	private String image;
	private String email;
	private String phone;
	private double home_lat;
	private double home_lng;
	private String home_detail;
	private double work_lat;
	private double work_lng;
	private String work_detail;
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public double getHome_lat() {
		return home_lat;
	}

	public void setHome_lat(double home_lat) {
		this.home_lat = home_lat;
	}

	public double getHome_lng() {
		return home_lng;
	}

	public void setHome_lng(double home_lng) {
		this.home_lng = home_lng;
	}

	public String getHome_detail() {
		return home_detail;
	}

	public void setHome_detail(String home_detail) {
		this.home_detail = home_detail;
	}

	public double getWork_lat() {
		return work_lat;
	}

	public void setWork_lat(double work_lat) {
		this.work_lat = work_lat;
	}

	public double getWork_lng() {
		return work_lng;
	}

	public void setWork_lng(double work_lng) {
		this.work_lng = work_lng;
	}

	public String getWork_detail() {
		return work_detail;
	}

	public void setWork_detail(String work_detail) {
		this.work_detail = work_detail;
	}

	public Rider(String id, String firstname, String lastname, String image,
			String email, String phone, double home_lat, double home_lng,
			String home_detail, double work_lat, double work_lng,
			String work_detail, String type) {
		super();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.image = image;
		this.email = email;
		this.phone = phone;
		this.home_lat = home_lat;
		this.home_lng = home_lng;
		this.home_detail = home_detail;
		this.work_lat = work_lat;
		this.work_lng = work_lng;
		this.work_detail = work_detail;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Rider() {
		super();
		// TODO Auto-generated constructor stub
	}

}
