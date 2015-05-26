package vn.co.taxinet.mobile.newactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.adapter.PaymentAdapter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Payment;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.Constants.URL;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;

public class PaymentActivity extends Activity {

	private Context mContext = this;
	private ArrayList<Payment> listPayment;
	private ListView mListView;
	private PaymentAdapter mAdapter;
	private DatabaseHandler handler;
	private SwipeRefreshLayout swipeLayout;
	private TextView tvNoRecord;
	private ActionBar actionBar;
	private BroadcastReceiver receiver,receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initialize();
	}

	public void initialize() {
		IntentFilter filter = new IntentFilter(
				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new HandleMessageReceiver();
		registerReceiver(receiver, filter);

		IntentFilter filter2 = new IntentFilter(
				Constants.BroadcastAction.TRIP_REQUEST);
		filter2.addCategory(Intent.CATEGORY_DEFAULT);
		receiverTrip = new TripReceiver();
		registerReceiver(receiverTrip, filter2);
		
		mListView = (ListView) findViewById(R.id.list);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		tvNoRecord = (TextView) findViewById(R.id.tv_no_record);
		handler = new DatabaseHandler(this);
		listPayment = handler.getListPayment();
		if (listPayment == null) {
			listPayment = new ArrayList<Payment>();
			tvNoRecord.setVisibility(View.VISIBLE);
			new GetPaymentrBO(true).execute();
		} else {
			tvNoRecord.setVisibility(View.GONE);
		}
		mAdapter = new PaymentAdapter(this, listPayment);
		mListView.setAdapter(mAdapter);
		mAdapter.setMode(Attributes.Mode.Single);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				((SwipeLayout) (mListView.getChildAt(position
						- mListView.getFirstVisiblePosition()))).open(true);
			}
		});
		mListView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.e("ListView", "OnTouch");
				return false;
			}
		});
		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						Toast.makeText(mContext, "OnItemLongClickListener",
								Toast.LENGTH_SHORT).show();
						return false;
					}
				});
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.e("ListView", "onScrollStateChanged");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mListView
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Log.e("ListView", "onItemSelected:" + position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						Log.e("ListView", "onNothingSelected:");
					}
				});
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (Utils.isConnectingToInternet(mContext)) {
					new GetPaymentrBO(false).execute();
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}

			}
		});

	}
	

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
					AlertDialogManager manager = new AlertDialogManager();
					manager.showNewTripRequestAlert(
							PaymentActivity.this, trip);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(mContext);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_update, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		if (item.getItemId() == R.id.update) {
			new GetPaymentrBO(true).execute();
		}
		return super.onOptionsItemSelected(item);
	}

	public class GetPaymentrBO extends AsyncTask<String, Void, String> {

		private boolean showProgressDialog;
		private ProgressDialog pd;

		public GetPaymentrBO(boolean showProgressDialog) {
			this.showProgressDialog = showProgressDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showProgressDialog) {
				pd = new ProgressDialog(mContext);
				pd.setMessage(mContext.getString(R.string.update));
				pd.setCancelable(false);
				pd.show();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL.GET_PAYMENT
					+ AppController.getDriverId());
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				return stringBuilder.toString();

			} catch (ClientProtocolException cpe) {
			} catch (IOException ioe) {
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (showProgressDialog) {
				if (pd.isShowing()) {
					pd.dismiss();
				}
			}
			swipeLayout.setRefreshing(false);
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager
						.showCannotConnectToServerAlert(PaymentActivity.this);
			}
		}

		public void parseJson(String response) {
			try {
				System.out.println(response);
				handler.deletePayment();
				JSONArray jsonArray = new JSONArray(response);
				listPayment = new ArrayList<Payment>();
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);
					Payment payment = new Payment();
					payment.setBankName(jsonObject.getString("bankName"));
					payment.setCardNumber(jsonObject.getString("cardNumber"));
					payment.setCvv(jsonObject.getString("cvv"));
					payment.setExpiredMonth(jsonObject
							.getString("expiredMonth"));
					payment.setExpiredYear(jsonObject.getString("expiredYear"));
					payment.setId(jsonObject.getInt("id"));
					payment.setType(jsonObject.getString("type"));
					handler.addPayment(payment);
					listPayment.add(payment);
					handler.addPayment(payment);
				}
				Toast.makeText(mContext, getString(R.string.update_complete),
						Toast.LENGTH_LONG).show();
				if (listPayment.size() == 0) {
					tvNoRecord.setVisibility(View.VISIBLE);
				} else {
					tvNoRecord.setVisibility(View.GONE);
				}
				mAdapter.updateRiderList(listPayment);
			} catch (JSONException e) {
				Toast.makeText(mContext, getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
