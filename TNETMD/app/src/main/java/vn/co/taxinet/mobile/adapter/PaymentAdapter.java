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
import vn.co.taxinet.mobile.model.Payment;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.CardType;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.RiderType;
import vn.co.taxinet.mobile.utils.Utils;
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

public class PaymentAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private DatabaseHandler handler;
	private ArrayList<Payment> listPayment;

	public PaymentAdapter(Context mContext, ArrayList<Payment> listPayment) {
		this.mContext = mContext;
		this.listPayment = listPayment;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.item_payment,
				null);
		SwipeLayout swipeLayout = (SwipeLayout) v
				.findViewById(getSwipeLayoutResourceId(position));
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				YoYo.with(Techniques.Tada).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.bt_delete));

			}
		});
		swipeLayout
				.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
					@Override
					public void onDoubleClick(SwipeLayout layout,
							boolean surface) {
						Toast.makeText(mContext, "DoubleClick",
								Toast.LENGTH_SHORT).show();
					}
				});
		return v;
	}

	@Override
	public void fillValues(final int position, final View convertView) {
		TextView bankName = (TextView) convertView
				.findViewById(R.id.tv_bank_name);
		TextView cvv = (TextView) convertView.findViewById(R.id.tv_cvv);
		TextView expiredDate = (TextView) convertView
				.findViewById(R.id.tv_expired_date);
		TextView cardNumber = (TextView) convertView
				.findViewById(R.id.tv_card_number);
		TextView type = (TextView) convertView.findViewById(R.id.type);

		bankName.setText(listPayment.get(position).getBankName());
		cvv.setText(mContext.getString(R.string.Cvv)
				+ listPayment.get(position).getCvv());
		expiredDate.setText(mContext.getString(R.string.expired_date)
				+ listPayment.get(position).getExpiredMonth() + "/"
				+ listPayment.get(position).getExpiredYear());
		cardNumber.setText(mContext.getString(R.string.card_number)
				+ listPayment
						.get(position)
						.getCardNumber()
						.substring(
								listPayment.get(position).getCardNumber()
										.length() - 3,
								listPayment.get(position).getCardNumber()
										.length()));
		if (listPayment.get(position).getType().equals(CardType.CR)) {
			type.setText(mContext.getString(R.string.card_type)
					+ mContext.getString(R.string.credit_card));
		}
		if (listPayment.get(position).getType().equals(CardType.VS)) {
			type.setText(mContext.getString(R.string.card_type)
					+ mContext.getString(R.string.visa));
		}
		if (listPayment.get(position).getType().equals(CardType.MC)) {
			type.setText(mContext.getString(R.string.card_type)
					+ mContext.getString(R.string.master_card));
		}
		Button delete = (Button) convertView.findViewById(R.id.bt_delete);
		handler = new DatabaseHandler(convertView.getContext());
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Utils.isConnectingToInternet(mContext)) {
					new DeletePaymentBO(convertView.getContext(), position)
							.execute(String.valueOf(listPayment.get(position)
									.getId()));
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}

			}
		});

	}

	public void updateRiderList(List<Payment> newlist) {
		listPayment.clear();
		listPayment.addAll(newlist);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listPayment.size();
	}

	@Override
	public Object getItem(int position) {
		return listPayment.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class DeletePaymentBO extends AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog pd;
		private int position;

		public DeletePaymentBO(Context activity, int position) {
			this.context = activity;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setMessage(context.getString(R.string.deleting_payment));
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
				AlertDialogManager.showCustomAlert(mContext,
						mContext.getString(R.string.cannot_connect_to_server));
			}
		}

		public String postData(String[] params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.DELETE_PAYMENT);
			try {
				System.out.println(AppController.getDriverId());

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("driverId",
						AppController.getDriverId()));
				nameValuePairs.add(new BasicNameValuePair("paymentId",
						params[0]));
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
				if (message != null
						&& message.equalsIgnoreCase(Message.SUCCESS)) {
					Payment payment = listPayment.get(position);
					payment.setType(RiderType.StrangerRider);
					handler.deletePaymentById(payment.getId());
					listPayment.remove(position);
					notifyDataSetChanged();
					AlertDialogManager.showCustomAlert(mContext, mContext
							.getString(R.string.delete_payment_successfully));
				}
				if (message.equalsIgnoreCase(Message.FAIL)) {
					AlertDialogManager
							.showCustomAlert(mContext, mContext
									.getString(R.string.can_not_delete_payment));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
