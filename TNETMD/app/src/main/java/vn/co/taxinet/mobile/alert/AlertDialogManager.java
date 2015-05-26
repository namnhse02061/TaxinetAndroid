package vn.co.taxinet.mobile.alert;

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

import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.LogoutBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.PromotionTripRiders;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.MapActivity;
import vn.co.taxinet.mobile.newactivity.PromotionTripActivity;
import vn.co.taxinet.mobile.newactivity.PromotionTripRiderItem;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.LruBitmapCache;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

public class AlertDialogManager {

	private Trip trip;
	private DatabaseHandler handler;
	private Dialog dialog;

	public static void showCancelRequestAlert(Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.cancel_request_title));
		builder.setMessage(context.getString(R.string.cancel_request_message));
		builder.setPositiveButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showInternetConnectionErrorAlert(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context
				.getString(R.string.no_internet_connection_title));
		builder.setMessage(context
				.getString(R.string.no_internet_connection_message));
		builder.setPositiveButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showCustomAlert(Context context, String title,
			String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showCustomAlert(Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showLogoutAlert(final Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.logout));
		builder.setMessage(context.getString(R.string.logout_message));
		builder.setPositiveButton(context.getString(R.string.logout),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						new LogoutBO(context).execute();
					}
				});
		builder.setNegativeButton(context.getString(R.string.cancel),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showRegisterPromotionAlert(final Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(R.string.register_promotion_trip));
		builder.setPositiveButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent it = new Intent(context,
								PromotionTripActivity.class);
						context.startActivity(it);
						context.finish();
					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showCannotConnectToServerAlert(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.error));
		builder.setMessage(context.getString(R.string.cannot_connect_to_server));
		builder.setPositiveButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showCancelPromotionTripAlert(final Context context,
			final PromotionTripRiders tripRiders) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(
				R.string.cancel_promotion_trip_message, tripRiders.getRider()
						.getFirstName()
						+ " "
						+ tripRiders.getRider().getLastName()));

		builder.setNegativeButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public static void showRegisterPromotionTripAlert(final Context context,
			final PromotionTripRiders tripRiders) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(
				R.string.register_promotion_trip_message, tripRiders.getRider()
						.getFirstName()
						+ " "
						+ tripRiders.getRider().getLastName()));
		builder.setPositiveButton(context.getString(R.string.show),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent it = new Intent(context,
								PromotionTripRiderItem.class);
						it.putExtra(Constants.PROMOTION_TRIP, tripRiders);
						context.startActivity(it);
					}
				});
		builder.setNegativeButton(context.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}

	public void showNewTripRequestAlert(final Activity activity, final Trip trip) {
		this.trip = trip;
		handler = new DatabaseHandler(activity);
		dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.new_trip_request);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		TextView phone = (TextView) dialog.findViewById(R.id.tv_phone);
		TextView name = (TextView) dialog.findViewById(R.id.tv_name);

		if (trip.getRider().getImage() != null) {
			ImageLoader.ImageCache imageCache = new LruBitmapCache();
			ImageLoader imageLoader = new ImageLoader(
					Volley.newRequestQueue(activity), imageCache);
			NetworkImageView imgAvatar = (NetworkImageView) dialog
					.findViewById(R.id.iv_image);
			imgAvatar.setImageUrl(trip.getRider().getImage().toString(),
					imageLoader);
		}

		name.setText(trip.getRider().getFirstName() + " "
				+ trip.getRider().getLastName());
		phone.setText(trip.getRider().getPhone());

		Button show = (Button) dialog.findViewById(R.id.bt_show);
		show.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(activity, MapActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
		});
		Button reject = (Button) dialog.findViewById(R.id.bt_reject);
		reject.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Utils.isConnectingToInternet(activity)) {
					String params[] = { trip.getTripId(),
							Constants.TripStatus.CANCELLED };
					new ResponsRequest(activity).execute(params);
				} else {
					showInternetConnectionErrorAlert(activity);
				}

			}
		});
		dialog.show();
	}

	public class ResponsRequest extends AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog pd;

		public ResponsRequest(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setTitle(context.getString(R.string.response_request));
			pd.setMessage(context.getString(R.string.response_request_message));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return responseRequest(params);

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
				showCannotConnectToServerAlert(context);
			}
		}

		public String responseRequest(String params[]) {
			// Create a new HttpClient and Post Header

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.URL.UPDATE_TRIP);
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("requestId",
						params[0]));
				nameValuePairs.add(new BasicNameValuePair("status", params[1]));
				nameValuePairs.add(new BasicNameValuePair("userId",
						AppController.getDriverId()));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				// Execute HTTP Post Request
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
				if (message.equalsIgnoreCase(Constants.TripStatus.CANCELLED)) {
					AlertDialogManager.showCancelRequestAlert(context);
					trip.setStatus(TripStatus.CANCELLED);
					handler.updateTrip(trip);
					dialog.dismiss();
				}

			} catch (JSONException e) {
				Toast.makeText(context, context.getString(R.string.error),
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
