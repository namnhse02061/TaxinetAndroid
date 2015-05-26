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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class FindPromotionBO {

	private Activity activity;
	private ProgressDialog pd;
	private PromotionTrip promotionTrip;
	private String fromLat, fromLng, toLat, toLng;
	private DatabaseHandler handler;
	private List<PromotionTrip> list;

	public FindPromotionBO(Activity activity, String fromLat, String fromLng,
			String toLat, String toLng) {
		this.activity = activity;
		this.fromLat = fromLat;
		this.fromLng = fromLng;
		this.toLat = toLat;
		this.toLng = toLng;
		new FindPromotionTripAsyncTask().execute();
	}

	public class FindPromotionTripAsyncTask extends
			AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			pd = new ProgressDialog(activity);
//			pd.setTitle("Find Promotion Trip");
//			pd.setMessage("Please wait until we find promotion trips");
//			pd.setCancelable(false);
//			pd.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			return postData();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				parseJson(result);
			}else{
				AlertDialogManager.showCannotConnectToServerAlert(activity);
			}
		}
	}

	public String postData() {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.FIND_PROMOTION_TRIP);
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("fromLatitude", fromLat));
			nameValuePairs
					.add(new BasicNameValuePair("fromLongitude", fromLng));
			nameValuePairs.add(new BasicNameValuePair("toLatitude", toLat));
			nameValuePairs.add(new BasicNameValuePair("toLongitude", toLng));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			System.out.println("Response: " + respnseCode);
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

	public String parseJson(String response) {
		try {
			ArrayList<Driver> listDriver = new ArrayList<Driver>();
			JSONArray jsonArray = new JSONArray(response);
			System.out.println(response);
			list = new ArrayList<PromotionTrip>();
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jsonObject = jsonArray.getJSONObject(i);
				PromotionTrip trip = new PromotionTrip();
				trip.setStatus(TripStatus.NEW_TRIP);
				trip.setFromLatitude(Double.parseDouble(jsonObject
						.getString("startLatitude")));
				trip.setFromLongitude(Double.parseDouble(jsonObject
						.getString("startLongitude")));
				trip.setToLatitude(Double.parseDouble(jsonObject
						.getString("stopLatitude")));
				trip.setToLongitude(Double.parseDouble(jsonObject
						.getString("stopLongitude")));
				trip.setTime(jsonObject.getString("time"));
				trip.setId(jsonObject.getString("id"));
				trip.setFee(Double.parseDouble(jsonObject.getString("fee")));
				trip.setFromAddress(jsonObject.getString("fromAddress"));
				trip.setToAddress(jsonObject.getString("toAddress"));
				trip.setFromCity(jsonObject.getString("fromCity"));
				trip.setToCity(jsonObject.getString("toCity"));
				trip.setStatus(TripStatus.NEW_TRIP);

				Driver driver = new Driver();
				JSONObject driverObject = jsonObject.getJSONObject("driver");
				driver.setPhoneNumber(driverObject.getString("phoneNumber"));
				driver.setFirstName(driverObject.getString("firstName"));
				driver.setLastName(driverObject.getString("lastName"));
				trip.setDriver(driver);
				handler.addPromotionTrip(trip);
				System.out.println("DriverID: " + driverObject.getString("phoneNumber"));
				return null;
				//list.add(trip);

				
//				Driver driver2 = handler.findDriverById(driver.getId());
//				if (driver2 == null) {
//					handler.addDriver(driver);
//				} else {
//					handler.updateDriver(driver);
//				}
			}
//			if (list.size() == 0) {
//				noRecord.setVisibility(View.VISIBLE);
//			} else {
//				noRecord.setVisibility(View.GONE);
//			}
//			swipeLayout.setRefreshing(false);
//			list = adapter.updateListTrip();
		} catch (JSONException e) {
//			Toast.makeText(mContext, mContext.getString(R.string.error),
//					Toast.LENGTH_LONG).show();
		}
		return null;

	}

}
