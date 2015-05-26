package vn.co.taxinet.mobile.newactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.adapter.MyTripAdatpter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.Constants.URL;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TripHistoryActivity extends Activity {

	private ListView listView;
	private List<Trip> list;
	private DatabaseHandler handler;
	private TextView noRecord;
	private SwipeRefreshLayout swipeLayout;
	private MyTripAdatpter adapter;
	private ActionBar actionBar;
	private Context mContext = this;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initialize();

		adapter = new MyTripAdatpter(this, list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				if (list.get(position).getDriver().getId() != null) {
					Intent it = new Intent(TripHistoryActivity.this,
							TripHistoryDetailsActivity.class);
					it.putExtra("trip", list.get(position));
					startActivity(it);
				}
			}
		});

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new GetTripHistory(false).execute();
			}
		});
	}

	public void initialize() {
		list = new ArrayList<Trip>();
		listView = (ListView) findViewById(R.id.list);
		handler = new DatabaseHandler(this);
		noRecord = (TextView) findViewById(R.id.tv_no_record);

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

		// actionBar = getActionBar();
		// actionBar.setDisplayShowHomeEnabled(true);
		list = handler.getListTrip();
		if (list.size() == 0) {
			noRecord.setVisibility(View.VISIBLE);
			if (Utils.isConnectingToInternet(mContext)) {
				new GetTripHistory(false).execute();
			} else {
				AlertDialogManager.showInternetConnectionErrorAlert(mContext);
			}
		} else {
			noRecord.setVisibility(View.GONE);
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
	protected void onDestroy() {
		unregisterReceiver(receiver);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}

	public class GetTripHistory extends AsyncTask<String, Void, String> {

		private boolean firstTime;
		private ProgressDialog pd;

		public GetTripHistory(boolean firstTime) {
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
			HttpGet httpGet = new HttpGet(URL.GET_LIST_COMPELTE_TRIP_RIDER
					+ "?id=" + AppController.getRiderId());
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
						.showCannotConnectToServerAlert(TripHistoryActivity.this);
			}
		}

		public void parseJson(String response) {
			try {
				handler.deleteCompleteTrip();
				ArrayList<Driver> listDriver = new ArrayList<Driver>();
				JSONArray jsonArray = new JSONArray(response);
				System.out.println(response);
				list = new ArrayList<Trip>();
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Trip trip = new Trip();
					trip.setStatus(TripStatus.COMPLETED);
					trip.setStartLatitude(Double.parseDouble(jsonObject
							.getString("startLatitude")));
					trip.setStartLongitude(Double.parseDouble(jsonObject
							.getString("startLongitude")));
					trip.setStopLatitude(Double.parseDouble(jsonObject
							.getString("stopLatitude")));
					trip.setStopLongitude(Double.parseDouble(jsonObject
							.getString("stopLongitude")));

					trip.setCompletionTime(jsonObject.getString("endTime"));
					trip.setTripId(jsonObject.getString("id"));
					trip.setDistance(Double.parseDouble(jsonObject
							.getString("distance")));
					trip.setFee(Double.parseDouble(jsonObject.getString("fee")));
					trip.setPaymenMethod(jsonObject.getString("paymentType"));
					trip.setFromAddress(jsonObject.getString("fromAddress"));
					trip.setToAddress(jsonObject.getString("toAddress"));

					Driver driver = new Driver();
					JSONObject driverObject = jsonObject
							.getJSONObject("driver");
					driver.setId(driverObject.getString("id"));
					driver.setPhoneNumber(driverObject.getString("phoneNumber"));
					driver.setFirstName(driverObject.getString("firstName"));
					driver.setLastName(driverObject.getString("lastName"));
					driver.setType(driverObject.getString("type"));
					trip.setDriver(driver);
					listDriver.add(driver);
					list.add(trip);

					handler.addTrip(trip);
					Driver driver2 = handler.findDriverById(driver.getId());
					if (driver2 == null) {
						handler.addDriver(driver);
					} else {
						handler.updateDriver(driver);
					}
				}
				if (list.size() == 0) {
					noRecord.setVisibility(View.VISIBLE);
				} else {
					noRecord.setVisibility(View.GONE);
				}
				swipeLayout.setRefreshing(false);
				list = adapter.updateListTrip();
			} catch (JSONException e) {
				Toast.makeText(mContext, mContext.getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
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
}
