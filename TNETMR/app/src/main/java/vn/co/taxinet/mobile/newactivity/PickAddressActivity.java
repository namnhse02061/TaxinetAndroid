package vn.co.taxinet.mobile.newactivity;

import java.util.HashMap;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.ProfileBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.googleapi.PlaceProvider;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.ChangPasswordActivity.TripReceiver;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PickAddressActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	private GoogleMap googleMap;
	private boolean isPickingHome = false;
	private Float lat, lng;
	private String placeDetail = null;
	private String riderID;
	private ProfileBO bo;
	private Marker PointMarker;
	private DatabaseHandler handler;
	private MarkerOptions markerOptions;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_address);
		handleIntent(getIntent());
		initilizeMap();
		Intent i = getIntent();
		String type = i.getStringExtra("type");
		riderID = i.getStringExtra("id");
		if (type.equalsIgnoreCase("home")) {
			isPickingHome = true;
		}
	}

	private void initilizeMap() {
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
			onSearchRequested();
		}
	}

	public void pick(View v) {
		if (lat != null && lng != null) {
			if (Utils.isConnectingToInternet(this)) {
				bo = new ProfileBO();
				handler = new DatabaseHandler(this);
				Rider rider = handler.findRider();
				rider.setId(AppController.getRiderId());
				String result;
				String type = null;
				if (isPickingHome) {
					type = "home";
					rider.setHome_detail(placeDetail);
					rider.setHome_lng(lng);
					rider.setHome_lat(lat);
					result = bo.updateAddress(PickAddressActivity.this, rider,
							type);

				} else {
					type = "work";
					rider.setWork_detail(placeDetail);
					rider.setWork_lng(lng);
					rider.setWork_lat(lat);
					result = bo.updateAddress(PickAddressActivity.this, rider,
							type);
				}
				if (result.equalsIgnoreCase(Constants.SUCCESS)) {
					handler.updateRider(rider);
					Intent i = new Intent(PickAddressActivity.this,
							ProfileActivity.class);
					startActivityForResult(i, 1);
				} else {
					Toast.makeText(this, result, Toast.LENGTH_LONG).show();
				}
			} else {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.no_internet_connection_title),
						getString(R.string.no_internet_connection_message));
			}
		} else {
			AlertDialogManager.showCustomAlert(this, getString(R.string.error),
					"Please choose a point");
		}
	}

	public void repick(View v) {
		onSearchRequested();
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

	public void addHomeAddress(View v) {
		// TODO
		Intent it = new Intent(PickAddressActivity.this, ProfileActivity.class);
		startActivityForResult(it, 1);
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
		markerOptions = null;
		LatLng position = null;
		googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker marker) {
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				lat = (float) marker.getPosition().latitude;
				lng = (float) marker.getPosition().longitude;
				HashMap<String, String> hm = Utils.getAddress(
						getApplicationContext(), lat, lng);
				placeDetail = hm.get("address") + ", " + hm.get("city") + ", "
						+ hm.get("country");
				Toast.makeText(getApplicationContext(), placeDetail, Toast.LENGTH_LONG).show();
				marker.setTitle(placeDetail);
				marker.showInfoWindow();
			}

			@Override
			public void onMarkerDrag(Marker arg0) {
			}
		});

		while (c.moveToNext()) {
			markerOptions = new MarkerOptions();
			position = new LatLng(Double.parseDouble(c.getString(1)),
					Double.parseDouble(c.getString(2)));
			markerOptions.position(position);
			lat = (float) position.latitude;
			lng = (float) position.longitude;
			placeDetail = c.getString(0);
			markerOptions
					.title(c.getString(0))
					.draggable(true)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
			if (PointMarker == null) {
				PointMarker = googleMap.addMarker(markerOptions);
			} else {
				PointMarker.remove();
				PointMarker = googleMap.addMarker(markerOptions);
			}
			PointMarker.showInfoWindow();
		}
		if (position != null) {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,
					13));
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(position) // Sets the center of the map to location
										// user
					.zoom(15) // Sets the zoom
					.build(); // Creates a CameraPosition from the builder
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
		}
	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKING)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.picking_request_title),
							getString(R.string.picking_request_message));
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKED)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.picked_request_title),
							getString(R.string.picked_request_message));
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.cancel_request_title),
							getString(R.string.cancel_request_message));
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.COMPLETED)) {
					String message = getString(R.string.distance)
							+ trip.getDistance() + " KM\n"
							+ getString(R.string.fee) + ": "
							+ +(int) trip.getFee() + " VND\n"
							+ getString(R.string.completed_request_message);
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.completed_request_title),
							message);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.REJECTED)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.reject_request_title),
							getString(R.string.reject_request_message));
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
}
