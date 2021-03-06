package vn.co.taxinet.mobile.bo;

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
import vn.co.taxinet.mobile.newactivity.ForgotPasswordActivity;
import vn.co.taxinet.mobile.newactivity.LoginActivity;
import vn.co.taxinet.mobile.newactivity.StartActivity;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

public class ForgotPasswordBO {

	private Activity activity;

	public ForgotPasswordBO(Activity mContext) {
		this.activity = mContext;
	}

	/**
	 * 
	 * 
	 * @param info
	 */
	public void checkInfo(String email) {
		if (TextUtils.isEmpty(email)) {
			// return Const.EMPTY_ERROR;
			AlertDialogManager.showCustomAlert(activity,
					activity.getString(R.string.error),
					activity.getString(R.string.empty_email));
			return;
		}
		if (!Utils.validateEmail(email)) {
			AlertDialogManager.showCustomAlert(activity,
					activity.getString(R.string.error),
					activity.getString(R.string.email_format_error));
			return;
		}
		new ForgotPasswordAsyncTask().execute(new String[] { email });
	}

	public class ForgotPasswordAsyncTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle(activity.getString(R.string.reset_password));
			pd.setMessage(activity.getString(R.string.wait_message));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.RESET_PASSWORD);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", params[0]));
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

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(activity);
			}
		}

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				if (message != null
						&& message.equalsIgnoreCase(Constants.SUCCESS)) {
					AlertDialogManager
							.showCompleteResetPass(activity);
				} else {
					AlertDialogManager.showCustomAlert(activity, activity
							.getString(R.string.reset_password), activity
							.getString(R.string.reset_password_fail_message));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
