package vn.co.taxinet.mobile.newactivity;

import java.util.ArrayList;

import vn.co.taxinet.mobile.adapter.PromotionTripDetailsAdapter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.PromotionTripRiders;
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

public class PromotionTripRidersActivity extends Activity {

	private ListView mListView;
	private ArrayList<PromotionTripRiders> list;
	private ActionBar actionBar;
	private DatabaseHandler handler;
	private PromotionTripDetailsAdapter mAdapter;
	private String promotionTripId;
	private BroadcastReceiver receiverPromotionTrip, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list2);
		initialize();

		mAdapter = new PromotionTripDetailsAdapter(this, list, promotionTripId);
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
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		list = new ArrayList<PromotionTripRiders>();
		mListView = (ListView) findViewById(R.id.list);
		handler = new DatabaseHandler(this);
		list = handler.getListPromotionTripRiders(promotionTripId);
	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
					AlertDialogManager manager = new AlertDialogManager();
					manager.showNewTripRequestAlert(
							PromotionTripRidersActivity.this, trip);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(PromotionTripRidersActivity.this,
					PromotionTripActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
