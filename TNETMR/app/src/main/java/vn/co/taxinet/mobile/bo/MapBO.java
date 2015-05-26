//package vn.co.taxinet.mobile.bo;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import vn.co.taxinet.mobile.alert.AlertDialogManager;
//import vn.co.taxinet.mobile.app.AppController;
//import vn.co.taxinet.mobile.bo.TripBO.UpdateTripAsyncTask;
//import vn.co.taxinet.mobile.database.DatabaseHandler;
//import vn.co.taxinet.mobile.exception.TNException;
//import vn.co.taxinet.mobile.model.Trip;
//import vn.co.taxinet.mobile.utils.Constants;
//import vn.co.taxinet.mobile.utils.ObjectEncoder;
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.widget.Toast;
//
//public class MapBO extends AsyncTask<String, Void, String> {
//
//	private Trip trip;
//	private Context mContext;
//	private DatabaseHandler handler;
//	private ProgressDialog pd;
//
//	public MapBO(Trip trip, Context mContext) {
//		this.trip = trip;
//		this.mContext = mContext;
//	}
//
//	public MapBO() {
//	}
//
//	@Override
//	protected void onPreExecute() {
//		super.onPreExecute();
//		pd = new ProgressDialog(mContext);
//		pd.setTitle("Calling taxi");
//		pd.setMessage("Sending request to driver, Please wait.");
//		pd.setCancelable(false);
//		pd.show();
//	}
//
//	@Override
//	protected String doInBackground(String... params) {
//		// Create a new HttpClient and Post Header
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost(Constants.URL.CREATE_TRIP);
//		try {
//			JSONObject json = new JSONObject();
//			json.put("riderId", AppController.getRiderId());
//			json.put("driverId", trip.getDriver().getId());
//			json.put("fromlongitude", trip.getStartLongitude());
//			json.put("fromlatitude", trip.getStartLatitude());
//			json.put("tolongitude", trip.getStartLongitude());
//			json.put("tolatitude", trip.getStopLatitude());
//			json.put("paymentMethod", trip.getPaymenMethod());
//			json.put("fromAddress", trip.getFromAddress());
//			json.put("toAddress", trip.getToAddress());
//			json.put("fromCity", trip.getFromCity());
//			json.put("toCity", trip.getToCity());
//
//			String encodeString = ObjectEncoder.objectToString(json.toString());
//
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("json", encodeString));
//
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			// Execute HTTP Post Request
//			HttpResponse response = httpclient.execute(httppost);
//			int respnseCode = response.getStatusLine().getStatusCode();
//			System.out.println("response code : " + respnseCode);
//			if (respnseCode == 200) {
//
//				HttpEntity entity = response.getEntity();
//				return EntityUtils.toString(entity);
//			}
//		} catch (ClientProtocolException e) {
//		} catch (IOException e) {
//		} catch (JSONException e) {
//		} catch (TNException e) {
//		}
//		return null;
//
//	}
//
//	@Override
//	protected void onPostExecute(String response) {
//		super.onPostExecute(response);
//		if (pd.isShowing()) {
//			pd.dismiss();
//		}
//		if (response != null) {
//			JSONObject jsonObject;
//			try {
//				jsonObject = new JSONObject(response);
//				System.out.println(response);
//				String message = jsonObject.getString("message");
//				if (message.equalsIgnoreCase(Constants.SUCCESS)) {
//					String tripId = jsonObject.getString("id");
//					AppController.setTripID(tripId);
//					trip.setTripId(tripId);
//					handler = new DatabaseHandler(mContext);
//					handler.addTrip(trip);
//				}
//
//			} catch (JSONException e) {
//				AlertDialogManager.showCannotConnectToServerAlert(mContext);
//				e.printStackTrace();
//			}
//		} else {
//			AlertDialogManager.showCannotConnectToServerAlert(mContext);
//		}
//
//	}
//
//	public void UpdateTrip(Context mContext, String requestID, String userID,
//			String status) {
//		this.mContext = mContext;
//		String[] params = { requestID, userID, status };
//		new UpdateTripAsyncTask().execute(params);
//	}
//
//	public class UpdateTripAsyncTask extends AsyncTask<String, Void, String> {
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//			pd = new ProgressDialog(mContext);
//			pd.setTitle("Sending Cancel Request");
//			pd.setMessage("Please wait!");
//			pd.setCancelable(false);
//			pd.show();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			return postDataUpdateTrip(params);
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			if (result != null) {
//				parseJson(result);
//			} else {
//				AlertDialogManager.showCannotConnectToServerAlert(mContext);
//			}
//			if (pd.isShowing()) {
//				pd.dismiss();
//			}
//		}
//
//	}
//
//	public String postDataUpdateTrip(String[] params) {
//		// Create a new HttpClient and Post Header
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost(Constants.URL.UPDATE_TRIP);
//		try {
//			// Add your data
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("requestId", params[0]));
//			nameValuePairs.add(new BasicNameValuePair("userId", params[1]));
//			nameValuePairs.add(new BasicNameValuePair("status", params[2]));
//
//			// httppost.setHeader("Content-Type","application/json;charset=UTF-8");
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//
//			// Execute HTTP Post Request
//			HttpResponse response = httpclient.execute(httppost);
//			int respnseCode = response.getStatusLine().getStatusCode();
//			if (respnseCode == 200) {
//				HttpEntity entity = response.getEntity();
//				return EntityUtils.toString(entity);
//			}
//		} catch (ClientProtocolException e) {
//		} catch (IOException e) {
//		}
//		return null;
//	}
//
//	public String parseJson(String response) {
//		try {
//			JSONObject jsonObject = new JSONObject(response);
//			System.out.println(response);
//			String tripID = jsonObject.getString("message");
//			Toast.makeText(mContext, tripID, Toast.LENGTH_LONG).show();
//			AppController.setTripID(tripID);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
//}
