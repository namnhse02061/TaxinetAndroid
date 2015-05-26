package vn.co.taxinet.mobile.newactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.adapter.PromotionAdatpter;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.PromotionTripRiders;
import vn.co.taxinet.mobile.model.Rider;
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

import com.daimajia.swipe.util.Attributes;

public class PromotionTripActivity extends Activity {

	private ListView mListView;
	private ArrayList<PromotionTrip> listPromotionTrip;
	private Context mContext = this;
	private SwipeRefreshLayout swipeLayout;
	private TextView tvNoRecord;
	private ActionBar actionBar;
	private DatabaseHandler handler;
	private PromotionAdatpter mAdapter;
	private GetListPromotionTripBO bo;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initialize();

		mAdapter = new PromotionAdatpter(this, listPromotionTrip);
		mListView.setAdapter(mAdapter);
		mAdapter.setMode(Attributes.Mode.Single);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// ((SwipeLayout) (mListView.getChildAt(position
				// - mListView.getFirstVisiblePosition()))).open(true);
				Intent intent = new Intent(PromotionTripActivity.this,
						PromotionTripRidersActivity.class);
				intent.putExtra("promotionId", listPromotionTrip.get(position)
						.getId());
				startActivity(intent);
				finish();
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
					bo = new GetListPromotionTripBO(false);
					bo.execute();
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}
			}
		});
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

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		listPromotionTrip = new ArrayList<PromotionTrip>();
		tvNoRecord = (TextView) findViewById(R.id.tv_no_record);
		mListView = (ListView) findViewById(R.id.list);
		handler = new DatabaseHandler(this);
		listPromotionTrip = handler.getListPromotionTrip();
		if (listPromotionTrip.size() == 0) {
			tvNoRecord.setVisibility(View.VISIBLE);
			if (Utils.isConnectingToInternet(mContext)) {
				bo = new GetListPromotionTripBO(false);
				bo.execute();
			}
		} else {
			tvNoRecord.setVisibility(View.GONE);
		}
	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
					AlertDialogManager manager = new AlertDialogManager();
					manager.showNewTripRequestAlert(PromotionTripActivity.this,
							trip);
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
		getMenuInflater().inflate(R.menu.menu_promotion_trip, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.register) {
			Intent it = new Intent(PromotionTripActivity.this,
					PickAddressActivity.class);
			startActivity(it);
			finish();
		}
		if (item.getItemId() == R.id.update) {
			new GetListPromotionTripBO(true).execute();
		}
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public class GetListPromotionTripBO extends AsyncTask<String, Void, String> {

		private boolean showProgressDialog;
		private ProgressDialog pd;

		public GetListPromotionTripBO(boolean showProgressDialog) {
			this.showProgressDialog = showProgressDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showProgressDialog) {
				pd = new ProgressDialog(mContext);
				pd.setMessage(mContext.getString(R.string.wait_message));
				pd.show();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL.GET_LIST_PROMOTION_TRIP + "?id="
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
						.showCannotConnectToServerAlert(PromotionTripActivity.this);
			}
		}

		public void parseJson(String response) {
			try {
				handler.deletePromotionTrip();
				System.out.println(response);
				JSONArray jsonArray = new JSONArray(response);
				listPromotionTrip = new ArrayList<PromotionTrip>();
				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jsonObject = jsonArray.getJSONObject(i);
					PromotionTrip promotionTrip = new PromotionTrip();
					promotionTrip.setFee(Double.parseDouble(jsonObject
							.getString("fee")));
					promotionTrip.setFromAddress(jsonObject
							.getString("fromAddress"));
					promotionTrip.setFromCity(jsonObject.getString("fromCity"));
					promotionTrip.setId(jsonObject.getString("id"));
					promotionTrip.setFromLatitude(Double.parseDouble(jsonObject
							.getString("fromLatitude")));
					promotionTrip
							.setFromLongitude(Double.parseDouble(jsonObject
									.getString("fromLongitude")));
					promotionTrip.setStatus(jsonObject.getString("status"));
					promotionTrip.setToLatitude(Double.parseDouble(jsonObject
							.getString("toLatitude")));
					promotionTrip.setToLongitude(Double.parseDouble(jsonObject
							.getString("toLongitude")));
					promotionTrip.setTime(jsonObject.getString("time"));
					promotionTrip.setToAddress(jsonObject
							.getString("toAddress"));
					promotionTrip.setToCity(jsonObject.getString("toCity"));
					promotionTrip.setCapacity(Integer.parseInt(jsonObject
							.getString("capacity")));

					JSONArray jsonArray2 = jsonObject
							.getJSONArray("promotionTripRiders");
					for (int j = 0; j < jsonArray2.length(); j++) {

						JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
						PromotionTripRiders tripRiders = new PromotionTripRiders();
						tripRiders.setFromAddress(jsonObject2
								.getString("fromAddress"));
						tripRiders.setFromCity(jsonObject2
								.getString("fromCity"));
						tripRiders.setNumberOfSeats(jsonObject2
								.getInt("numberOfSeats"));
						tripRiders.setToAddress(jsonObject2
								.getString("toAddress"));
						tripRiders.setToCity(jsonObject2.getString("toCity"));
						tripRiders.setStatus(jsonObject2.getString("status"));

						Format formatter = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = new Date(jsonObject2.getLong("timeStart"));
						String startTime = formatter.format(date);
						tripRiders.setStartTime(startTime);
						tripRiders.setToCity(jsonObject2.getString("toCity"));
						tripRiders.setPromotionTrip(promotionTrip);
						Rider rider = new Rider();
						rider.setFirstName(jsonObject2.getJSONObject("rider")
								.getString("firstName"));
						rider.setLastName(jsonObject2.getJSONObject("rider")
								.getString("lastName"));
						rider.setId(jsonObject2.getJSONObject("rider")
								.getString("riderId"));
						rider.setPhone(jsonObject2.getJSONObject("rider")
								.getString("phone"));
						rider.setType(jsonObject2.getJSONObject("rider")
								.getString("type"));

						tripRiders.setRider(rider);
						handler.addRider(rider);
						handler.addPromotrionTripRiders(tripRiders);
					}

					handler.addPromotionTrip(promotionTrip);
					listPromotionTrip.add(promotionTrip);
				}
				if (listPromotionTrip.size() == 0) {
					tvNoRecord.setVisibility(View.VISIBLE);
				} else {
					tvNoRecord.setVisibility(View.GONE);
				}
				Toast.makeText(mContext,
						mContext.getString(R.string.update_complete),
						Toast.LENGTH_LONG).show();
				mAdapter.updateListPromotionTrip(listPromotionTrip);
			} catch (JSONException e) {
				Toast.makeText(mContext,
						getString(R.string.error) + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
