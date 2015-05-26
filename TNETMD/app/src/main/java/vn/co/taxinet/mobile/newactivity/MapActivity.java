/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.co.taxinet.mobile.newactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.adapter.NavDrawerListAdapter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.UpdateCurrentStatusBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.GooglePlayService;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.NavDrawerItem;
import vn.co.taxinet.mobile.model.Price;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.LruBitmapCache;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.WakeLocker;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Main UI for the demo app.
 */
@SuppressWarnings("deprecation")
public class MapActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	// Google Map
	private GoogleMap googleMap;

	// These tags will be used to cancel the requests
	private TextView mRiderName, mRiderPhoneNumber;
	private LinearLayout mReqestLayout;

	private BroadcastReceiver receiver, receiverTrip;

	private GooglePlayService googlePlayService;
	private UpdateTripBO responsRequest;
	private UpdateCurrentStatusBO mapBO;

	// boolean flag to toggle periodic location updates
	private DatabaseHandler handler;

	private Location mLastLocation;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;
	private LocationManager locationManager;
	private String bestProvider = null;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 10000; // 10 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 10; // 10 meters
	private Context mContext = this;
	private Trip trip;
	private boolean flag = false;
	public Button btAccept;
	private Price price = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_blank);
		// register gcm id
		googlePlayService = new GooglePlayService(MapActivity.this);
		if (googlePlayService.checkPlayServices(this)) {

			// Building the GoogleApi client
			buildGoogleApiClient();

			createLocationRequest();
		}
		// create slide menu
		createSlideMenu(savedInstanceState);

		initialize();
		// Loading map
		initilizeMap();
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			trip = (Trip) extras.getSerializable(Constants.TRIP);
			showNotification(trip);
		} else {
			trip = handler.getTrip();
			if (trip != null) {
				Rider rider = handler.findRiderById(trip.getRider().getId());
				trip.setRider(rider);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKED)) {
					Intent it = new Intent(MapActivity.this, PayActivity.class);
					it.putExtra(Constants.TRIP, trip);
					startActivity(it);
					finish();
				}
				showNotification(trip);
			}
		}

	}

	private void initialize() {
		// set up broadcast receiver
		IntentFilter filter = new IntentFilter(
				Constants.BroadcastAction.TRIP_REQUEST);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new mHandleMessageReceiver();
		registerReceiver(receiver, filter);

		IntentFilter filter2 = new IntentFilter(
				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
		filter2.addCategory(Intent.CATEGORY_DEFAULT);
		receiverTrip = new HandleMessageReceiver();
		registerReceiver(receiverTrip, filter2);

		btAccept = (Button) findViewById(R.id.bt_accept);

		mRiderName = (TextView) findViewById(R.id.tv_rider_name);
		mRiderPhoneNumber = (TextView) findViewById(R.id.tv_rider_phone_number);
		mReqestLayout = (LinearLayout) findViewById(R.id.request_layout);
		mReqestLayout.setVisibility(View.GONE);

		handler = new DatabaseHandler(this);
	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			bestProvider = locationManager.getBestProvider(criteria, true);
			Location location = locationManager
					.getLastKnownLocation(bestProvider);
			if (location != null) {
				onLocationChanged(location);
			}

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(this, "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
			}
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
	protected void onPause() {
		super.onPause();
		if (mGoogleApiClient.isConnected()) {
			stopLocationUpdates();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check device for Play Services APK.
		googlePlayService.checkPlayServices(this);

		// Resuming the periodic location updates
		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
		}
		if (flag) {
			mReqestLayout.setVisibility(View.VISIBLE);
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
	protected void onDestroy() {
		unregisterReceiver(receiver);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}

	public void accept(View v) {
		if (Utils.isConnectingToInternet(this)) {
			if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
				String params[] = { trip.getTripId(),
						Constants.TripStatus.PICKING };
				responsRequest = new UpdateTripBO(this);
				responsRequest.execute(params);
				return;

			}
			if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKING)) {
				String params[] = { trip.getTripId(),
						Constants.TripStatus.PICKED };
				responsRequest = new UpdateTripBO(this);
				responsRequest.execute(params);
				return;
			}
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(this);
		}

	}

	public void deni(View v) {
		if (Utils.isConnectingToInternet(this)) {
			String params[] = { trip.getTripId(),
					Constants.TripStatus.CANCELLED };
			responsRequest = new UpdateTripBO(this);
			responsRequest.execute(params);
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(this);
		}
	}

	public class mHandleMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			trip = (Trip) extras.getSerializable(Constants.TRIP);
			showNotification(trip);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return true;
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// if (drawerOpen && flag) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(getString(R.string.request));
		// builder.setMessage(getString(R.string.request_message));
		// builder.setPositiveButton(getString(R.string.back),
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// mDrawerLayout.closeDrawer(mDrawerList);
		// }
		// });
		// builder.setNegativeButton(getString(R.string.cancel_request),
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (Utils.isConnectingToInternet(MapActivity.this)) {
		// String params[] = { trip.getTripId(),
		// Constants.TripStatus.CANCELLED };
		// responsRequest = new ResponsRequest(
		// MapActivity.this);
		// responsRequest.execute(params);
		// } else {
		// manager.showInternetConnectionErrorAlert(MapActivity.this);
		// }
		// }
		// });
		// builder.show();
		// mReqestLayout.setVisibility(View.GONE);
		// }
		// menu.findItem(R.id.taxi).setVisible(!drawerOpen);
		mDrawerList.setSelection(0);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {

		// mDrawerList.setItemChecked(position, true);
		// mDrawerList.setSelection(position);
		// setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

		Intent it;
		switch (position) {
		case 0:
			it = new Intent(this, ProfileActivity.class);
			startActivity(it);
			finish();
			break;
		case 1:
			it = new Intent(this, CompanyActivity.class);
			startActivity(it);
			break;
		case 2:
			it = new Intent(this, PromotionTripActivity.class);
			startActivity(it);
			break;
		case 3:
			it = new Intent(this, VipRiderActivity.class);
			startActivity(it);
			break;
		case 4:
			it = new Intent(this, TripHistoryActivity.class);
			startActivity(it);
			break;
		case 5:
			it = new Intent(this, PaymentActivity.class);
			startActivity(it);
			break;
		case 6:
			it = new Intent(this, PaymentActivity.class);
			startActivity(it);
			break;
		case 7:
			it = new Intent(this, SupportActivity.class);
			startActivity(it);
			break;
		case 8:
			it = new Intent(this, AboutActivity.class);
			startActivity(it);
			break;
		case 9:
			if (Utils.isConnectingToInternet(this)) {
				AlertDialogManager.showLogoutAlert(this);
			} else {
				AlertDialogManager.showInternetConnectionErrorAlert(mContext);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void createSlideMenu(Bundle savedInstanceState) {

		mTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		setNavDrawerItemForCustomer();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.color.facebook, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				// getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

	public void setNavDrawerItemForCustomer() {
		// adding nav drawer items to array

		DatabaseHandler handler = new DatabaseHandler(this);
		Driver driver = handler.findDriver();
		String fullName = driver.getFirstName() + " " + driver.getLastName();

		navDrawerItems.add(new NavDrawerItem(fullName, 2));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],
				R.drawable.ic_company, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],
				R.drawable.ic_promotion_trip, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3],
				R.drawable.ic_star, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
				R.drawable.ic_history, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5],
				R.drawable.ic_payment, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6],
				R.drawable.ic_promotion_trip, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7],
				R.drawable.ic_support, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8],
				R.drawable.ic_about, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9],
				R.drawable.ic_logout, 3));
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
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
		Log.i("", "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {

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
		if (mLastLocation == null) {
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(location.getLatitude(), location
							.getLongitude())).zoom(14).build();
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}
		mLastLocation = location;

		mapBO = new UpdateCurrentStatusBO();
		HashMap<String, String> hm = Utils.getAddress(this,
				mLastLocation.getLatitude(), mLastLocation.getLongitude());
		if (hm != null) {
			StringBuilder address = new StringBuilder();
			address.append(hm.get("address")).append(", ")
					.append(hm.get("city")).append(", ")
					.append(hm.get("country"));
			String params[] = { String.valueOf(mLastLocation.getLatitude()),
					String.valueOf(mLastLocation.getLongitude()),
					address.toString() };
			mapBO.execute(params);
		} else {
			Toast.makeText(
					mContext,
					"Can't get address, please try again or check internet connection",
					Toast.LENGTH_LONG).show();
		}
	}

	public void showNotification(Trip trip) {
		// lấy trạng thái của trip
		flag = true;
		this.trip = trip;
		// nếu trạng thái là new trip
		if (!trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
			// display request
			if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
				btAccept.setText(getString(R.string.accept));
				mReqestLayout.setVisibility(View.VISIBLE);
				mRiderName.setText(trip.getRider().getFirstName() + " "
						+ trip.getRider().getLastName());
				mRiderPhoneNumber.setText(trip.getRider().getPhone());

				ImageLoader.ImageCache imageCache = new LruBitmapCache();
				ImageLoader imageLoader = new ImageLoader(
						Volley.newRequestQueue(mContext), imageCache);
				NetworkImageView imgAvatar = (NetworkImageView) findViewById(R.id.iv_image);
				imgAvatar.setImageUrl(trip.getRider().getImage(), imageLoader);

				// wake mobile up
				WakeLocker.acquire(getApplicationContext());
				WakeLocker.release();

				// Moving Camera to a Location with animation
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(trip.getFromLatitude(), trip
								.getFromLongitude())).zoom(14).build();
				// add maker
				MarkerOptions marker = new MarkerOptions().position(
						new LatLng(trip.getFromLatitude(), trip
								.getFromLongitude())).title(
						trip.getRider().getFirstName() + " "
								+ trip.getRider().getLastName());
				MarkerOptions marker2 = new MarkerOptions()
						.position(
								new LatLng(trip.getToLatitude(), trip
										.getToLongitude())).title("End");

				googleMap.addMarker(marker).showInfoWindow();
				googleMap.addMarker(marker2);

				googleMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
			}
			if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKING)) {
				btAccept.setText(getString(R.string.picked));
			}

		}
		// nếu trạng thái là cancel, thông báo ra màn hình và tắt thông tin
		// chuyến đi
		if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
			if (mReqestLayout.isShown()) {
				mReqestLayout.setVisibility(View.GONE);
			}
			AlertDialogManager.showCancelRequestAlert(mContext);
			googleMap.clear();
		}
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);

		Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		long time[] = { 0, 500, 500, 500, 0 };
		// Vibrate for 300 milliseconds
		v.vibrate(time, -1);

	}

	public void call(View v) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + trip.getRider().getPhone()));
		startActivity(callIntent);

	}

	public class UpdateTripBO extends AsyncTask<String, Void, String> {

		private Activity activity;
		private ProgressDialog pd;

		public UpdateTripBO(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle(activity.getString(R.string.response_request));
			pd.setMessage(activity.getString(R.string.response_request_message));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return responseRequest(params);

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(mContext);
			}
		}

		public String responseRequest(String params[]) {
			// Create a new HttpClient and Post Header

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.UPDATE_TRIP);
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("requestId",
						params[0]));
				nameValuePairs.add(new BasicNameValuePair("status", params[1]));
				nameValuePairs.add(new BasicNameValuePair("userId",
						AppController.getDriverId()));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				int respnseCode = response.getStatusLine().getStatusCode();
				if (respnseCode == 200) {
					HttpEntity entity = response.getEntity();
					return EntityUtils.toString(entity);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			return null;
		}

		public void parseJson(String response) {
			try {
				System.out.println(response);
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				if (message.equalsIgnoreCase(Constants.TripStatus.PICKED)) {
					mReqestLayout.setVisibility(View.GONE);
					trip.setStatus(TripStatus.PICKED);
					handler.updateTrip(trip);
					Intent it = new Intent(activity, PayActivity.class);
					if (price != null) {
						trip.setPrice(price);
					}
					it.putExtra(Constants.TRIP, trip);
					activity.startActivity(it);
					return;
				}
				if (message.equalsIgnoreCase(Constants.TripStatus.PICKING)) {
					trip.setStatus(TripStatus.PICKING);
					handler.updateTrip(trip);
					btAccept.setText(getString(R.string.picked));
				}
				if (message.equalsIgnoreCase(Constants.TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(activity);
					trip.setStatus(TripStatus.CANCELLED);
					handler.updateTrip(trip);
					if (mReqestLayout.isShown()) {
						mReqestLayout.setVisibility(View.GONE);
					}
					googleMap.clear();
					Location location = locationManager
							.getLastKnownLocation(bestProvider);
					if (location != null) {
						double latitude = location.getLatitude();
						double longitude = location.getLongitude();
						LatLng latLng = new LatLng(latitude, longitude);
						googleMap.moveCamera(CameraUpdateFactory
								.newLatLng(latLng));
						googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
						onLocationChanged(location);
					}
					flag = false;
				}

			} catch (JSONException e) {
				Toast.makeText(activity, activity.getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onBackPressed() {
	}

}
