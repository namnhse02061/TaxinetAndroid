package vn.co.taxinet.mobile.app;

import java.util.List;

import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.utils.LruBitmapCache;
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

	public static final String TAG = AppController.class
			.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static AppController mInstance;

	public static String riderId;
	
	public static List<Driver> listDrivers;
	
	public static String tripID;
	
	public static Double distance;
	
	public static PromotionTrip findPromotionTripDetail;
	

	public static PromotionTrip getFindPromotionTripDetail() {
		return findPromotionTripDetail;
	}

	public static void setFindPromotionTripDetail(
			PromotionTrip findPromotionTripDetail) {
		AppController.findPromotionTripDetail = findPromotionTripDetail;
	}

	public static Double getDistance() {
		return distance;
	}

	public static void setDistance(Double distance) {
		AppController.distance = distance;
	}

	public static String getTripID() {
		return tripID;
	}

	public static void setTripID(String tripID) {
		AppController.tripID = tripID;
	}

	public static List<Driver> getListDrivers() {
		return listDrivers;
	}

	public static void setListDrivers(List<Driver> listDrivers) {
		AppController.listDrivers = listDrivers;
	}

	public static String getRiderId() {
		return riderId;
	}

	public static void setRiderId(String riderId) {
		AppController.riderId = riderId;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
