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
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.RegisterBO.RegisterAsyncTask;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.newactivity.MapActivity;
import vn.co.taxinet.mobile.utils.Constants;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class TripBO {

	private Activity activity;
	private String account, password;
	private ProgressDialog pd;

	public void UpdateTrip(Activity context, String requestID, String userID,
			String status) {
		this.activity = context;
		String[] params = { requestID, userID, status };
		new UpdateTripAsyncTask().execute(params);
	}

	public class UpdateTripAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle("Sending Cancel Request");
			pd.setMessage("Please wait!");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postDataUpdateTrip(params);
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				parseJson(result);
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
		}

	}

	public String postDataUpdateTrip(String[] params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.UPDATE_TRIP);
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("requestId", params[0]));
			nameValuePairs.add(new BasicNameValuePair("userId", params[1]));
			nameValuePairs.add(new BasicNameValuePair("status", params[2]));

			// httppost.setHeader("Content-Type","application/json;charset=UTF-8");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			if (respnseCode == 200) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

	public void CreateTrip(Activity context, String riderid, String driverid,
			String start_lat, String start_lng, String end_lat, String end_lng,
			String distance, String paymentMethod, String estimateCost) {
		this.activity = context;
		String[] params = { riderid, driverid, start_lng, start_lat, end_lng,
				end_lat, distance, paymentMethod, estimateCost };
		new CreateTripAsyncTask().execute(params);

	}

	public class CreateTripAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(activity);
			pd.setTitle("Sending Request");
			pd.setMessage("Please wait!");
			pd.setCancelable(false);
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
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
		}

	}

	public String parseJson(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			// get message from json
			System.out.println(response);
			String tripID = jsonObject.getString("message");
			Toast.makeText(activity, tripID, Toast.LENGTH_LONG).show();
			AppController.setTripID(tripID);
			// AppController.setTripID(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//
		return null;

	}

	public String postData(String[] params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.CREATE_TRIP);
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("riderId", params[0]));
			nameValuePairs.add(new BasicNameValuePair("driverId", params[1]));
			nameValuePairs.add(new BasicNameValuePair("startlongitude",
					params[2]));
			nameValuePairs.add(new BasicNameValuePair("startlatitude",
					params[3]));
			nameValuePairs.add(new BasicNameValuePair("stoplongitude",
					params[4]));
			nameValuePairs
					.add(new BasicNameValuePair("stoplatitude", params[5]));
			nameValuePairs.add(new BasicNameValuePair("distanceEstimate",
					params[6]));
			nameValuePairs.add(new BasicNameValuePair("paymentMethod",
					params[7]));
			nameValuePairs
					.add(new BasicNameValuePair("costEstimate", params[8]));
			// httppost.setHeader("Content-Type","application/json;charset=UTF-8");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			if (respnseCode == 200) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

}
