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

import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import android.os.AsyncTask;

public class UpdateCurrentStatusBO extends AsyncTask<String, Void, String> {

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.UPDATE_CURRENT_STATUS);
		try {
			JSONObject json = new JSONObject();
			json.put("driverId", AppController.driverId);
			json.put("latitude", params[0]);
			json.put("longitude", params[1]);
			json.put("location", params[2]);
			String encodeString = ObjectEncoder.objectToString(json.toString());
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("json", encodeString));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
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
	}
}
