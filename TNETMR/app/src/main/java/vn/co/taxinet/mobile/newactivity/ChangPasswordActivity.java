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
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.FavoriteDriverActivity.TripReceiver;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
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
import android.widget.EditText;
import android.widget.Toast;

public class ChangPasswordActivity extends Activity {

	private EditText mOldPassword, mNewPassword, mConfirmPassword;
	private DatabaseHandler handler;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		inialize();
	}

	public void inialize() {
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

		mOldPassword = (EditText) findViewById(R.id.et_old_password);
		mNewPassword = (EditText) findViewById(R.id.et_new_password);
		mConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
		handler = new DatabaseHandler(this);
	}

	public void changePassword(View v) {
		if (Utils.isConnectingToInternet(this)) {
			Rider rider = handler.findRider();
			String oldPassword = mOldPassword.getText().toString();
			String newPassword = mNewPassword.getText().toString();
			String confirmPassword = mConfirmPassword.getText().toString();
			// check 2 password
			if (!newPassword.equals(confirmPassword)) {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.error),
						getString(R.string.two_password_error));
				return;
			}

			if (newPassword.equalsIgnoreCase(oldPassword)) {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.error),
						getString(R.string.two_password_error_2));
				return;
			}

			if (oldPassword.length() < 6) {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.error),
						getString(R.string.password_length_error));
				return;
			}

			if (newPassword.length() < 6) {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.error),
						getString(R.string.password_length_error));
				return;
			}

			// send new password to server
			String params[] = { rider.getId(), oldPassword, newPassword };
			new ChangePassword().execute(params);

		}
	}

	public class ChangePassword extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getApplicationContext());
			pd.setTitle(getString(R.string.change_password));
			pd.setMessage(getString(R.string.change_password));
			pd.setCancelable(false);
			// pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postData(params);
		}

		public String postData(String[] params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.CHANGE_PASSWORD);
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("id", params[0]));
				nameValuePairs.add(new BasicNameValuePair("oldpassword",
						params[1]));
				nameValuePairs.add(new BasicNameValuePair("newpassword",
						params[2]));
				// httppost.setHeader("Content-Type","application/json;charset=UTF-8");
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

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			try {
				JSONObject object = new JSONObject(result);
				if (object.getString("message").equalsIgnoreCase(
						Constants.SUCCESS)) {
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}
				if (object.getString("message").equalsIgnoreCase(
						Constants.PASSWORD_ERROR)) {
					Intent returnIntent = new Intent();
					setResult(RESULT_CANCELED, returnIntent);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
		int id = item.getItemId();

		if (id == android.R.id.home) {
			Intent returnIntent = new Intent();
			returnIntent.putExtra("action", "back");
			setResult(RESULT_OK, returnIntent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("action", "back");
		setResult(RESULT_OK, returnIntent);
		finish();
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
