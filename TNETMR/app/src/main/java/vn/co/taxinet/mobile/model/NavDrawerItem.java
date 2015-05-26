package vn.co.taxinet.mobile.model;

public class NavDrawerItem {

	private String title;
	private int icon;
	private String count = "0";
	// boolean to set visiblity of the counter
	private boolean isCounterVisible = false;
	private int type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public boolean isCounterVisible() {
		return isCounterVisible;
	}

	public void setCounterVisible(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public NavDrawerItem(String title, int icon, String count,
			boolean isCounterVisible, int type) {
		super();
		this.title = title;
		this.icon = icon;
		this.count = count;
		this.isCounterVisible = isCounterVisible;
		this.type = type;
	}

	public NavDrawerItem(String title, int icon, String count,
			boolean isCounterVisible) {
		super();
		this.title = title;
		this.icon = icon;
		this.count = count;
		this.isCounterVisible = isCounterVisible;
	}

	public NavDrawerItem(String title, int type) {
		super();
		this.title = title;
		this.type = type;
	}

	public NavDrawerItem(String title, int icon, int type) {
		super();
		this.title = title;
		this.icon = icon;
		this.type = type;
	}

	public NavDrawerItem() {
		super();
	}
}
