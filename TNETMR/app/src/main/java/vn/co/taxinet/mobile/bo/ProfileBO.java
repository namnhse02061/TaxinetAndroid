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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.daimajia.androidanimations.library.Techniques;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

public class ProfileBO {

	private Activity activity;
	private DatabaseHandler handler;
	private ProgressDialog pd;
	private Rider rider;

	public String updateAddress(Activity activity, Rider rider3, String type) {
		this.activity = activity;
		if (type.equalsIgnoreCase("home")) {
			String param[] = { String.valueOf(rider3.getHome_lat()),
					String.valueOf(rider3.getHome_lng()),
					rider3.getHome_detail(), "home" };
			new UpdateRiderAddressAsyncTask().execute(param);
		} else {
			String param[] = { String.valueOf(rider3.getWork_lat()),
					String.valueOf(rider3.getWork_lng()),
					rider3.getWork_detail(), "work" };
			new UpdateRiderAddressAsyncTask().execute(param);

		}
		return Constants.SUCCESS;
	}

	public class UpdateRiderAddressAsyncTask extends
			AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle("Update");
			pd.setMessage("Please wait until we check your infomation");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postData2(params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				parseJson2(result);
			}
		}
	}

	public String parseJson2(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			// get message from json
			System.out.println(response);
			String message = jsonObject.getString("message");
			// success
			if (message != null && message.equalsIgnoreCase(Constants.SUCCESS)) {
				Toast.makeText(activity, activity.getString(R.string.success),
						Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(activity, "Rider not found", Toast.LENGTH_LONG)
						.show();
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//
		return null;
	}

	public String postData2(String[] params) {
		// Create a new HttpClient and Post Header
		// HttpClient httpclient = new DefaultHttpClient();
		// HttpPost httppost = new HttpPost(Constants.URL.UPDATE_RIDER_ADDRESS);
		// try {
		// // Add your data
		// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// nameValuePairs.add(new BasicNameValuePair("id", AppController
		// .getRiderId()));
		// nameValuePairs.add(new BasicNameValuePair("lat", params[0]));
		// nameValuePairs.add(new BasicNameValuePair("lng", params[1]));
		// nameValuePairs.add(new BasicNameValuePair("detail", params[2]));
		// nameValuePairs.add(new BasicNameValuePair("type", params[3]));
		// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
		// "UTF-8"));
		//
		// // Execute HTTP Post Request
		// HttpResponse response = httpclient.execute(httppost);
		// int respnseCode = response.getStatusLine().getStatusCode();
		// if (respnseCode == 200) {
		// HttpEntity entity = response.getEntity();
		// return EntityUtils.toString(entity);
		// }
		// } catch (ClientProtocolException e) {
		// } catch (IOException e) {
		// }
		// return null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.UPDATE_RIDER_ADDRESS);
		try {
			JSONObject json = new JSONObject();
			json.put("id", AppController.getRiderId());
			json.put("lat", params[0]);
			json.put("lng", params[1]);
			json.put("detail", params[2]);
			json.put("type", params[3]);

			String encodeString = ObjectEncoder.objectToString(json.toString());
			System.out.println("Encode String: " + encodeString);
			StringEntity entity2 = new StringEntity(encodeString, HTTP.UTF_8);
			httppost.setEntity(entity2);
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			System.out.println("response code : " + respnseCode);
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

	public String checkProfile(Activity mContext, Rider rider2) {
		this.activity = mContext;
		handler = new DatabaseHandler(mContext);
		rider = handler.findRider();
		// check null
		if (rider2.getFirstname().equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_first_name));
			return Constants.FAIL;
		}
		if (rider2.getLastname().equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_last_name));
			return Constants.FAIL;
		}
		if (rider2.getEmail().equalsIgnoreCase("")) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_email));
			return Constants.FAIL;
		}
		if (TextUtils.isEmpty(rider2.getPhone())) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_phone_number));
			return Constants.FAIL;
		}

		// check email
		if (!Utils.validateEmail(rider2.getEmail())) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.email_format_error));
			return Constants.FAIL;
		}
		// // check phone
		// if (!Utils.validatePhoneNumber(rider2.getPhone())) {
		// return Constants.PHONE_NUMBER_ERROR;
		// }

		// check if no info change
		if (!rider.getEmail().equalsIgnoreCase(rider2.getEmail())
				|| !rider.getFirstname()
						.equalsIgnoreCase(rider2.getFirstname())
				|| !rider.getLastname().equalsIgnoreCase(rider2.getLastname())
				|| !rider.getPhone().equalsIgnoreCase(rider2.getPhone())) {
			String param[] = { rider2.getFirstname(), rider2.getLastname(),
					rider2.getEmail(), rider2.getPhone() };
			rider.setFirstname(rider2.getFirstname());
			rider.setLastname(rider2.getLastname());
			rider.setEmail(rider2.getEmail());
			rider.setPhone(rider2.getPhone());
			new UpdateRiderAsyncTask().execute(param);
		}
		return Constants.SUCCESS;
	}

	public class UpdateRiderAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle("Update");
			pd.setMessage("Please wait until we check your infomation");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.UPDATE_RIDER);
			try {
				JSONObject json = new JSONObject();
				json.put("id", AppController.getRiderId());
				json.put("firstname", params[0]);
				json.put("lastname", params[1]);
				json.put("email", params[2]);
				json.put("phone", params[3]);

				String encodeString = ObjectEncoder.objectToString(json
						.toString());

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs
						.add(new BasicNameValuePair("json", encodeString));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				int respnseCode = response.getStatusLine().getStatusCode();
				System.out.println("response code : " + respnseCode);
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
				AlertDialogManager.showCannotConnectToServerAlert(activity);
			}
		}
	}

	public String parseJson(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			// get message from json
			System.out.println(response);
			String message = jsonObject.getString("message");
			// success
			if (message != null && message.equalsIgnoreCase(Constants.SUCCESS)) {
				AlertDialogManager.showCustomAlert(activity, activity
						.getString(R.string.update_profile_title), activity
						.getString(R.string.update_profile_message_success));
				DatabaseHandler handler = new DatabaseHandler(activity);
				handler.updateRider(rider);

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		//
		return null;

	}

}
