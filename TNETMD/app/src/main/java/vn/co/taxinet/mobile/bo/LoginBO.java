package vn.co.taxinet.mobile.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.newactivity.MapActivity;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.DriverStatus;
import vn.co.taxinet.mobile.utils.Constants.LOGIN_ERROR;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.UserStatus;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

public class LoginBO {

	private Activity activity;
	private String account, password;
	private ProgressDialog pd;

	public void checkLoginInfo(Activity activity, String account,
			String password) {
		this.activity = activity;
		this.account = account;
		this.password = password;

		if (TextUtils.isEmpty(account)) {
			// return Const.EMPTY_ERROR;
			AlertDialogManager.showCustomAlert(activity,
					this.activity.getString(R.string.error),
					this.activity.getString(R.string.empty_email));
			return;
		}
		if (TextUtils.isEmpty(password)) {
			// return Const.EMPTY_ERROR;
			AlertDialogManager.showCustomAlert(activity,
					this.activity.getString(R.string.error),
					this.activity.getString(R.string.empty_password));
			return;
		}
		if (password.length() < 6) {
			AlertDialogManager.showCustomAlert(activity,
					this.activity.getString(R.string.error),
					this.activity.getString(R.string.password_length_error));
		}
		if (!Utils.validateEmail(account)) {
			AlertDialogManager.showCustomAlert(activity,
					this.activity.getString(R.string.error),
					this.activity.getString(R.string.email_format_error));
			return;
		}
		new LoginAsyncTask().execute();
	}

	public class LoginAsyncTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle(activity.getString(R.string.login));
			pd.setMessage(activity.getString(R.string.wait_message));
			pd.setCancelable(true);
			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.LOGIN_AUTHEN);
			try {
				// Add your data

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", account));
				nameValuePairs
						.add(new BasicNameValuePair("password", password));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				int respnseCode = response.getStatusLine().getStatusCode();
				if (respnseCode == 200) {
					HttpEntity entity = response.getEntity();
					return parseJson(EntityUtils.toString(entity));
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
				if (result.equalsIgnoreCase(LOGIN_ERROR.ACCOUNT_NOT_ACTIVE)) {
					AlertDialogManager.showCustomAlert(activity,
							activity.getString(R.string.login),
							activity.getString(R.string.account_not_active));
				} else if (result
						.equalsIgnoreCase(LOGIN_ERROR.WRONG_EMAIL_OR_PASSWORD)) {
					AlertDialogManager.showCustomAlert(activity, activity
							.getString(R.string.login), activity
							.getString(R.string.wrong_email_or_password));
				} else {
					Intent it = new Intent(activity, MapActivity.class);
					activity.startActivity(it);
					activity.finish();
				}
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(activity);
			}

		}
	}

	private String saveToInternalSorage(Bitmap bitmapImage) {
		ContextWrapper cw = new ContextWrapper(activity);
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("images", Context.MODE_PRIVATE);
		// Create imageDir
		File mypath = new File(directory, "profile.jpg");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return directory.getAbsolutePath();
	}

	public String parseJson(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String status = jsonObject.getString("status");
			if (status.equalsIgnoreCase(UserStatus.ACTIVE)) {
				Driver driver = new Driver();
				driver.setId(jsonObject.getString("id"));
				driver.setFirstName(jsonObject.getString("firstName"));
				driver.setLastName(jsonObject.getString("lastName"));
				driver.setEmail(jsonObject.getString("email"));
				driver.setPassword(jsonObject.getString("password"));
				driver.setPhoneNumber(jsonObject.getString("phoneNumber"));
				driver.setImage(jsonObject.getString("image"));
				driver.setBalance(Double.parseDouble(jsonObject
						.getString("balance")));
				driver.setHomeAddress(jsonObject.getString("homeAddress"));

				DatabaseHandler handler = new DatabaseHandler(activity);
				handler.addDriver(driver);
				AppController.setDriverId(jsonObject.getString("id"));
				try {
					URL imageurl = new URL(driver.getImage());
					Bitmap bitmap = BitmapFactory.decodeStream(imageurl
							.openConnection().getInputStream());
					saveToInternalSorage(bitmap);
				} catch (MalformedURLException e) {
				} catch (IOException e) {
				}
			}
			if (status.equalsIgnoreCase(DriverStatus.NEW)) {
				return LOGIN_ERROR.ACCOUNT_NOT_ACTIVE;
			}
			if (status.equalsIgnoreCase(Message.FAIL)) {
				return LOGIN_ERROR.WRONG_EMAIL_OR_PASSWORD;
			}
			return status;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}
