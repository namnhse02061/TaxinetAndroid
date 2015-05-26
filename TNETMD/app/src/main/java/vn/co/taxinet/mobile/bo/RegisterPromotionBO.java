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

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * @author Hieu-Gie
 * 
 * @createDate 20/1/2014
 */

public class RegisterPromotionBO extends AsyncTask<String, Void, String> {

	private Activity activity;
	private ProgressDialog pd;
	private DatabaseHandler handler;
	private PromotionTrip promotionTrip;

	public RegisterPromotionBO(Activity activity, PromotionTrip promotionTrip) {
		this.activity = activity;
		handler = new DatabaseHandler(activity);
		this.promotionTrip = promotionTrip;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(activity);
		pd.setTitle(activity.getString(R.string.register));
		pd.setMessage(activity.getString(R.string.register_promotion_trip));
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.REGISTER_PROMOTION_TRIP);
		try {

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", AppController.getDriverId());
			jsonObject.put("fromCity", promotionTrip.getFromCity());
			jsonObject.put("toCity", promotionTrip.getToCity());
			jsonObject.put("fromAddress", promotionTrip.getFromAddress());
			jsonObject.put("toAddress", promotionTrip.getToAddress());
			jsonObject.put("fromLatitude", promotionTrip.getFromLatitude());
			jsonObject.put("fromLongitude", promotionTrip.getFromLongitude());
			jsonObject.put("toLatitude", promotionTrip.getToLatitude());
			jsonObject.put("toLongitude", promotionTrip.getToLongitude());
			jsonObject.put("numberOfseat", promotionTrip.getCapacity());
			jsonObject.put("fee", promotionTrip.getFee());
			jsonObject.put("time", promotionTrip.getTime());

			String encodeString = ObjectEncoder.objectToString(jsonObject
					.toString());

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("json", encodeString));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			if (respnseCode == 200) {
				HttpEntity entity2 = response.getEntity();
				return EntityUtils.toString(entity2);
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

			String id = jsonObject.getString("id");
			if (!id.equalsIgnoreCase("")) {
				promotionTrip.setId(id);
				handler.addPromotionTrip(promotionTrip);
				AlertDialogManager.showRegisterPromotionAlert(activity);
			}
		} catch (JSONException e) {
			AlertDialogManager.showCannotConnectToServerAlert(activity);
		}
	}

}
