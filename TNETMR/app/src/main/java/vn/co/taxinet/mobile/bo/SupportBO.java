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
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.exception.TNException;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.ObjectEncoder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

public class SupportBO {

	private Context mContext;

	public SupportBO(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 
	 * 
	 * @param info
	 */
	public void checkInfo(String subject, String content) {
		if (TextUtils.isEmpty(subject)) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_subject));
			return;
		}
		if (TextUtils.isEmpty(content)) {
			AlertDialogManager.showCustomAlert(mContext,
					mContext.getString(R.string.error),
					mContext.getString(R.string.null_content));
			return;
		}
		String params[] = { subject, content };
		new SupportAsyncTask().execute(params);
	}

	public class SupportAsyncTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(mContext);
			pd.setTitle(mContext.getString(R.string.send_feedback));
			pd.setMessage(mContext.getString(R.string.wait_message));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.SEND_RESPONSE_EMAIL);
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", AppController.getRiderId());
				jsonObject.put("subject", params[0]);
				jsonObject.put("content", params[1]);

				String encodeString = ObjectEncoder.objectToString(jsonObject
						.toString());

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("json", encodeString));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
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

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				if (message != null
						&& message.equalsIgnoreCase(Constants.SUCCESS)) {
					AlertDialogManager.showCustomAlert(mContext, mContext
							.getString(R.string.send_feedback), mContext
							.getString(R.string.send_feedback_successfully));
				} else {
					AlertDialogManager.showCustomAlert(mContext,
							mContext.getString(R.string.send_feedback),
							mContext.getString(R.string.reject));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
