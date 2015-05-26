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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.ChangeHomeAddressBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.gps.PlaceProvider;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeAddressActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private GoogleMap mGoogleMap;
	private InputMethodManager imm;
	private SearchView searchView;
	private MarkerOptions currentMarker = null;
	private TextView searchText;
	private Context mContext = this;
	private HashMap<String, String> hm;
	private BroadcastReceiver receiverPromotionTrip, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_address);
		initialize();
		initializeMap();
		setupSearchView();

	}

	private void initialize() {
		IntentFilter filter = new IntentFilter(
				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiverPromotionTrip = new HandleMessageReceiver();
		registerReceiver(receiverPromotionTrip, filter);

		IntentFilter filter2 = new IntentFilter(
				Constants.BroadcastAction.TRIP_REQUEST);
		filter2.addCategory(Intent.CATEGORY_DEFAULT);
		receiverTrip = new TripReceiver();
		registerReceiver(receiverTrip, filter2);

	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
					AlertDialogManager manager = new AlertDialogManager();
					manager.showNewTripRequestAlert(HomeAddressActivity.this,
							trip);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(mContext);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiverPromotionTrip);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}

	public void initializeMap() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mGoogleMap = fragment.getMap();
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if (location != null) {
			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());
			hm = Utils.getAddress(mContext, latlng.latitude, latlng.longitude);
			if (hm != null) {
				addMarkerAndMoveCamera(latlng);
			} else {
				Toast.makeText(
						mContext,
						"Can't get address, please try again or check internet connection",
						Toast.LENGTH_LONG).show();
			}

		}
		mGoogleMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latlng) {
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				hm = Utils.getAddress(mContext, latlng.latitude,
						latlng.longitude);
				if (hm != null) {
					addMarkerAndMoveCamera(latlng);
				} else {
					Toast.makeText(mContext,
							getString(R.string.no_internet_connection_message),
							Toast.LENGTH_LONG).show();
				}

			}
		});
		mGoogleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker marker) {

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				hm = Utils.getAddress(mContext, marker.getPosition().latitude,
						marker.getPosition().longitude);
				if (hm != null) {
					addMarkerAndMoveCamera(marker.getPosition());
				} else {
					Toast.makeText(getApplicationContext(),
							getString(R.string.no_internet_connection_message),
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onMarkerDrag(Marker marker) {

			}
		});
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

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
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

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		showLocations(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	private void showLocations(Cursor c) {
		LatLng position = null;
		mGoogleMap.clear();
		while (c.moveToNext()) {
			hm = new HashMap<String, String>();
			currentMarker = new MarkerOptions();
			position = new LatLng(Double.parseDouble(c.getString(1)),
					Double.parseDouble(c.getString(2)));
			currentMarker.position(position);
			currentMarker.title(c.getString(0));
			hm.put("city", c.getString(4));
			hm.put("address", c.getString(5));
			currentMarker.draggable(true);
			mGoogleMap.addMarker(currentMarker).showInfoWindow();
			searchText.setText("");
		}
		if (position != null) {
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(position).zoom(14).build();
			mGoogleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}
	}

	public void addMarkerAndMoveCamera(LatLng latlng) {
		mGoogleMap.clear();
		currentMarker = new MarkerOptions();
		currentMarker.position(latlng);
		currentMarker.draggable(true);
		currentMarker.title(hm.get("address") + " " + hm.get("city"));

		mGoogleMap.addMarker(currentMarker).showInfoWindow();

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latlng).zoom(14).build();
		mGoogleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	public void changeHomeAddress(View v) {
		if (Utils.isConnectingToInternet(mContext)) {
			if (currentMarker != null) {
				String params[] = { hm.get("country"), hm.get("city"),
						hm.get("address"),
						String.valueOf(currentMarker.getPosition().latitude),
						String.valueOf(currentMarker.getPosition().longitude) };
				new ChangeHomeAddressBO(HomeAddressActivity.this).execute(params);
			} else {
				AlertDialogManager.showCustomAlert(mContext, getResources()
						.getString(R.string.pick_marker_message));
			}
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(mContext);
		}

	}

	// public class ChangeHomeAddressBO extends AsyncTask<String, Void, String>
	// {
	//
	// private ProgressDialog pd;
	// private DatabaseHandler handler;
	//
	// public ChangeHomeAddressBO() {
	// handler = new DatabaseHandler(mContext);
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// pd = new ProgressDialog(mContext);
	// pd.setMessage(getString(R.string.update_home_addresss));
	// pd.setCancelable(false);
	// pd.show();
	// }
	//
	// @Override
	// protected String doInBackground(String... params) {
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(Constants.URL.UPDATE_HOME_ADDRESS);
	// try {
	//
	// JSONObject jsonObject = new JSONObject();
	// jsonObject.put("country", hm.get("country"));
	// jsonObject.put("city", hm.get("city"));
	// jsonObject.put("address", hm.get("address"));
	// jsonObject.put("id", AppController.getDriverId());
	// jsonObject.put("longitude",
	// currentMarker.getPosition().longitude);
	// jsonObject
	// .put("latitude", currentMarker.getPosition().latitude);
	//
	// String encodeString = ObjectEncoder.objectToString(jsonObject
	// .toString());
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	// nameValuePairs.add(new BasicNameValuePair("json", encodeString));
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
	// HttpResponse response = httpclient.execute(httppost);
	// int respnseCode = response.getStatusLine().getStatusCode();
	// if (respnseCode == 200) {
	// HttpEntity entity = response.getEntity();
	// return EntityUtils.toString(entity);
	// }
	// } catch (ClientProtocolException e) {
	// } catch (IOException e) {
	// } catch (JSONException e) {
	// } catch (TNException e) {
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// if (pd.isShowing()) {
	// pd.dismiss();
	// }
	// if (result != null) {
	// parseJson(result);
	// } else {
	// AlertDialogManager.showCannotConnectToServerAlert(mContext);
	// }
	// }
	//
	// public void parseJson(String response) {
	// try {
	// JSONObject jsonObject = new JSONObject(response);
	//
	// if (jsonObject.getString("message").equalsIgnoreCase(
	// Constants.SUCCESS)) {
	// Driver driver = handler.findDriver();
	// driver.setHomeAddress(hm.get("address"));
	// handler.updateDriver(driver);
	//
	// Intent returnIntent = new Intent();
	// returnIntent.putExtra("address", hm.get("address"));
	// setResult(RESULT_OK, returnIntent);
	// finish();
	// }
	// if (jsonObject.getString("message").equalsIgnoreCase(
	// Constants.FAIL)) {
	// Intent returnIntent = new Intent();
	// setResult(RESULT_CANCELED, returnIntent);
	// finish();
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra("action", "back");
			setResult(RESULT_OK, returnIntent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("action", "back");
		setResult(RESULT_OK, returnIntent);
		finish();
	}
}
