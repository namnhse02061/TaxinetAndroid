package vn.co.taxinet.mobile.bo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.newactivity.ListRegisteredPromotionTripActivity;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

public class RegisterPromotionTripBO extends AsyncTask<String, Void, String>{

	private ProgressDialog pd;
	private String promotionTripId,fromCity,fromAddress,toCity,toAddress,numberOfseat,time;
	private Activity activity;
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(activity);
		pd.setTitle(activity.getString(R.string.register));
		pd.setMessage(activity.getString(R.string.register));
		pd.show();
	}
	public RegisterPromotionTripBO(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	protected String doInBackground(String... params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.REGISTER_PROMOTION_TRIP);
		try {
			JSONObject json = new JSONObject();
			PromotionTrip promotionTrip = AppController.getFindPromotionTripDetail();
			json.put("riderId", AppController.getRiderId());
			json.put("promotionTripId", params[0]);
			json.put("fromCity", params[1]);
			json.put("fromAddress", params[2]);
			json.put("toCity", params[3]);
			json.put("toAddress", params[4]);
			json.put("numberOfseat", params[5]);
			json.put("time", params[6]);
			
			promotionTripId = params[0];
			fromCity = params[1];
			fromAddress = params[2];
			toCity = params[3];
			toAddress = params[4];
			numberOfseat = params[5];
			time = params[6];
			
			
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

	@Override
	protected void onPostExecute(String response) {
		super.onPostExecute(response);
		if (pd.isShowing()) {
			pd.dismiss();
		}
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			System.out.println(response);
			String message = jsonObject.getString("message");
			if (message.equalsIgnoreCase(Constants.SUCCESS)) {
				PromotionTrip promotionTrip = new PromotionTrip();
				DatabaseHandler handler = new DatabaseHandler(activity);
				
				promotionTrip = handler.findPromotionTripById(promotionTripId);
				promotionTrip.setId(promotionTripId);
				promotionTrip.setFromAddress(fromAddress);
				promotionTrip.setFromCity(fromCity);
				promotionTrip.setToAddress(toAddress);
				promotionTrip.setToCity(toCity);
				promotionTrip.setCapacity(Integer.parseInt(numberOfseat));
				promotionTrip.setTime(time);
				promotionTrip.setStatus(PromotionTripRiderStatus.REGISTERED);
				
				handler.updatePromotionTrip(promotionTrip);
				
				handler.deleteUnRegisterPromotionTrip();
				Intent intent = new Intent(activity, ListRegisteredPromotionTripActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// get message from json

	}
}
