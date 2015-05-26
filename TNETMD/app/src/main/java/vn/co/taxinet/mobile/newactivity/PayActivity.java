package vn.co.taxinet.mobile.newactivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.PaymentBO;
import vn.co.taxinet.mobile.gps.GooglePlayService;
import vn.co.taxinet.mobile.model.Price;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class PayActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	// LogCat tag
	private static final String TAG = PayActivity.class.getSimpleName();

	private Location newLocation, oldLocation;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 10000; // 10 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 10; // 10 meters

	// UI elements
	private GooglePlayService googlePlayService;
	private double distance = 0, totalCost;
	private boolean firstTime = true;
	private TextView tvName, tvDistance, tvPaymentMethod, tvCost, tvFrom, tvTo;
	private Trip trip;
	private Price price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);

		initialize();

		// First we need to check availability of play services
		googlePlayService = new GooglePlayService(this);

		if (googlePlayService.checkPlayServices(this)) {

			// Building the GoogleApi client
			buildGoogleApiClient();
			createLocationRequest();
			mRequestingLocationUpdates = true;
		}
		loadPrice();
		System.out.println(price.getFirstKM());
		System.out.println(price.getOpenKM());
		System.out.println(price.getNextKM());

	}

	public void initialize() {
		// price = bd.getFloat("price");
		tvDistance = (TextView) findViewById(R.id.tv_distance);
		tvName = (TextView) findViewById(R.id.tv_rider_name);
		tvPaymentMethod = (TextView) findViewById(R.id.tv_payment);
		tvCost = (TextView) findViewById(R.id.tv_cost);
		tvFrom = (TextView) findViewById(R.id.tv_from);
		tvTo = (TextView) findViewById(R.id.tv_to);

		trip = new Trip();
		Bundle bd = getIntent().getExtras();
		if (bd != null) {
			trip = (Trip) bd.getSerializable(Constants.TRIP);
			String start = GetAddress(trip.getFromLatitude(),
					trip.getFromLongitude());
			String stop = GetAddress(trip.getToLatitude(),
					trip.getToLongitude());

			tvFrom.setText(start);
			tvTo.setText(stop);
			tvPaymentMethod.setText(trip.getPaymenMethod());
			tvDistance.setText("0 KM");
			tvCost.setText("0 VND");
			tvName.setText(trip.getRider().getFirstName() + " "
					+ trip.getRider().getLastName());
		}
	}

	public void pay(View v) {
		if (Utils.isConnectingToInternet(this)) {
			String params[] = { trip.getTripId(),
					String.valueOf(estimateCost(distance / 1000)),
					String.valueOf(distance) };
			PaymentBO bo = new PaymentBO(this);
			bo.execute(params);
		} else {
			AlertDialogManager
					.showInternetConnectionErrorAlert(getApplicationContext());
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		googlePlayService.checkPlayServices(this);
		// Resuming the periodic location updates
		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	/**
	 * Creating google api client object
	 * */
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	/**
	 * Creating location request object
	 * */
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FATEST_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
	}

	/**
	 * Starting the location updates
	 * */
	protected void startLocationUpdates() {

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	/**
	 * Stopping location updates
	 */
	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	/**
	 * Google api callback methods
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {

		// Once connected with google api, get the location

		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		// Assign the new location
		if (firstTime) {
			newLocation = location;
			firstTime = false;
			return;
		}
		oldLocation = newLocation;
		newLocation = location;
		distance += oldLocation.distanceTo(newLocation);
		double rouding = distance / 1000;
		totalCost = estimateCost(rouding);

		tvDistance.setText((double) Math.round(rouding * 100) / 100 + " KM");
		tvCost.setText((double) Math.round(totalCost * 100) / 100 + " VND");
	}

	public String GetAddress(double lat, double lon) {
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
		String ret = "";
		try {
			List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
			if (addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder();
				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
					if (!returnedAddress.getAddressLine(i).equalsIgnoreCase(
							"Unnamed Rd")) {
						strReturnedAddress.append(
								returnedAddress.getAddressLine(i)).append(" ");
					}

				}
				ret = strReturnedAddress.toString();
			} else {
				ret = "No Address returned!";
			}
		} catch (IOException e) {
			e.printStackTrace();
			ret = "Can't get Address!";
		}
		return ret;
	}

	private double estimateCost(Double distance) {

		if (distance <= price.getOpenKM()) {
			return price.getOpenKMPrice();

		} else if (distance <= price.getFirstKM()
				&& distance > price.getOpenKM()) {
			return (price.getOpenKMPrice() + (distance - price.getOpenKM())
					* price.getFirstPrice());
		} else {
			return (price.getOpenKMPrice() + price.getFirstPrice()
					* (distance - price.getFirstKM()) + price.getNextKMPrice()
					* (distance - price.getFirstKM()));
		}
	}

	public void loadPrice() {
		price = new Price();
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		price.setOpenKM(Double.parseDouble(prefs.getString("OpenKM", "0")));
		price.setOpenKMPrice(prefs.getLong("OpenKMPrice", 0));
		price.setFirstKM(prefs.getLong("FirstKM", 0));
		price.setFirstPrice(prefs.getLong("FirstKMPrice", 0));
		price.setNextKM(prefs.getLong("NextKM", 0));
		price.setNextKMPrice(prefs.getLong("NextKMPrice", 0));
	}
}
