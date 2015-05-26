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
import vn.co.taxinet.mobile.model.PromotionTripRiders;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PromotionTripRiderItem extends Activity {

	private PromotionTripRiders tripRiders;
	private TextView tvFrom, tvTo, tvName, tvNumberOfSeats, tvPhone, tvStatus,
			tvTime;
	private Context mContext = this;
	private String status;
	private DatabaseHandler handler;
	private BroadcastReceiver receiverPromotionTrip, receiverTrip;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_promotion_trip_details);
		initialize();
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

		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		Bundle bd = getIntent().getExtras();
		tripRiders = (PromotionTripRiders) bd
				.getSerializable(Constants.PROMOTION_TRIP);
		handler = new DatabaseHandler(mContext);

		tvFrom = (TextView) findViewById(R.id.tv_from);
		tvTo = (TextView) findViewById(R.id.tv_to);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvNumberOfSeats = (TextView) findViewById(R.id.tv_number_of_seats);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		tvTime = (TextView) findViewById(R.id.tv_time);

		tvFrom.setText(tripRiders.getFromCity() + " "
				+ tripRiders.getFromAddress());
		tvTo.setText(tripRiders.getToCity() + " " + tripRiders.getToAddress());
		tvNumberOfSeats.setText(String.valueOf(tripRiders.getNumberOfSeats()));
		tvName.setText(tripRiders.getRider().getFirstName() + " "
				+ tripRiders.getRider().getLastName());
		tvPhone.setText(tripRiders.getRider().getPhone());
		tvTime.setText(tripRiders.getStartTime());
		tvStatus.setText(getString(R.string.waiting_response));

		Button reject = (Button) findViewById(R.id.bt_reject);
		Button accept = (Button) findViewById(R.id.bt_accept);

		reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v2) {
				status = PromotionTripRiderStatus.REJECT;
				if (Utils.isConnectingToInternet(mContext)) {
					String[] params = { tripRiders.getPromotionTrip().getId(),
							tripRiders.getRider().getId(), status };
					new UpdatePromotionTripDetails(mContext).execute(params);
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}

			}
		});
		accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v2) {
				status = PromotionTripRiderStatus.ACCEPT;
				if (Utils.isConnectingToInternet(mContext)) {
					String[] params = { tripRiders.getPromotionTrip().getId(),
							tripRiders.getRider().getId(), status };
					new UpdatePromotionTripDetails(mContext).execute(params);
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}
			}
		});
	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				AlertDialogManager manager = new AlertDialogManager();
				manager.showNewTripRequestAlert(PromotionTripRiderItem.this,
						trip);
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
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public class UpdatePromotionTripDetails extends
			AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog pd;

		public UpdatePromotionTripDetails(Context activity) {
			this.context = activity;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setMessage(context.getString(R.string.response_request));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postData(params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCustomAlert(mContext,
						mContext.getString(R.string.cannot_connect_to_server));
			}
		}

		public String postData(String[] params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					Constants.URL.UPDATE_PROMOTION_TRIP_DETAILS);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("promotionTripId",
						params[0]));
				nameValuePairs
						.add(new BasicNameValuePair("riderId", params[1]));
				nameValuePairs.add(new BasicNameValuePair("status", params[2]));
				nameValuePairs.add(new BasicNameValuePair("driverId",
						AppController.getDriverId()));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
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

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				if (message.equalsIgnoreCase(Message.SUCCESS)) {

					if (status
							.equalsIgnoreCase(PromotionTripRiderStatus.REJECT)) {
						AlertDialogManager
								.showCustomAlert(
										mContext,
										mContext.getString(R.string.reject_register_promotrion_trip));
						handler.updatePromotionTripDetails(tripRiders
								.getRider().getId(), tripRiders
								.getPromotionTrip().getId(),
								PromotionTripRiderStatus.REJECT);
					} else {
						showCustomAlert(
								PromotionTripRiderItem.this,
								mContext.getString(R.string.accept_register_promotrion_trip));
						// AlertDialogManager
						// .showCustomAlert(
						// mContext,
						// mContext.getString(R.string.accept_register_promotrion_trip));
						handler.updatePromotionTripDetails(tripRiders
								.getRider().getId(), tripRiders
								.getPromotionTrip().getId(),
								PromotionTripRiderStatus.ACCEPT);
					}

				}
				if (message.equalsIgnoreCase(Message.CANCEL)) {
					handler.updatePromotionTripDetails(tripRiders.getRider()
							.getId(), tripRiders.getPromotionTrip().getId(),
							PromotionTripRiderStatus.CANCEL);
					AlertDialogManager
							.showCustomAlert(
									mContext,
									mContext.getString(R.string.cancel_register_promotrion_trip));

				}
				if (message.equalsIgnoreCase(Message.FAIL)) {
					AlertDialogManager.showCustomAlert(mContext,
							mContext.getString(R.string.promotion_trip_null));

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void showCustomAlert(final Activity context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(context.getString(R.string.accept),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						context.finish();
					}
				});
		builder.setCancelable(false);
		builder.show();
	}

}
