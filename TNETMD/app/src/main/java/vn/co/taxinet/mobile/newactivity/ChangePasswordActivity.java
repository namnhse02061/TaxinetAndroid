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
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class ChangePasswordActivity extends Activity {

	private EditText mOldPassword, mNewPassword, mConfirmPassword;
	private DatabaseHandler handler;
	private ActionBar actionBar;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		mOldPassword = (EditText) findViewById(R.id.et_old_password);
		mNewPassword = (EditText) findViewById(R.id.et_new_password);
		mConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
		handler = new DatabaseHandler(this);
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
							ChangePasswordActivity.this, trip);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(mContext);
				}
			}
		}
	}

	public void changePassword(View v) {
		if (Utils.isConnectingToInternet(this)) {
			Driver driver = handler.findDriver();
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
			String params[] = { driver.getId(), oldPassword, newPassword };
			new ChangePassword(this).execute(params);

		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}

	public class ChangePassword extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;
		private Activity activity;

		public ChangePassword(Activity activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle(getString(R.string.change_password));
			pd.setMessage(getString(R.string.change_password));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
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
			if (result != null) {
				try {
					JSONObject object = new JSONObject(result);
					if (object.getString("message").equalsIgnoreCase(
							Constants.SUCCESS)) {
						Driver driver = handler.findDriver();
						driver.setPassword(mNewPassword.getText().toString());
						handler.updateDriver(driver);
						Intent returnIntent = new Intent();
						setResult(RESULT_OK, returnIntent);
						finish();

					}
					if (object.getString("message").equalsIgnoreCase(
							Constants.FAIL)) {
						Intent returnIntent = new Intent();
						setResult(RESULT_CANCELED, returnIntent);
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				AlertDialogManager
						.showCannotConnectToServerAlert(ChangePasswordActivity.this);
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
}
