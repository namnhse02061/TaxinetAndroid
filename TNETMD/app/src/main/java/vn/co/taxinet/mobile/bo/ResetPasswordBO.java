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
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * @author Hieu-Gie
 * 
 * @createDate 20/1/2014
 */

public class ResetPasswordBO extends AsyncTask<String, Void, String> {

	private Context mContext;
	private ProgressDialog pd;

	public ResetPasswordBO(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pd = new ProgressDialog(mContext);
		pd.setTitle(mContext.getString(R.string.logout));
		pd.setMessage(mContext.getString(R.string.logout));
		pd.setCancelable(false);
		pd.show();
	}

	@Override
	protected String doInBackground(String... params) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.URL.RESET_PASSWORD);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", params[0]));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			int respnseCode = response.getStatusLine().getStatusCode();
			if (respnseCode == 200) {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
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

	public void parseJson(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);

			String message = jsonObject.getString("message");
			if (message != null && message.equalsIgnoreCase(Constants.SUCCESS)) {
				AlertDialogManager
						.showCustomAlert(
								mContext,
								mContext.getString(R.string.reset_password),
								mContext.getString(R.string.reset_password_successfully_message));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
