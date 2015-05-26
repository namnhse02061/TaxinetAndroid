package vn.co.taxinet.mobile.newactivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Company;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CompanyActivity extends Activity {

	private DatabaseHandler handler;
	private TextView name, address, city, phone, postalCode, vatNumber;
	private Company company;
	private SwipeRefreshLayout swipeLayout;
	private RelativeLayout companyInfo;
	private ActionBar actionBar;
	private TextView noRecord;
	private BroadcastReceiver receiver, receiverTrip;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conpany_info);
		initalize();

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (Utils.isConnectingToInternet(mContext)) {
					swipeLayout.setRefreshing(true);
					new CompanyBO(false).execute();
				} else {
					AlertDialogManager
							.showInternetConnectionErrorAlert(mContext);
				}

			}
		});

	}

	public void initalize() {
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
		name = (TextView) findViewById(R.id.tv_name);
		address = (TextView) findViewById(R.id.tv_address);
		city = (TextView) findViewById(R.id.tv_city);
		phone = (TextView) findViewById(R.id.tv_phone);
		postalCode = (TextView) findViewById(R.id.tv_zip_code);
		vatNumber = (TextView) findViewById(R.id.tv_vat);
		noRecord = (TextView) findViewById(R.id.tv_no_record);
		companyInfo = (RelativeLayout) findViewById(R.id.company_info);
		handler = new DatabaseHandler(this);
		company = handler.findCompany();
		if (company != null) {
			name.setText(company.getName());
			address.setText(company.getAddress());
			city.setText(company.getCity());
			phone.setText(company.getPhone());
			postalCode.setText(company.getPostalCode());
			vatNumber.setText(company.getVatNumeber());
			noRecord.setVisibility(View.GONE);
		} else {
			companyInfo.setVisibility(View.GONE);
			noRecord.setVisibility(View.VISIBLE);
			if (Utils.isConnectingToInternet(mContext)) {
				new CompanyBO(false).execute();
			} else {
				AlertDialogManager.showInternetConnectionErrorAlert(mContext);
			}
		}

		phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"
						+ phone.getText().toString()));
				startActivity(callIntent);
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
					manager.showNewTripRequestAlert(CompanyActivity.this, trip);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(mContext);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiverTrip);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	public class CompanyBO extends AsyncTask<String, Void, String> {
		private boolean showProgressDialog;
		private ProgressDialog pd;

		public CompanyBO(boolean showProgressDialog) {
			this.showProgressDialog = showProgressDialog;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(mContext);
			if (showProgressDialog) {
				pd.setMessage(mContext.getString(R.string.updating));
				pd.show();
			}

		}

		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL.GET_COMPANY + "?id="
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
			if (pd.isShowing()) {
				pd.dismiss();
			}
			swipeLayout.setRefreshing(false);
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCannotConnectToServerAlert(mContext);
			}
		}

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				Company company = new Company();
				company.setName(jsonObject.getString("name"));
				company.setAddress(jsonObject.getString("address"));
				company.setCity(jsonObject.getString("city"));
				company.setPhone(jsonObject.getString("phone"));
				company.setPostalCode(jsonObject.getString("postalCode"));
				company.setVatNumeber(jsonObject.getString("vatNumber"));
				if (CompanyActivity.this.company != null) {
					if (company.compareTo(CompanyActivity.this.company) != 1) {
						handler.deleteCompany();
						handler.addCompany(company);
						name.setText(company.getName());
						address.setText(company.getAddress());
						city.setText(company.getCity());
						phone.setText(company.getPhone());
						postalCode.setText(company.getPostalCode());
						vatNumber.setText(company.getVatNumeber());

					}
				} else {
					handler.addCompany(company);
					name.setText(company.getName());
					address.setText(company.getAddress());
					city.setText(company.getCity());
					phone.setText(company.getPhone());
					postalCode.setText(company.getPostalCode());
					vatNumber.setText(company.getVatNumeber());
				}
				Toast.makeText(getApplicationContext(),
						getString(R.string.update_complete), Toast.LENGTH_LONG)
						.show();
				companyInfo.setVisibility(View.VISIBLE);
				noRecord.setVisibility(View.GONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_update, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.update) {
			if (Utils.isConnectingToInternet(mContext)) {
				new CompanyBO(true).execute();
			} else {
				AlertDialogManager.showInternetConnectionErrorAlert(mContext);
			}

		}
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
}
