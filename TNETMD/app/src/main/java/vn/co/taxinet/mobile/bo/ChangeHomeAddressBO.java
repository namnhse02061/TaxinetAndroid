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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

public class ChangeHomeAddressBO extends AsyncTask<String, Void, String> {

	private ProgressDialog pd;
	private DatabaseHandler handler;
	private Activity activity;
	private String address;

	public ChangeHomeAddressBO(Activity activity) {
		this.activity = activity;
		handler = new DatabaseHandler(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(activity);
		pd.setMessage(activity.getString(R.string.update_home_addresss));
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.UPDATE_HOME_ADDRESS);
		try {
			address = params[2];
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("country", params[0]);
			jsonObject.put("city", params[1]);
			jsonObject.put("address", params[2]);
			jsonObject.put("id", AppController.getDriverId());
			jsonObject.put("latitude", params[3]);
			jsonObject.put("longitude", params[4]);

			String encodeString = ObjectEncoder.objectToString(jsonObject
					.toString());
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

			if (jsonObject.getString("message").equalsIgnoreCase(
					Constants.SUCCESS)) {
				Driver driver = handler.findDriver();
				driver.setHomeAddress(address);
				handler.updateDriver(driver);

				Intent returnIntent = new Intent();
				returnIntent.putExtra("address", address);
				activity.setResult(Activity.RESULT_OK, returnIntent);
				activity.finish();
			}
			if (jsonObject.getString("message")
					.equalsIgnoreCase(Constants.FAIL)) {
				Intent returnIntent = new Intent();
				activity.setResult(Activity.RESULT_CANCELED, returnIntent);
				activity.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
