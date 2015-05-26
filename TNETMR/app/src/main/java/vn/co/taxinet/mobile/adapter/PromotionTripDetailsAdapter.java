package vn.co.taxinet.mobile.adapter;

import java.util.List;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.bo.RegisterPromotionTripBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class PromotionTripDetailsAdapter extends BaseAdapter {

	private Activity activity;
	private List<PromotionTrip> list;
	private TextView tvFrom, tvTo, tvName, tvNumberOfSeats, tvPhone, tvStatus,
			tvFee,tvTime;
	private DatabaseHandler handler;
	private String promotionTripId, status;
	PromotionTrip promotionTrip;
	private String fromAddress, fromCity, toAddress, toCity, capacity, time;
	private BroadcastReceiver receiver, receiverTrip;

	public PromotionTripDetailsAdapter(Activity activity,
			List<PromotionTrip> list, String promotionTripId,
			String fromAddress, String fromCity, String toAddress,
			String toCity, String capacity, String time) {
		this.activity = activity;
		this.list = list;
		this.promotionTripId = promotionTripId;
		this.fromAddress = fromAddress;
		this.fromCity = fromCity;
		this.toAddress = toAddress;
		this.toCity = toCity;
		this.capacity = capacity;
		this.time = time;
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
		
//		IntentFilter filter = new IntentFilter(
//				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
//		filter.addCategory(Intent.CATEGORY_DEFAULT);
//		receiver = new HandleMessageReceiver();
//		registerReceiver(receiver, filter);
		
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
		tvFee = (TextView) convertView.findViewById(R.id.tv_fee);
		tvTime = (TextView) convertView.findViewById(R.id.tv_time);
		Button register = (Button) convertView.findViewById(R.id.bt_register);

		promotionTrip = list.get(position);
		tvFrom.setText(promotionTrip.getFromCity() + " "
				+ promotionTrip.getFromAddress());
		tvTo.setText(promotionTrip.getToCity() + " "
				+ promotionTrip.getToAddress());
		tvNumberOfSeats.setText(String.valueOf(promotionTrip.getCapacity()));
		tvName.setText(promotionTrip.getDriver().getFirstName() + " "
				+ promotionTrip.getDriver().getLastName());
		tvPhone.setText(promotionTrip.getDriver().getPhoneNumber());
		tvFee.setText(String.valueOf((int) promotionTrip.getFee()) + " VND");
		tvTime.setText(promotionTrip.getTime());
		
		if (promotionTrip.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.REGISTERED) || promotionTrip.getStatus().equalsIgnoreCase(
						PromotionTripRiderStatus.ACCEPT)) {
			tvStatus.setVisibility(View.VISIBLE);
			tvStatus.setText(promotionTrip.getStatus());
			register.setVisibility(View.GONE);
		} else {
			tvStatus.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
		}

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String params[] = { promotionTrip.getId(), fromCity,
						fromAddress, toCity, toAddress, capacity, time };
				RegisterPromotionTripBO bo = new RegisterPromotionTripBO(
						activity);
				bo.execute(params);

			}
		});
		return convertView;
	}

	// public class UpdatePromotionTripDetails extends
	// AsyncTask<String, Void, String> {
	//
	// private Context context;
	// private ProgressDialog pd;
	// private int position;
	//
	// public UpdatePromotionTripDetails(Context activity, int position) {
	// this.context = activity;
	// this.position = position;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// pd = new ProgressDialog(context);
	// pd.setMessage(context.getString(R.string.response_request));
	// pd.setCancelable(false);
	// pd.show();
	// }
	//
	// @Override
	// protected String doInBackground(String... params) {
	// return postData(params);
	// }
	//
	// @Override
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// if (pd.isShowing()) {
	// pd.dismiss();
	// }
	// if (result != null) {
	// parseJson(result);
	// } else {
	// AlertDialogManager.showCustomAlert(activity,
	// activity.getString(R.string.cannot_connect_to_server));
	// }
	// }
	//
	// public String postData(String[] params) {
	// // Create a new HttpClient and Post Header
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(
	// Constants.URL.UPDATE_PROMOTION_TRIP_DETAILS);
	// try {
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	// nameValuePairs.add(new BasicNameValuePair("promotionTripId",
	// params[0]));
	// nameValuePairs
	// .add(new BasicNameValuePair("driverId", params[1]));
	// nameValuePairs.add(new BasicNameValuePair("status", params[2]));
	// nameValuePairs.add(new BasicNameValuePair("riderId",
	// AppController.getRiderId()));
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
	// "UTF-8"));
	// HttpResponse response = httpclient.execute(httppost);
	// int respnseCode = response.getStatusLine().getStatusCode();
	// if (respnseCode == 200) {
	// HttpEntity entity = response.getEntity();
	// return EntityUtils.toString(entity);
	// }
	// } catch (ClientProtocolException e) {
	// } catch (IOException e) {
	// }
	// return null;
	// }
	//
	// public void parseJson(String response) {
	// try {
	// JSONObject jsonObject = new JSONObject(response);
	// String message = jsonObject.getString("message");
	// PromotionTrip tripDrivers = list.get(position);
	// if (message.equalsIgnoreCase(Message.SUCCESS)) {
	//
	// if (status
	// .equalsIgnoreCase(PromotionTripRiderStatus.REJECT)) {
	// AlertDialogManager
	// .showCustomAlert(
	// activity,
	// activity.getString(R.string.reject_register_promotrion_trip));
	// // handler.updatePromotionTripDetails(tripDrivers
	// // .getDriver().getId(), tripDrivers
	// // .getPromotionTrip().getId(),
	// // PromotionTripDriverStatus.REJECT);
	// list.remove(position);
	// notifyDataSetChanged();
	// } else {
	// AlertDialogManager
	// .showCustomAlert(
	// activity,
	// activity.getString(R.string.accept_register_promotrion_trip));
	// // handler.updatePromotionTripDetails(tripDrivers
	// // .getDriver().getId(), tripDrivers
	// // .getPromotionTrip().getId(),
	// // PromotionTripDriverStatus.ACCEPT);
	// Button accept = (Button) activity
	// .findViewById(R.id.bt_register_promotion_trip);
	// accept.setVisibility(View.GONE);
	// list.get(position).setStatus(
	// PromotionTripRiderStatus.ACCEPT);
	// notifyDataSetChanged();
	// }
	//
	// }
	// if (message.equalsIgnoreCase(Message.CANCEL)) {
	// AlertDialogManager
	// .showCustomAlert(
	// activity,
	// activity.getString(R.string.cancel_register_promotrion_trip));
	// // handler.updatePromotionTripDetails(tripDrivers.getDriver()
	// // .getId(), tripDrivers.getPromotionTrip().getId(),
	// // PromotionTripDriverStatus.CANCEL);
	// list.remove(position);
	// notifyDataSetChanged();
	// }
	// if (message.equalsIgnoreCase(Message.DATA_NOT_FOUND)) {
	// Toast.makeText(context, Message.FAIL, Toast.LENGTH_LONG)
	// .show();
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// }
}
