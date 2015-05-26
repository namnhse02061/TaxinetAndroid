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

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripStatus;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

public class PromotionAdatpter extends BaseSwipeAdapter {

	private Activity activity;
	private List<PromotionTrip> list;
	private TextView tvTime, tvStatus, tvFrom, tvTo, tvFee;
	private DatabaseHandler handler;

	public PromotionAdatpter(Activity activity, List<PromotionTrip> list) {
		this.activity = activity;
		this.list = list;
	}

	@Override
	public void fillValues(final int position, final View v) {
		tvTime = (TextView) v.findViewById(R.id.tv_time);
		tvStatus = (TextView) v.findViewById(R.id.tv_status);
		tvFrom = (TextView) v.findViewById(R.id.tv_from);
		tvTo = (TextView) v.findViewById(R.id.tv_to);

		PromotionTrip promotionTrip = list.get(position);
		tvTime.setText(promotionTrip.getTime());
		tvStatus.setText(promotionTrip.getStatus());
		tvFrom.setText(promotionTrip.getFromCity() + " "
				+ promotionTrip.getFromAddress());
		tvTo.setText(promotionTrip.getToCity() + " "
				+ promotionTrip.getToAddress());
		if (promotionTrip.getStatus().equalsIgnoreCase(
				PromotionTripStatus.NEW_TRIP)) {
			if (promotionTrip.getTime().compareTo(Utils.getDateTime()) >= 1) {
				tvStatus.setText(activity.getString(R.string.opening));
			} else {
				tvStatus.setText(activity.getString(R.string.out_of_date));
			}
		}
		if (promotionTrip.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.REJECT)) {
			tvStatus.setText(activity.getString(R.string.reject));
		}
		if (promotionTrip.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.REGISTERED)) {
			tvStatus.setText(activity.getString(R.string.registered));
		}
		if (promotionTrip.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.ACCEPT)) {
			tvStatus.setText(activity.getString(R.string.accept));
		}

		Button delete = (Button) v.findViewById(R.id.bt_delete);
		handler = new DatabaseHandler(v.getContext());
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v2) {
				new DeletePromotionTripBO(v.getContext(), position)
						.execute(list.get(position).getId());
			}
		});
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		View v = LayoutInflater.from(activity).inflate(
				R.layout.item_promotion_trip, null);
		SwipeLayout swipeLayout = (SwipeLayout) v
				.findViewById(getSwipeLayoutResourceId(position));
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				YoYo.with(Techniques.Shake).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.bt_delete));
				// YoYo.with(Techniques.Tada).duration(500).delay(100)
				// .playOn(layout.findViewById(R.id.bt_edit));

			}
		});
		swipeLayout
				.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
					@Override
					public void onDoubleClick(SwipeLayout layout,
							boolean surface) {
						Toast.makeText(activity, "DoubleClick",
								Toast.LENGTH_SHORT).show();
					}
				});
		return v;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe_promotion;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public class DeletePromotionTripBO extends AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog pd;
		private int position;

		public DeletePromotionTripBO(Context activity, int position) {
			this.context = activity;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setTitle(context.getString(R.string.delete));
			pd.setMessage(context.getString(R.string.delete_vip_rider));
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
				Toast.makeText(activity, "ERROR", Toast.LENGTH_LONG).show();
			}
		}

		public String postData(String[] params) {
			// Create a new HttpClient and Post Header
			// HttpClient httpclient = new DefaultHttpClient();
			// HttpPost httppost = new HttpPost(
			// Constants.URL.DELETE_PROMOTION_TRIP);
			// try {
			// List<NameValuePair> nameValuePairs = new
			// ArrayList<NameValuePair>();
			// nameValuePairs.add(new BasicNameValuePair("driverId",
			// AppController.getDriverId()));
			// nameValuePairs.add(new BasicNameValuePair("id", params[0]));
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
			return null;
		}

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				if (message != null
						&& message.equalsIgnoreCase(Message.SUCCESS)) {
					PromotionTrip promotionTrip = list.get(position);
					// handler.deletePromotionTrip(promotionTrip.getId());
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

	public void updateListPromotionTrip(
			ArrayList<PromotionTrip> listPromotionTrip) {
		list = listPromotionTrip;
		notifyDataSetChanged();

	}

}
