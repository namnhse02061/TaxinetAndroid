package vn.co.taxinet.mobile.newactivity;

import java.io.IOException;
import java.util.ArrayList;
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

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.RiderType;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TripHistoryDetailsActivity extends Activity {

	private TextView tvRiderName, tvFtom, tvTo, tvEndTime, tvFee, tvPayment,
			tvStartTime, tvPhone;
	private Trip trip;
	private Context context = this;
	private Button addVipRider;
	private ActionBar actionBar;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_details);
		initialize();
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

		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);

		Bundle bd = getIntent().getExtras();
		addVipRider = (Button) findViewById(R.id.bt_add_vip_rider);
		trip = (Trip) bd.getSerializable("trip");
		tvRiderName = (TextView) findViewById(R.id.tv_name);
		tvFtom = (TextView) findViewById(R.id.tv_from);
		tvTo = (TextView) findViewById(R.id.tv_to);
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		tvFee = (TextView) findViewById(R.id.tv_fee);
		tvPayment = (TextView) findViewById(R.id.tv_payment);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);

		tvRiderName.setText(trip.getRider().getFirstName()
				+ trip.getRider().getLastName());
		tvFtom.setText(trip.getFromAddress());
		tvTo.setText(trip.getToAddress());

		if (trip.getCompletionTime().substring(0, 10)
				.equalsIgnoreCase(Utils.getDate())) {
			tvEndTime.setText(getString(R.string.today)
					+ ", "
					+ trip.getCompletionTime().substring(11,
							trip.getCompletionTime().length()));
		} else if (trip.getCompletionTime().substring(0, 10)
				.equalsIgnoreCase(Utils.getYesterdayDateString())) {
			tvEndTime.setText(getString(R.string.yesterday)
					+ ", "
					+ trip.getCompletionTime().substring(11,
							trip.getCompletionTime().length()));
		} else {
			tvEndTime.setText(trip.getCompletionTime());
		}

		if (trip.getStartTime().substring(0, 10)
				.equalsIgnoreCase(Utils.getDate())) {
			tvStartTime.setText(getString(R.string.today)
					+ ", "
					+ trip.getStartTime().substring(11,
							trip.getStartTime().length()));
		} else if (trip.getStartTime().substring(0, 10)
				.equalsIgnoreCase(Utils.getYesterdayDateString())) {
			tvStartTime.setText(getString(R.string.yesterday)
					+ ", "
					+ trip.getStartTime().substring(11,
							trip.getStartTime().length()));
		} else {
			tvStartTime.setText(trip.getStartTime());
		}
		tvFee.setText(String.valueOf(trip.getFee()) + " VND ("
				+ trip.getDistance() + " KM)");
		tvPayment.setText(trip.getPaymenMethod());
		tvPhone.setText(trip.getRider().getPhone());

		if (trip.getRider().getType().equalsIgnoreCase(RiderType.RegularRider)) {
			addVipRider.setVisibility(View.GONE);
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
					manager.showNewTripRequestAlert(
							TripHistoryDetailsActivity.this, trip);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(mContext);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiverTrip);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			Intent it = new Intent(TripHistoryDetailsActivity.this,
					TripHistoryActivity.class);
			startActivity(it);
			finish();
		}
		return true;
	}

	public void addVipRider(View v) {
		if (Utils.isConnectingToInternet(context)) {
			new AddVipRiderBO().execute();
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(context);
		}

	}

	public class AddVipRiderBO extends AsyncTask<Void, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(TripHistoryDetailsActivity.this);
			pd.setMessage(getString(R.string.add_vip_rider));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			return getData();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				parseJson(result);
			}
		}

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				if (jsonObject.getString("message").equalsIgnoreCase(
						Constants.SUCCESS)) {
					AlertDialogManager.showCustomAlert(context,
							getString(R.string.add_vip_rider),
							getString(R.string.add_vip_rider_success_message));
					DatabaseHandler handler = new DatabaseHandler(
							getApplicationContext());
					trip.getRider().setType(RiderType.RegularRider);
					handler.updateRider(trip.getRider());
					addVipRider.setVisibility(View.GONE);
				} else {
					AlertDialogManager.showCustomAlert(getApplicationContext(),
							getString(R.string.add_vip_rider),
							getString(R.string.add_vip_rider_fail_message));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public String getData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.ADD_FAVORITE_RIDER);
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("riderId", trip
						.getRider().getId()));
				nameValuePairs.add(new BasicNameValuePair("driverId",
						AppController.getDriverId()));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));

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
	}

	@Override
	public void onBackPressed() {
		Intent it = new Intent(TripHistoryDetailsActivity.this,
				TripHistoryActivity.class);
		startActivity(it);
		finish();
	}

}
