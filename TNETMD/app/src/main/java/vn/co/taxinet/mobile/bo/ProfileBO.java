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

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Hieu-Gie
 * 
 * @createDate 20/1/2014
 */

public class ProfileBO {

	private Context mContext;
	private DatabaseHandler handler;
	private Driver driver;

	public ProfileBO(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @author Hieu-Gie
	 * 
	 *         kiểm tra dữ liệu ,gửi dữ liệu lên server và lưu dữ liệu offline
	 * 
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param phone
	 */

	public String checkProfile(String firstName, String lastName, String email,
			String phone) {
		handler = new DatabaseHandler(mContext);
		driver = handler.findDriver();

		// check null
		if (firstName.equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_first_name));
			return Constants.FAIL;
		}
		if (lastName.equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_last_name));
			return Constants.FAIL;
		}
		if (email.equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_email));
			return Constants.FAIL;
		}
		if (phone.equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_phone_number));
			return Constants.FAIL;
		}

		if (!Utils.validateEmail(email)) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.email_format_error));
			return Constants.FAIL;
		}
		// // check phone
		// if (!Utils.validatePhoneNumber(phone)) {
		// AlertDialogManager.showCustomAlert(mContext,
		// mContext.getString(R.string.error),
		// mContext.getString(R.string.phone_format_error));
		// return Constants.FAIL;
		// }

		// check if no info change
		if (!driver.getEmail().equalsIgnoreCase(email)
				|| !driver.getFirstName().equalsIgnoreCase(firstName)
				|| !driver.getLastName().equalsIgnoreCase(lastName)
				|| !driver.getPhoneNumber().equalsIgnoreCase(phone)) {
			String param[] = { firstName, lastName, email, phone };
			driver.setFirstName(firstName);
			driver.setLastName(lastName);
			driver.setEmail(email);
			driver.setPhoneNumber(phone);
			new UpdateDriverAsyncTask().execute(param);
		}
		return Constants.SUCCESS;
	}

	public class UpdateDriverAsyncTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(mContext);
			pd.setTitle(mContext.getString(R.string.update));
			pd.setMessage(mContext.getString(R.string.wait_message));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.UPDATE_DRIVER);
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("firstname", params[0]);
				jsonObject.put("lastname", params[1]);
				jsonObject.put("email", params[2]);
				jsonObject.put("id", AppController.getDriverId());
				jsonObject.put("phoneNumber", params[3]);
				String encodeString = ObjectEncoder.objectToString(jsonObject
						.toString());
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair("json", encodeString));
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
			} catch (JSONException e) {
			} catch (TNException e) {
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
				AlertDialogManager.showCannotConnectToServerAlert(mContext);
			}
		}
	}

	public void parseJson(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			String message = jsonObject.getString("message");
			if (message != null && message.equalsIgnoreCase(Constants.SUCCESS)) {
				AlertDialogManager.showCustomAlert(mContext, mContext
						.getString(R.string.update_profile_title), mContext
						.getString(R.string.update_profile_message_success));
				DatabaseHandler handler = new DatabaseHandler(mContext);
				handler.updateDriver(driver);

			} else {
				AlertDialogManager.showCustomAlert(mContext, mContext
						.getString(R.string.update_profile_title), mContext
						.getString(R.string.update_profile_message_error));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
