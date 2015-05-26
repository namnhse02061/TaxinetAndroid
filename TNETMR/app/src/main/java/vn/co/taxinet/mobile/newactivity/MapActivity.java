package vn.co.taxinet.mobile.newactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.adapter.NavDrawerListAdapter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.GetDriverBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.gcm.GooglePlayService;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.googleapi.DirectionsJSONParser;
import vn.co.taxinet.mobile.googleapi.PlaceProvider;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.NavDrawerItem;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Main UI for the demo app.
 */
@SuppressWarnings("deprecation")
public class MapActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private int status = 0;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	// Google Map
	private GoogleMap googleMap;

	private BroadcastReceiver receiver, proReceiver;

	private GooglePlayService googlePlayService;
	private Driver driver;
	private MarkerOptions currentMarker, pickUpMarker, destiationMarker;
	private HashMap<String, String> HashMap = null;

	private Polyline line = null;
	private Location lastLocation;
	private Context mContext = this;

	private RelativeLayout no_driver_nearby, create_request;
	private LinearLayout request_message;
	private DatabaseHandler databaseHandler;
	private TextView message, centerText, searchText;

	private Button process1, process2, pick, showInfo;
	private double costEstimate;
	private Trip trip = new Trip();

	private SearchView searchView;
	private DatabaseHandler handler;
	private boolean canFindDriver = true;
	private boolean isGetFromDatabase = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_blank);

		// register gcm id
		googlePlayService = new GooglePlayService(MapActivity.this);

		databaseHandler = new DatabaseHandler(mContext);

		// create slide menu
		createSlideMenu(savedInstanceState);

		// Loading map
		initilizeMap();
		initialize();

		if (databaseHandler.getTrip() != null) {
			canFindDriver = false;
			isGetFromDatabase = true;
			Trip trip = databaseHandler.getTrip();
			handleTripStatus(trip);
		}
		displayLocation();
		setupSearchView();

	}

	private void setupSearchView() {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) findViewById(R.id.searchView);
		SearchableInfo searchableInfo = searchManager
				.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		searchView.setQueryHint(getString(R.string.search));
		int searchPlateId = searchView.getContext().getResources()
				.getIdentifier("android:id/search_plate", null, null);
		View searchPlate = searchView.findViewById(searchPlateId);
		if (searchPlate != null) {
			searchPlate.setBackgroundColor(Color.WHITE);
			searchPlate.setPadding(5, 5, 5, 5);
			int searchTextId = searchPlate.getContext().getResources()
					.getIdentifier("android:id/search_src_text", null, null);
			searchText = (TextView) searchPlate.findViewById(searchTextId);
			if (searchText != null) {
				searchText.setTextColor(Color.BLACK);
				searchText.setHintTextColor(Color.GRAY);
			}
		}
	}

	private void initialize() {
		// startLocationUpdates();
		receiver = new mHandleMessageReceiver();
		// set up broadcast receiver
		IntentFilter filter = new IntentFilter(
				Constants.BroadcastAction.TRIP_REQUEST);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		// receiver = new mHandleMessageReceiver();
		registerReceiver(receiver, filter);
		
		IntentFilter filter2 = new IntentFilter(
				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
		filter2.addCategory(Intent.CATEGORY_DEFAULT);
		proReceiver = new HandleMessageReceiver();
		registerReceiver(proReceiver, filter);

		message = (TextView) findViewById(R.id.message);
		centerText = (TextView) findViewById(R.id.center_text);
		showInfo = (Button) findViewById(R.id.bt_show_trip_info);
		showInfo.setVisibility(View.GONE);
		pick = (Button) findViewById(R.id.pick);
		process1 = (Button) findViewById(R.id.process1);
		process2 = (Button) findViewById(R.id.process2);
		process1.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		request_message = (LinearLayout) findViewById(R.id.request_message);
		request_message.setVisibility(View.GONE);
		create_request = (RelativeLayout) findViewById(R.id.create_request);
		create_request.setVisibility(View.GONE);
	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(this, "Sorry! unable to create maps",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class mHandleMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Trip trip2 = (Trip) intent.getSerializableExtra(Constants.TRIP);
			handleTripStatus(trip2);
		}
	}

	public void handleTripStatus(Trip trip2) {

		if (trip2.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
			request_message.setVisibility(View.GONE);
			AlertDialogManager.showCustomAlert(mContext,
					getString(R.string.response),
					getString(R.string.response_cancel_message));
			googleMap.clear();
			new GetDriverBO(mContext,
					String.valueOf(lastLocation.getLatitude()),
					String.valueOf(lastLocation.getLongitude()), googleMap);
			resetLayout();
		}
		if (trip2.getStatus().equalsIgnoreCase(TripStatus.PICKING)) {
			request_message.setVisibility(View.VISIBLE);
			message.setText(getString(R.string.response_accept_message));
		}
		if (trip2.getStatus().equalsIgnoreCase(TripStatus.PICKED)) {
			if (!isGetFromDatabase) {
				request_message.setVisibility(View.GONE);
				AlertDialogManager.showCustomAlert(mContext,
						getString(R.string.picked_message));
			} else {
				Toast.makeText(mContext, getString(R.string.in_trip_message),
						Toast.LENGTH_LONG).show();
			}

		}

		if (trip2.getStatus().equalsIgnoreCase(TripStatus.COMPLETED)) {
			String message = getString(R.string.distance) + ": "
					+ trip2.getDistance() / 1000 + " KM\n"
					+ getString(R.string.fee) + ": " + +(int) trip2.getFee()
					+ " VND\n" + getString(R.string.completed_request_message);
			AlertDialogManager.showCustomAlert(mContext,
					getString(R.string.completed_request_title), message);
			googleMap.clear();
			new GetDriverBO(mContext,
					String.valueOf(lastLocation.getLatitude()),
					String.valueOf(lastLocation.getLongitude()), googleMap);
			resetLayout();

		}
		if (trip2.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
			request_message.setVisibility(View.VISIBLE);
			message.setText(getString(R.string.rider_waiting_response));
		}
	}

	private void resetLayout() {
		process1.setBackground(getResources()
				.getDrawable(R.drawable.ic_process));
		process2.setBackground(getResources()
				.getDrawable(R.drawable.ic_process));
		centerText.setText(getString(R.string.pick_up_here));
		status = -1;
		showInfo.setVisibility(View.GONE);
		pick.setText(getString(R.string.pick));
		pickUpMarker = null;
		destiationMarker = null;
	}

	// Display Location
	private void displayLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, true);
		lastLocation = locationManager.getLastKnownLocation(bestProvider);

		if (lastLocation != null) {
			if (canFindDriver) {
				new GetDriverBO(mContext, String.valueOf(lastLocation
						.getLatitude()), String.valueOf(lastLocation
						.getLongitude()), googleMap);
			}

			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					return clickOnMarker(marker);
				}

			});

			LatLng latLng = new LatLng(lastLocation.getLatitude(),
					lastLocation.getLongitude());
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
					14));

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(latLng) // Sets the center of the map to location
									// user
					.zoom(14) // Sets the zoom
					.build(); // Creates a CameraPosition from the builder
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}
	}

	public void clickOnMap(LatLng latlng, Driver driver) {
		if (driver != null) {
			if (status == 0 || status == 1) {
				googleMap.clear();
				if (status == 0) {
					pick.setText(getString(R.string.pick));
					showInfo.setVisibility(View.GONE);
					if (destiationMarker != null) {

						googleMap.addMarker(destiationMarker);
					}
				}
				if (status == 1) {
					pick.setText(getString(R.string.stop_here));
					showInfo.setVisibility(View.GONE);
					if (pickUpMarker != null) {
						googleMap.addMarker(pickUpMarker);
					}
				}
				HashMap = Utils.getAddress(mContext, latlng.latitude,
						latlng.longitude);

				if (HashMap != null) {
					currentMarker = new MarkerOptions();
					currentMarker.position(latlng);
					currentMarker.draggable(true);
					currentMarker.title(HashMap.get("address") + " "
							+ HashMap.get("city"));

					googleMap.addMarker(currentMarker).showInfoWindow();
				} else {
					Toast.makeText(
							mContext,
							"Can't get address, please try again or check internet connection",
							Toast.LENGTH_LONG).show();
				}
			}
		}

	}

	public boolean clickOnMarker(Marker marker) {
		List<Driver> drivers = AppController.getListDrivers();
		if (drivers != null) {
			for (int i = 0; i < drivers.size(); i++) {
				LatLng latLng = new LatLng(drivers.get(i).getLatitude(),
						drivers.get(i).getLongitude());
				if (latLng.equals(marker.getPosition())) {
					driver = drivers.get(i);
					create_request.setVisibility(View.VISIBLE);
					googleMap.setOnMapClickListener(new OnMapClickListener() {

						@Override
						public void onMapClick(LatLng latlng) {
							clickOnMap(latlng, driver);
						}

					});
				}
			}
		} else {
			no_driver_nearby.setVisibility(View.VISIBLE);
		}
		return false;
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	/** A class to download data from Google Directions URL */
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Directions in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(4);
				lineOptions.color(Color.RED);
			}

			// Drawing polyline in the Google Map for the i-th route
			if (line == null) {
				line = googleMap.addPolyline(lineOptions);
			} else {
				line.remove();
				line = googleMap.addPolyline(lineOptions);
			}
			costEstimate = Utils.estimateCost(driver,
					AppController.getDistance());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check device for Play Services APK.
		googlePlayService.checkPlayServices(MapActivity.this);

	}

	private void handleIntent(Intent intent) {

		if (intent.getAction() != null) {
			if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
				doSearch(intent.getStringExtra(SearchManager.QUERY));
			} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
				getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(0, data, this);
	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(1, data, this);
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.accept:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.taxi).setVisible(!drawerOpen);
		mDrawerList.setSelection(0);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {

		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

		Intent it;
		switch (position) {
		case 0:
			it = new Intent(this, ProfileActivity.class);
			startActivity(it);
			finish();
			break;
		case 1:
			it = new Intent(this, FindPromotionTripActivity.class);
			startActivity(it);
			break;
		case 2:
			it = new Intent(this, ListRegisteredPromotionTripActivity.class);
			startActivity(it);
			break;
		case 3:
			it = new Intent(this, FavoriteDriverActivity.class);
			startActivity(it);
			break;
		case 4:
			it = new Intent(this, TripHistoryActivity.class);
			startActivity(it);
			break;
		case 5:
			it = new Intent(this, SupportActivity.class);
			startActivity(it);
			break;
		case 6:
			it = new Intent(this, AboutActivity.class);
			startActivity(it);
			break;
		case 7:
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

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		setNavDrawerItemForCustomer();

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
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
		Rider rider = databaseHandler.findRider();
		navDrawerItems.add(new NavDrawerItem(rider.getFirstname() + " "
				+ rider.getLastname(), 2));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],
				R.drawable.ic_promotion_trip, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],
				R.drawable.ic_promotion_trip, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3],
				R.drawable.ic_star, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],
				R.drawable.ic_history, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5],
				R.drawable.ic_support, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6],
				R.drawable.ic_about, 1));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7],
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

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		CursorLoader cLoader = null;
		if (arg0 == 0)
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.SEARCH_URI, null, null,
					new String[] { query.getString("query") }, null);
		else if (arg0 == 1)
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.DETAILS_URI, null, null,
					new String[] { query.getString("query") }, null);
		return cLoader;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		showLocations(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void showLocations(Cursor c) {
		LatLng position = null;
		while (c.moveToNext()) {
			position = new LatLng(Double.parseDouble(c.getString(1)),
					Double.parseDouble(c.getString(2)));
			currentMarker = new MarkerOptions();
			currentMarker.position(position);

			currentMarker.title(c.getString(0));
			currentMarker.draggable(true);
			HashMap = new HashMap<String, String>();
			HashMap.put("city", c.getString(4));
			HashMap.put("address", c.getString(5));
			googleMap.addMarker(currentMarker).showInfoWindow();
			searchText.setText("");
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(position).zoom(14).build();
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}

	}

	public void cancelRequest(View v) {
		if (Utils.isConnectingToInternet(mContext)) {
			trip.setStatus(TripStatus.CANCELLED);
			new UpdateTripAsyncTask().execute();
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(mContext);
		}

	}

	public void process1(View v) {
		googleMap.clear();
		status = 0;
		process1.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		checkProcess2();
		if (pickUpMarker != null) {
			addMarkerAndMoveCamera(pickUpMarker);
		}
		if (destiationMarker != null && pickUpMarker != null) {
			googleMap.addMarker(destiationMarker);
			showInfo.setVisibility(View.VISIBLE);
			String url = Utils.getDirectionsUrl(pickUpMarker.getPosition(),
					destiationMarker.getPosition());
			DownloadTask downloadTask = new DownloadTask();
			downloadTask.execute(url);
		}
		centerText.setText(getResources().getString(R.string.pick_up_here));
		HashMap = null;
		currentMarker = pickUpMarker;
	}

	public void process2(View v) {
		googleMap.clear();
		status = 1;
		checkProcess1();
		process2.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		if (destiationMarker != null) {
			addMarkerAndMoveCamera(destiationMarker);
		}
		if (pickUpMarker != null && destiationMarker != null) {
			googleMap.addMarker(pickUpMarker);
			pick.setText(getString(R.string.stop_here));
			showInfo.setVisibility(View.VISIBLE);
			String url = Utils.getDirectionsUrl(pickUpMarker.getPosition(),
					destiationMarker.getPosition());
			DownloadTask downloadTask = new DownloadTask();
			downloadTask.execute(url);
		}
		centerText.setText(getResources().getString(R.string.stop_here));
		currentMarker = destiationMarker;
		HashMap = null;
	}

	public void addMarkerAndMoveCamera(MarkerOptions markerOptions) {

		googleMap.addMarker(markerOptions).showInfoWindow();

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(markerOptions.getPosition()).zoom(14).build();
		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	public void checkProcess2() {
		if (destiationMarker != null) {
			process2.setBackground(getResources().getDrawable(
					R.drawable.ic_process_done));
		} else {
			process2.setBackground(getResources().getDrawable(
					R.drawable.ic_process));
		}
	}

	public void checkProcess1() {
		if (pickUpMarker != null) {
			process1.setBackground(getResources().getDrawable(
					R.drawable.ic_process_done));
		} else {
			process1.setBackground(getResources().getDrawable(
					R.drawable.ic_process));
		}
	}

	public void pick(View v) {

		if (pick.getText().toString()
				.equalsIgnoreCase(getString(R.string.send_request))) {
			trip.setPaymenMethod("cash");
			trip.setDriver(driver);
			new MapBO().execute();
			googleMap.clear();

		} else if (status == 0) {
			if (!(currentMarker == null && pickUpMarker == null)) {
				if (pickUpMarker != null) {
					if (!pickUpMarker.equals(currentMarker)) {
						trip.setFromAddress(HashMap.get("address"));
						trip.setFromCity(HashMap.get("city"));
						trip.setStartLatitude(currentMarker.getPosition().latitude);
						trip.setStartLongitude(currentMarker.getPosition().longitude);
					}
				} else {
					trip.setFromAddress(HashMap.get("address"));
					trip.setFromCity(HashMap.get("city"));
					trip.setStartLatitude(currentMarker.getPosition().latitude);
					trip.setStartLongitude(currentMarker.getPosition().longitude);
				}

				pickUpMarker = currentMarker;
				process1.setBackground(getResources().getDrawable(
						R.drawable.ic_process_done));
				status++;
				process2.setBackground(getResources().getDrawable(
						R.drawable.ic_processing));
				centerText.setText(getResources().getString(
						R.string.choose_end_point_place));

				currentMarker = null;
				pick.setText(getString(R.string.stop_here));
				if (destiationMarker != null) {
					pick.setText(getString(R.string.send_request));
					showInfo.setVisibility(View.VISIBLE);
					status++;
					// Getting URL to the Google Directions API
					String url = Utils.getDirectionsUrl(
							pickUpMarker.getPosition(),
							destiationMarker.getPosition());

					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(url);
					process2.setBackground(getResources().getDrawable(
							R.drawable.ic_process_done));
				}
			} else {
				AlertDialogManager.showCustomAlert(mContext, getResources()
						.getString(R.string.pick_marker_message));
			}
		} else if (status == 1) {
			if (!(currentMarker == null && destiationMarker == null)) {
				if (destiationMarker != null) {
					if (!destiationMarker.equals(currentMarker)) {
						trip.setToAddress(HashMap.get("address"));
						trip.setToCity(HashMap.get("city"));
						trip.setStopLatitude(currentMarker.getPosition().latitude);
						trip.setStopLongitude(currentMarker.getPosition().longitude);
					}
				} else {
					trip.setToAddress(HashMap.get("address"));
					trip.setToCity(HashMap.get("city"));
					trip.setStopLatitude(currentMarker.getPosition().latitude);
					trip.setStopLongitude(currentMarker.getPosition().longitude);

				}

				destiationMarker = currentMarker;
				currentMarker = null;
				status++;
				centerText.setText(getResources().getString(
						R.string.send_request));
				process2.setBackground(getResources().getDrawable(
						R.drawable.ic_process_done));
				if (pickUpMarker != null) {
					pick.setText(getString(R.string.send_request));
					showInfo.setVisibility(View.VISIBLE);
					String url = Utils.getDirectionsUrl(
							pickUpMarker.getPosition(),
							destiationMarker.getPosition());
					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(url);
				}

			} else {
				AlertDialogManager.showCustomAlert(mContext, getResources()
						.getString(R.string.pick_marker_message));
			}
		}
	}

	public void viewTripInfo(View v) {
		String tripInfo = getString(R.string.fee) + ": " + (int) costEstimate
				+ " VND\n" + getString(R.string.distance) + ": "
				+ AppController.getDistance() + " KM";
		AlertDialogManager.showCustomAlert(mContext, tripInfo);
	}

	public class MapBO extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(mContext);
			pd.setTitle("Calling taxi");
			pd.setMessage("Sending request to driver, Please wait.");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.CREATE_TRIP);
			try {
				JSONObject json = new JSONObject();
				json.put("riderId", AppController.getRiderId());
				json.put("driverId", trip.getDriver().getId());
				json.put("fromlongitude", trip.getStartLongitude());
				json.put("fromlatitude", trip.getStartLatitude());
				json.put("tolongitude", trip.getStartLongitude());
				json.put("tolatitude", trip.getStopLatitude());
				json.put("paymentMethod", trip.getPaymenMethod());
				json.put("fromAddress", trip.getFromAddress());
				json.put("toAddress", trip.getToAddress());
				json.put("fromCity", trip.getFromCity());
				json.put("toCity", trip.getToCity());

				String encodeString = ObjectEncoder.objectToString(json
						.toString());

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair("json", encodeString));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				int respnseCode = response.getStatusLine().getStatusCode();
				System.out.println("response code : " + respnseCode);
				if (respnseCode == 200) {

					HttpEntity entity = response.getEntity();
					return EntityUtils.toString(entity);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} catch (JSONException e) {
			} catch (TNException e) {
			}
			return null;

		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (response != null) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(response);
					String message = jsonObject.getString("message");
					if (message.equalsIgnoreCase(Constants.SUCCESS)) {
						String tripId = jsonObject.getString("id");
						AppController.setTripID(tripId);
						trip.setTripId(tripId);
						trip.setStatus(TripStatus.NEW_TRIP);
						handler = new DatabaseHandler(mContext);
						handler.addTrip(trip);
						create_request.setVisibility(View.GONE);
						request_message.setVisibility(View.VISIBLE);
						googleMap.clear();
						System.out.println(trip.getTripId());
					}

				} catch (JSONException e) {
					AlertDialogManager.showCannotConnectToServerAlert(mContext);
					e.printStackTrace();
				}
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(mContext);
			}

		}
	}

	public class UpdateTripAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(mContext);
			pd.setTitle("Sending Cancel Request");
			pd.setMessage("Please wait!");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postDataUpdateTrip(params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(mContext);
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
		}

	}

	public String postDataUpdateTrip(String[] params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.UPDATE_TRIP);
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("requestId", trip
					.getTripId()));
			nameValuePairs.add(new BasicNameValuePair("userId", AppController
					.getRiderId()));
			nameValuePairs.add(new BasicNameValuePair("status",
					TripStatus.CANCELLED));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

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

	public String parseJson(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String message = jsonObject.getString("message");
			if (message.equals(TripStatus.CANCELLED)) {
				googleMap.clear();
				trip.setStatus(Message.CANCEL);
				handler = new DatabaseHandler(mContext);
				handler.updateTrip(trip);
				request_message.setVisibility(View.GONE);
				pickUpMarker = null;
				destiationMarker = null;
				showInfo.setVisibility(View.GONE);
				pick.setText(getString(R.string.pick));
				process1.setBackground(getResources().getDrawable(
						R.drawable.ic_processing));
				process2.setBackground(getResources().getDrawable(
						R.drawable.ic_process));
				centerText.setText(getString(R.string.choose_pick_up_place));
				new GetDriverBO(this, "" + lastLocation.getLatitude(), ""
						+ lastLocation.getLongitude(), googleMap);
				trip = new Trip();
				status = 0;
				driver = null;
			}
			System.out.println(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

}
