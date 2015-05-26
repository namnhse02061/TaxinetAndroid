package vn.co.taxinet.mobile.newactivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.RegisterPromotionBO;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.gps.PlaceProvider;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.CustomDateTimePicker;
import vn.co.taxinet.mobile.utils.Utils;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PickAddressActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private GoogleMap mGoogleMap;
	private InputMethodManager imm;
	private SearchView searchView;
	private RelativeLayout layout;
	private int status = 0;
	private MarkerOptions pickUpMarker, destiationMarker, currentMarker = null;
	private TextView searchText, centerText;
	private Context mContext = this;
	private PromotionTrip promotionTrip;
	private HashMap<String, String> hm;
	private Button process1, process2, process3, btTime, pick;
	private EditText etCost, etNumberOfRider;
	private RelativeLayout relativeLayout;
	private BroadcastReceiver receiver, receiverTrip;
	private DatePickerDialog pickDateDialog;

	private SimpleDateFormat dateFormatter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_address);

		layout = (RelativeLayout) findViewById(R.id.pick_address);
		promotionTrip = new PromotionTrip();
		initializeMap();

		setupSearchView();
		imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		initialize();

	}

	@SuppressLint("SimpleDateFormat")
	private void initialize() {
		IntentFilter filter = new IntentFilter(
				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new HandleMessageReceiver();
		registerReceiver(receiver, filter);

		IntentFilter filter2 = new IntentFilter(
				Constants.BroadcastAction.TRIP_REQUEST);
		filter2.addCategory(Intent.CATEGORY_DEFAULT);
		receiverTrip = new TripReceiver();
		registerReceiver(receiverTrip, filter2);

		process1 = (Button) findViewById(R.id.process1);
		process2 = (Button) findViewById(R.id.process2);
		process3 = (Button) findViewById(R.id.process3);
		pick = (Button) findViewById(R.id.pick);
		process1.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		centerText = (TextView) findViewById(R.id.center_text);
		relativeLayout = (RelativeLayout) findViewById(R.id.other_setting);
		relativeLayout.setVisibility(View.GONE);
		etCost = (EditText) findViewById(R.id.et_cost);
		etNumberOfRider = (EditText) findViewById(R.id.et_rider_number);
		btTime = (Button) findViewById(R.id.bt_time);

		Calendar newCalendar = Calendar.getInstance();
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		pickDateDialog = new DatePickerDialog(this, new OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar newDate = Calendar.getInstance();
				newDate.set(year, monthOfYear, dayOfMonth);
				btTime.setText(dateFormatter.format(newDate.getTime()));
			}

		}, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
				newCalendar.get(Calendar.DAY_OF_MONTH));
		if (Build.VERSION.SDK_INT >= 11) {
			pickDateDialog.getDatePicker().setCalendarViewShown(false);
		}
		btTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pickDateDialog.show();
			}
		});

	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
					AlertDialogManager manager = new AlertDialogManager();
					manager.showNewTripRequestAlert(PickAddressActivity.this,
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
		unregisterReceiver(receiver);
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
						getApplicationContext(),
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
					Toast.makeText(
							getApplicationContext(),
							"Can't get address, please try again or check internet connection",
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
					Toast.makeText(
							getApplicationContext(),
							"Can't get address, please try again or check internet connection",
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
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		return super.onMenuItemSelected(featureId, item);
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

	public void process1(View v) {
		mGoogleMap.clear();
		status = 0;
		process1.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		checkProcess2();
		checkProcess3();
		if (pickUpMarker != null) {
			addMarkerAndMoveCamera(pickUpMarker);
		}
		centerText.setText(getResources().getString(
				R.string.choose_pick_up_place));
		hm = null;
		currentMarker = pickUpMarker;
		relativeLayout.setVisibility(View.GONE);
		pick.setText(getString(R.string.pick_up_here));
	}

	public void process2(View v) {
		mGoogleMap.clear();
		status = 1;
		checkProcess1();
		checkProcess3();
		process2.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		if (destiationMarker != null) {
			addMarkerAndMoveCamera(destiationMarker);
		}
		centerText.setText(getResources().getString(
				R.string.choose_end_point_place));
		currentMarker = destiationMarker;
		hm = null;
		relativeLayout.setVisibility(View.GONE);
		pick.setText(getString(R.string.stop_here));
	}

	public void process3(View v) {
		mGoogleMap.clear();
		checkProcess1();
		checkProcess2();
		relativeLayout.setVisibility(View.VISIBLE);
		process3.setBackground(getResources().getDrawable(
				R.drawable.ic_processing));
		centerText.setText(getResources().getString(R.string.other_setting));
		status = 2;
		pick.setText(getString(R.string.complete_register));
	}

	public void pick(View v) {
		if (status == 0) {
			if (currentMarker != null) {
				YoYo.with(Techniques.Tada).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.process2));
				pickUpMarker = currentMarker;
				process1.setBackground(getResources().getDrawable(
						R.drawable.ic_process_done));
				status++;
				process2.setBackground(getResources().getDrawable(
						R.drawable.ic_processing));
				centerText.setText(getResources().getString(
						R.string.choose_end_point_place));
				promotionTrip.setFromAddress(hm.get("address"));
				promotionTrip.setFromCity(hm.get("city"));
				promotionTrip
						.setFromLatitude(currentMarker.getPosition().latitude);
				promotionTrip
						.setFromLongitude(currentMarker.getPosition().longitude);
				currentMarker = null;
				pick.setText(getString(R.string.stop_here));
			} else {
				AlertDialogManager.showCustomAlert(mContext, getResources()
						.getString(R.string.pick_marker_message));
			}
		} else if (status == 1) {
			if (currentMarker != null) {
				YoYo.with(Techniques.Tada).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.process3));
				destiationMarker = currentMarker;
				process2.setBackground(getResources().getDrawable(
						R.drawable.ic_process_done));
				status++;
				process3.setBackground(getResources().getDrawable(
						R.drawable.ic_processing));
				centerText.setText(getResources().getString(
						R.string.other_setting));
				promotionTrip.setToAddress(hm.get("address"));
				promotionTrip.setToCity(hm.get("city"));
				relativeLayout.setVisibility(View.VISIBLE);
				promotionTrip
						.setToLatitude(currentMarker.getPosition().latitude);
				promotionTrip
						.setToLongitude(currentMarker.getPosition().longitude);
				currentMarker = null;
				pick.setText(getString(R.string.complete_register));
			} else {
				AlertDialogManager.showCustomAlert(mContext, getResources()
						.getString(R.string.pick_marker_message));
			}
		} else {
			if (TextUtils.isEmpty(etCost.getText().toString())
					|| TextUtils.isEmpty(etNumberOfRider.getText().toString())
					|| btTime.getText().toString().equalsIgnoreCase("Set Time")) {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.error),
						getString(R.string.process3_error));
				return;
			}
			if (!TextUtils.isDigitsOnly(etNumberOfRider.getText().toString())) {
				Toast.makeText(getApplicationContext(),
						"Number of Rider's value has wrong format",
						Toast.LENGTH_LONG).show();
			} else if (!TextUtils.isDigitsOnly(etCost.getText().toString())) {
				Toast.makeText(getApplicationContext(),
						"Cost's value has wrong format", Toast.LENGTH_LONG)
						.show();
			} else {
				if (pickUpMarker == null) {
					AlertDialogManager.showCustomAlert(this,
							getString(R.string.error),
							getString(R.string.process1_error));
					return;
				}
				if (destiationMarker == null) {
					AlertDialogManager.showCustomAlert(this,
							getString(R.string.error),
							getString(R.string.process2_error));
					return;
				}
				promotionTrip.setFee(Double.parseDouble(etCost.getText()
						.toString()));
				promotionTrip.setTime(btTime.getText().toString());
				promotionTrip.setCapacity(Integer.parseInt(etNumberOfRider
						.getText().toString()));
				new RegisterPromotionBO(this, promotionTrip).execute();
			}
		}
		mGoogleMap.clear();
	}

	public void addMarkerAndMoveCamera(LatLng latlng) {
		layout.requestFocus();
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

	public void addMarkerAndMoveCamera(MarkerOptions markerOptions) {
		layout.requestFocus();
		mGoogleMap.clear();

		mGoogleMap.addMarker(markerOptions).showInfoWindow();

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(markerOptions.getPosition()).zoom(14).build();
		mGoogleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	public void checkProcess3() {
		if (TextUtils.isEmpty(etCost.getText().toString())
				|| TextUtils.isEmpty(etNumberOfRider.getText().toString())
				|| btTime.getText().toString().equalsIgnoreCase("Set Time")) {
			process3.setBackground(getResources().getDrawable(
					R.drawable.ic_process));
		} else {
			process3.setBackground(getResources().getDrawable(
					R.drawable.ic_process_done));
		}
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

}