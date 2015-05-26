package vn.co.taxinet.mobile.adapter;

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
import vn.co.taxinet.mobile.model.PromotionTripRiders;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Constants.RiderType;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PromotionTripDetailsAdapter extends BaseAdapter {

	private Activity activity;
	private List<PromotionTripRiders> list;
	private TextView tvFrom, tvTo, tvName, tvNumberOfSeats, tvPhone, tvStatus,
			tvTime;
	private DatabaseHandler handler;
	private String promotionTripId, status;

	public PromotionTripDetailsAdapter(Activity activity,
			List<PromotionTripRiders> list, String promotionTripId) {
		this.activity = activity;
		this.list = list;
		this.promotionTripId = promotionTripId;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int location) {
		return list.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_promotion_trip_details, null);
		}
		tvFrom = (TextView) convertView.findViewById(R.id.tv_from);
		tvTo = (TextView) convertView.findViewById(R.id.tv_to);
		tvName = (TextView) convertView.findViewById(R.id.tv_name);
		tvNumberOfSeats = (TextView) convertView
				.findViewById(R.id.tv_number_of_seats);
		tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
		tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
		tvTime = (TextView) convertView.findViewById(R.id.tv_time);

		PromotionTripRiders tripRiders = list.get(position);
		tvFrom.setText(tripRiders.getFromCity() + " "
				+ tripRiders.getFromAddress());
		tvTo.setText(tripRiders.getToCity() + " " + tripRiders.getToAddress());
		tvNumberOfSeats.setText(String.valueOf(tripRiders.getNumberOfSeats()));
		tvName.setText(tripRiders.getRider().getFirstName() + " "
				+ tripRiders.getRider().getLastName());
		tvPhone.setText(tripRiders.getRider().getPhone());
		tvTime.setText(tripRiders.getStartTime());

		if (list.get(position).getStatus()
				.equalsIgnoreCase(PromotionTripRiderStatus.NEWREQUEST)) {
			tvStatus.setText(activity.getString(R.string.waiting_response));
		} else {
			tvStatus.setText(activity.getString(R.string.accpeted));
		}

		Button reject = (Button) convertView.findViewById(R.id.bt_reject);
		Button accept = (Button) convertView.findViewById(R.id.bt_accept);
		if (list.get(position).getStatus()
				.equalsIgnoreCase(PromotionTripRiderStatus.ACCEPT)) {
			accept.setVisibility(View.GONE);
		}
		handler = new DatabaseHandler(convertView.getContext());

		reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v2) {
				status = PromotionTripRiderStatus.REJECT;
				if (Utils.isConnectingToInternet(activity)) {
					String[] params = { promotionTripId,
							list.get(position).getRider().getId(), status };
					new UpdatePromotionTripDetails(activity, position)
							.execute(params);
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(activity);
				}

			}
		});
		accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v2) {
				status = PromotionTripRiderStatus.ACCEPT;
				if (Utils.isConnectingToInternet(activity)) {
					String[] params = { promotionTripId,
							list.get(position).getRider().getId(), status };
					new UpdatePromotionTripDetails(activity, position)
							.execute(params);
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(activity);
				}
			}
		});
		return convertView;
	}

	public class UpdatePromotionTripDetails extends
			AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog pd;
		private int position;

		public UpdatePromotionTripDetails(Context activity, int position) {
			this.context = activity;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setMessage(context.getString(R.string.response_request));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postData(params);
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
				AlertDialogManager.showCustomAlert(activity,
						activity.getString(R.string.cannot_connect_to_server));
			}
		}

		public String postData(String[] params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					Constants.URL.UPDATE_PROMOTION_TRIP_DETAILS);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("promotionTripId",
						params[0]));
				nameValuePairs
						.add(new BasicNameValuePair("riderId", params[1]));
				nameValuePairs.add(new BasicNameValuePair("status", params[2]));
				nameValuePairs.add(new BasicNameValuePair("driverId",
						AppController.getDriverId()));
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
			}
			return null;
		}

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				PromotionTripRiders tripRiders = list.get(position);
				if (message.equalsIgnoreCase(Message.SUCCESS)) {

					if (status
							.equalsIgnoreCase(PromotionTripRiderStatus.REJECT)) {
						AlertDialogManager
								.showCustomAlert(
										activity,
										activity.getString(R.string.reject_register_promotrion_trip));
						handler.updatePromotionTripDetails(tripRiders
								.getRider().getId(), promotionTripId,
								PromotionTripRiderStatus.REJECT);
						list.remove(position);
						notifyDataSetChanged();
					} else {
						AlertDialogManager
								.showCustomAlert(
										activity,
										activity.getString(R.string.accept_register_promotrion_trip));
						handler.updatePromotionTripDetails(tripRiders
								.getRider().getId(), promotionTripId,
								PromotionTripRiderStatus.ACCEPT);
						Button accept = (Button) activity
								.findViewById(R.id.bt_accept);
						accept.setVisibility(View.GONE);
						list.get(position).setStatus(
								PromotionTripRiderStatus.ACCEPT);
						notifyDataSetChanged();
					}

				}
				if (message.equalsIgnoreCase(Message.CANCEL)) {
					AlertDialogManager
							.showCustomAlert(
									activity,
									activity.getString(R.string.cancel_register_promotrion_trip));
					handler.updatePromotionTripDetails(tripRiders.getRider()
							.getId(), promotionTripId,
							PromotionTripRiderStatus.CANCEL);
					list.remove(position);
					notifyDataSetChanged();
				}
				if (message.equalsIgnoreCase(Message.DATA_NOT_FOUND)) {
					Toast.makeText(context, Message.FAIL, Toast.LENGTH_LONG)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
