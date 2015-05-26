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

import vn.co.taxinet.mobile.adapter.MyTripAdatpter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Rider;
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
				if (list.get(position).getRider().getId() != null) {
					Intent it = new Intent(TripHistoryActivity.this,
							TripHistoryDetailsActivity.class);
					it.putExtra("trip", list.get(position));
					startActivity(it);
					finish();
				}
			}
		});

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (Utils.isConnectingToInternet(mContext)) {
					new GetTripHistory(false).execute();
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}

			}
		});
	}

	@Override
	protected void onDestroy() {

		unregisterReceiver(receiverTrip);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	public void initialize() {
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

		list = new ArrayList<Trip>();
		listView = (ListView) findViewById(R.id.list);
		handler = new DatabaseHandler(this);
		noRecord = (TextView) findViewById(R.id.tv_no_record);
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
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

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
					AlertDialogManager manager = new AlertDialogManager();
					manager.showNewTripRequestAlert(TripHistoryActivity.this,
							trip);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(mContext);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		if (item.getItemId() == R.id.update) {
			new GetTripHistory(true).execute();
		}
		return super.onOptionsItemSelected(item);

	}

	public class GetTripHistory extends AsyncTask<String, Void, String> {

		private boolean showProgressDialog;
		private ProgressDialog pd;

		public GetTripHistory(boolean showProgressDialog) {
			this.showProgressDialog = showProgressDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showProgressDialog) {
				pd = new ProgressDialog(mContext);
				pd.setMessage(mContext.getString(R.string.update));
				pd.show();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL.GET_LIST_COMPELTE_TRIP + "?id="
					+ AppController.getDriverId());
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
			if (showProgressDialog) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
			}
			swipeLayout.setRefreshing(false);
			if (result != null) {
				System.out.println(result);
				parseJson(result);
			} else {
				AlertDialogManager
						.showCannotConnectToServerAlert(TripHistoryActivity.this);
			}
		}

		public void parseJson(String response) {
			try {
				handler.deleteCompleteTrip();
				ArrayList<Rider> listRider = new ArrayList<Rider>();
				JSONArray jsonArray = new JSONArray(response);
				list = new ArrayList<Trip>();
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Trip trip = new Trip();
					trip.setStatus(TripStatus.COMPLETED);
					trip.setFromLatitude(Double.parseDouble(jsonObject
							.getString("startLatitude")));
					trip.setFromLongitude(Double.parseDouble(jsonObject
							.getString("startLongitude")));
					trip.setToLatitude(Double.parseDouble(jsonObject
							.getString("stopLatitude")));
					trip.setToLongitude(Double.parseDouble(jsonObject
							.getString("stopLongitude")));

					trip.setCompletionTime(jsonObject.getString("endTime"));
					trip.setStartTime(jsonObject.getString("startTime"));
					trip.setTripId(jsonObject.getString("id"));
					trip.setDistance(Double.parseDouble(jsonObject
							.getString("distance")));
					trip.setFee(Double.parseDouble(jsonObject.getString("fee")));
					trip.setPaymenMethod(jsonObject.getString("paymentType"));
					trip.setFromAddress(jsonObject.getString("fromAddress"));
					trip.setToAddress(jsonObject.getString("toAddress"));

					Rider rider = new Rider();
					JSONObject riderObject = jsonObject.getJSONObject("rider");
					rider.setId(riderObject.getString("riderId"));
					rider.setPhone(riderObject.getString("phone"));
					rider.setFirstName(riderObject.getString("firstName"));
					rider.setLastName(riderObject.getString("lastName"));
					rider.setType(riderObject.getString("type"));
					trip.setRider(rider);
					listRider.add(rider);
					list.add(trip);

					handler.addTrip(trip);
					handler.addRider(rider);
				}
				if (list.size() == 0) {
					noRecord.setVisibility(View.VISIBLE);
				} else {
					noRecord.setVisibility(View.GONE);
				}
				Toast.makeText(mContext,
						mContext.getString(R.string.update_complete),
						Toast.LENGTH_LONG).show();
				list = adapter.updateListTrip();
			} catch (JSONException e) {
				Toast.makeText(mContext, mContext.getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
