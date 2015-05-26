package vn.co.taxinet.mobile.newactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.adapter.FavoriteDriverAdapter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.ListRegisteredPromotionTripActivity.TripReceiver;
import vn.co.taxinet.mobile.utils.Constants.DriverType;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.Constants.URL;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

public class FavoriteDriverActivity extends Activity {

	private ListView mListView;
	private FavoriteDriverAdapter mAdapter;
	private Context mContext = this;
	private ArrayList<Driver> listDriver;
	private DatabaseHandler handler;
	private SwipeRefreshLayout swipeLayout;
	private GetListFavoriteDriverBO bo;
	private TextView tvNoRecord;
	private ActionBar actionBar;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initialize();
		mListView = (ListView) findViewById(R.id.list);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		tvNoRecord = (TextView) findViewById(R.id.tv_no_record);
		handler = new DatabaseHandler(this);

		listDriver = handler.getListDriver();
		if (listDriver == null) {
			listDriver = new ArrayList<Driver>();
			tvNoRecord.setVisibility(View.VISIBLE);

		} else {
			tvNoRecord.setVisibility(View.GONE);
			bo = new GetListFavoriteDriverBO(true);
			bo.execute();
		}
		mAdapter = new FavoriteDriverAdapter(this, listDriver);
		mListView.setAdapter(mAdapter);
		mAdapter.setMode(Attributes.Mode.Single);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((SwipeLayout) (mListView.getChildAt(position
						- mListView.getFirstVisiblePosition()))).open(true);
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
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (Utils.isConnectingToInternet(mContext)) {
					bo = new GetListFavoriteDriverBO(false);
					bo.execute();
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}

			}
		});

	}

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
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public class GetListFavoriteDriverBO extends
			AsyncTask<String, Void, String> {

		private boolean firstTime;
		private ProgressDialog pd;

		public GetListFavoriteDriverBO(boolean firstTime) {
			this.firstTime = firstTime;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (firstTime) {
				pd = new ProgressDialog(mContext);
				pd.setTitle(mContext.getString(R.string.response_request));
				pd.setMessage(mContext
						.getString(R.string.response_request_message));
				pd.setCancelable(false);
				pd.show();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL.GET_FAVORITE_DRIVER + "?id="
					+ AppController.getRiderId());
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				return stringBuilder.toString();

			} catch (ClientProtocolException cpe) {
			} catch (IOException ioe) {
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (firstTime) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
			}
			swipeLayout.setRefreshing(false);
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager
						.showCannotConnectToServerAlert(FavoriteDriverActivity.this);
			}
		}

		public void parseJson(String response) {
			try {
				JSONArray jsonArray = new JSONArray(response);
				listDriver = new ArrayList<Driver>();
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Driver driver = new Driver();
					driver.setId(jsonObject.getString("id"));
					driver.setFirstName(jsonObject.getString("firstName"));
					driver.setLastName(jsonObject.getString("lastName"));
					driver.setPhoneNumber(jsonObject.getString("phoneNumber"));
					driver.setType(DriverType.RegularDriver);
					Driver driver2 = handler.findDriverById(driver.getId());
					if (driver2 == null) {
						handler.addDriver(driver);
					} else {
						handler.updateDriver(driver);
					}
					listDriver.add(driver);

				}
				if (listDriver.size() == 0) {
					tvNoRecord.setVisibility(View.VISIBLE);
				} else {
					tvNoRecord.setVisibility(View.GONE);
				}
				swipeLayout.setRefreshing(false);
				mAdapter.updateDriverList(listDriver);
			} catch (JSONException e) {
				Toast.makeText(mContext, getString(R.string.error),
						Toast.LENGTH_LONG).show();
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
