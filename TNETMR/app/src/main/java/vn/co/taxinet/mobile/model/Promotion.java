package vn.co.taxinet.mobile.model;

public class Promotion {
	public String imageUri;
	public String promotion_content;

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}

	public String getPromotion_content() {
		return promotion_content;
	}

	public void setPromotion_content(String promotion_content) {
		this.promotion_content = promotion_content;
	}

	public Promotion(String imageUri, String promotion_content) {
		super();
		this.imageUri = imageUri;
		this.promotion_content = promotion_content;
	}

	public Promotion() {
		super();
	}

}
