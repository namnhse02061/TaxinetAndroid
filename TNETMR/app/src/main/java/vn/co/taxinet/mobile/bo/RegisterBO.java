package vn.co.taxinet.mobile.bo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.newactivity.LoginActivity;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

public class RegisterBO {

	private Activity activity;

	public void checkRegisterInfo(Activity context, String email,
			String password, String confirmPassword, String firstName,
			String lastName, String phoneNumber) {
		this.activity = context;
		if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(confirmPassword)
				|| TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
				|| TextUtils.isEmpty(phoneNumber)) {
			AlertDialogManager.showCustomAlert(activity,
					context.getString(R.string.register_blank_error));
			return;
		}
		if (!Utils.validateEmail(email)) {
			AlertDialogManager.showCustomAlert(activity,
					context.getString(R.string.email_format_error));
			return;
		}
		if (password.length() < 6) {
			AlertDialogManager.showCustomAlert(activity,
					context.getString(R.string.password_length_error));
			return;
		}
		if (!password.equals(confirmPassword)) {
			AlertDialogManager.showCustomAlert(activity,
					context.getString(R.string.two_password_error));
			return;
		}

		if (!Utils.validatePhoneNumber(phoneNumber)) {
			AlertDialogManager.showCustomAlert(activity,
					context.getString(R.string.phonenumber_error));
			return;
		}
		String countryCode = activity.getResources().getConfiguration().locale
				.getCountry();
		String languageCode = Locale.getDefault().getLanguage();
		String[] params = { email, password, firstName, lastName, phoneNumber,
				languageCode, Constants.UserGroup.RIDER, "VN" };
		new RegisterAsyncTask().execute(params);

	}

	public class RegisterAsyncTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle(activity.getString(R.string.register));
			pd.setMessage(activity.getString(R.string.register));
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postData(params);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(activity);
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
		}
	}

	public String postData(String[] params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.REGISTER_RIDER);
		try {
			JSONObject json = new JSONObject();
			json.put("email", params[0]);
			json.put("password", params[1]);
			json.put("firstname", params[2]);
			json.put("lastname", params[3]);
			json.put("phone", params[4]);
			json.put("language", params[5]);
			json.put("usergroup", params[6]);
			json.put("countrycode", params[7]);

			String encodeString = ObjectEncoder.objectToString(json.toString());

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("json", encodeString));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			if (respnseCode == 200) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
		} catch (TNException e) {
		}
		return null;
	}

	public void parseJson(String response) {
		try {
			JSONObject json = new JSONObject(response);
			String message = json.getString("message");
			if (message.equalsIgnoreCase(Message.EXIST_ACCOUNT)) {
				AlertDialogManager.showCustomAlert(activity,
						activity.getString(R.string.exist_email_title),
						activity.getString(R.string.exist_email_message));
			}
			if (message.equalsIgnoreCase(Message.SUCCESS)) {
				AlertDialogManager.showRegisterSuccess(activity);
			}
			// Intent it = new Intent(activity, LoginActivity.class);
			// activity.startActivity(it);
			// activity.finish();
		} catch (JSONException e) {
		}
	}
}
