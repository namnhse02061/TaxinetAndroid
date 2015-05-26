package vn.co.taxinet.mobile.newactivity;

import java.util.ArrayList;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.adapter.PromotionTripDetailsAdapter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class PromotionTripDetailsActivity extends Activity {

	private ListView mListView;
	private ArrayList<PromotionTrip> list;
	private Context mContext = this;
	private ActionBar actionBar;
	private DatabaseHandler handler;
	private PromotionTripDetailsAdapter mAdapter;
	private String promotionTripId;
	private BroadcastReceiver receiverPromotionTrip, receiverTrip;
	private String fromAddress, fromCity, toAddress, toCity, capacity, time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list2);

		initialize();
		mAdapter = new PromotionTripDetailsAdapter(this, list, promotionTripId,
				fromAddress, fromCity, toAddress, toCity, capacity, time);
		mListView.setAdapter(mAdapter);
	}

	public void initialize() {
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

		Bundle bd = getIntent().getExtras();
		promotionTripId = bd.getString("promotionId");
		capacity = bd.getString("capacity");
		time = bd.getString("startTime");
		fromAddress = bd.getString("fromAddress");
		fromCity = bd.getString("fromCity");
		toAddress = bd.getString("toAddress");
		toCity = bd.getString("toCity");

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		list = new ArrayList<PromotionTrip>();
		mListView = (ListView) findViewById(R.id.list);
		handler = new DatabaseHandler(this);
		PromotionTrip promotionTrip = handler
				.findPromotionTripById(promotionTripId);
		list.add(promotionTrip);
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
		unregisterReceiver(receiverPromotionTrip);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}
	
	//Back 20-4
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

}
