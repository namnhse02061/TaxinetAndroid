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

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.ListPromotionTripActivity.TripReceiver;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.DriverType;
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
			tvPhone;
	private Trip trip;
	private Context context = this;
	private Button addFavoriteDriver;
	private ActionBar actionBar;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_trip_details);
		initialize();
	}

	public void initialize() {
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);

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

		Bundle bd = getIntent().getExtras();
		addFavoriteDriver = (Button) findViewById(R.id.bt_add_vip_rider);
		trip = (Trip) bd.getSerializable("trip");
		tvRiderName = (TextView) findViewById(R.id.tv_name);
		tvFtom = (TextView) findViewById(R.id.tv_from);
		tvTo = (TextView) findViewById(R.id.tv_to);
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		tvFee = (TextView) findViewById(R.id.tv_fee);
		tvPayment = (TextView) findViewById(R.id.tv_payment);
		tvPhone = (TextView) findViewById(R.id.tv_phone);

		tvRiderName.setText(trip.getDriver().getFirstName() + " "
				+ trip.getDriver().getLastName());
		tvFtom.setText(trip.getFromAddress());
		tvTo.setText(trip.getToAddress());
		tvEndTime.setText(trip.getCompletionTime());
		tvFee.setText(String.valueOf(trip.getFee()) + " VND("
				+ trip.getDistance() + " KM)");
		tvPayment.setText(trip.getPaymenMethod());
		tvPhone.setText(trip.getDriver().getPhoneNumber());

		if (trip.getDriver().getType()
				.equalsIgnoreCase(DriverType.RegularDriver)) {
			addFavoriteDriver.setVisibility(View.GONE);
		}
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

	public void addFavoriteDriver(View v) {
		new AddFavoriteDriverBO().execute();
	}

	public class AddFavoriteDriverBO extends AsyncTask<Void, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(TripHistoryDetailsActivity.this);
			pd.setMessage(getString(R.string.add_favorite_driver_message));
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
					AlertDialogManager
							.showCustomAlert(
									context,
									getString(R.string.add_favorite_driver_message),
									getString(R.string.add_favorite_driver_success_message));
					DatabaseHandler handler = new DatabaseHandler(
							getApplicationContext());
					trip.getDriver().setType(DriverType.RegularDriver);
					handler.updateDriver(trip.getDriver());
					addFavoriteDriver.setVisibility(View.GONE);
				} else {
					AlertDialogManager
							.showCustomAlert(
									getApplicationContext(),
									getString(R.string.add_favorite_driver_message),
									getString(R.string.add_favorite_driver_fail_message));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public String getData() {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.ADD_FAVORITE_DRIVER);
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("riderId",
						AppController.getRiderId()));
				nameValuePairs.add(new BasicNameValuePair("driverId", trip
						.getDriver().getId()));
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
