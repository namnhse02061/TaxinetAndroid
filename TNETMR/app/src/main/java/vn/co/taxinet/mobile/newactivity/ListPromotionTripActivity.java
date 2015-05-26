package vn.co.taxinet.mobile.newactivity;

import java.util.ArrayList;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.adapter.PromotionAdatpter;
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
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

public class ListPromotionTripActivity extends Activity {

	private ListView mListView;
	private ArrayList<PromotionTrip> listPromotionTrip;
	private Context mContext = this;
	private TextView tvNoRecord;
	private ActionBar actionBar;
	private DatabaseHandler handler;
	private PromotionAdatpter mAdapter;
	private BroadcastReceiver receiver, receiverTrip;
	private String fromAddress, fromCity, toAddress, toCity, capacity, time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initialize();

		mAdapter = new PromotionAdatpter(this, listPromotionTrip);
		mListView.setAdapter(mAdapter);
		mAdapter.setMode(Attributes.Mode.Single);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(ListPromotionTripActivity.this,
						PromotionTripDetailsActivity.class);
				intent.putExtra("promotionId", listPromotionTrip.get(position)
						.getId());
				intent.putExtra("fromAddress", fromAddress);
				intent.putExtra("fromCity", fromCity);
				intent.putExtra("toAddress", toAddress);
				intent.putExtra("toCity", toCity);
				intent.putExtra("capacity", capacity);
				intent.putExtra("startTime", time);
				startActivity(intent);
			}
		});
		mListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.e("ListView", "OnTouch");
				return false;
			}
		});
		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						Toast.makeText(mContext, "OnItemLongClickListener",
								Toast.LENGTH_SHORT).show();
						return false;
					}
				});
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.e("ListView", "onScrollStateChanged");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mListView
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Log.e("ListView", "onItemSelected:" + position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						Log.e("ListView", "onNothingSelected:");
					}
				});

	}

	public void initialize() {
		Bundle bd = getIntent().getExtras();
		capacity = bd.getString("capacity");
		time = bd.getString("startTime");
		fromAddress = bd.getString("fromAddress");
		fromCity = bd.getString("fromCity");
		toAddress = bd.getString("toAddress");
		toCity = bd.getString("toCity");

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

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		listPromotionTrip = new ArrayList<PromotionTrip>();
		tvNoRecord = (TextView) findViewById(R.id.tv_no_record);
		mListView = (ListView) findViewById(R.id.list);
		handler = new DatabaseHandler(this);
		listPromotionTrip = handler.getListUnRegisteredPromotionTrip();
		if (listPromotionTrip.size() == 0) {
			tvNoRecord.setVisibility(View.VISIBLE);
			// }
		} else {
			tvNoRecord.setVisibility(View.GONE);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

}
