package vn.co.taxinet.mobile.newactivity;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.RegisterPromotionTripBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.TripHistoryActivity.TripReceiver;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RegisterdPromotionTripDetails extends Activity {

	private TextView tvFrom, tvTo, tvName, tvNumberOfSeats, tvPhone, tvStatus,
			tvFee, tvTime;

	private PromotionTrip promotionTrip;
	private Context mContext = this;
	private DatabaseHandler handler;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_promotion_trip_details);
		handler = new DatabaseHandler(mContext);
		Bundle bd = getIntent().getExtras();
		promotionTrip = (PromotionTrip) bd.getSerializable("promotionTrip");
		promotionTrip = handler.findPromotionTripById(promotionTrip.getId());
		initialize();
	}

	private void initialize() {
		tvFrom = (TextView) findViewById(R.id.tv_from);
		tvTo = (TextView) findViewById(R.id.tv_to);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvNumberOfSeats = (TextView) findViewById(R.id.tv_number_of_seats);
		tvPhone = (TextView) findViewById(R.id.tv_phone);
		tvStatus = (TextView) findViewById(R.id.tv_status);
		tvFee = (TextView) findViewById(R.id.tv_fee);
		tvTime = (TextView) findViewById(R.id.tv_time);
		Button register = (Button) findViewById(R.id.bt_register);

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
				PromotionTripRiderStatus.REGISTERED)
				|| promotionTrip.getStatus().equalsIgnoreCase(
						PromotionTripRiderStatus.ACCEPT)) {
			tvStatus.setVisibility(View.VISIBLE);
			if (promotionTrip.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.REJECT)) {
				tvStatus.setText(mContext.getString(R.string.reject));
			}
			if (promotionTrip.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.REGISTERED)) {
				tvStatus.setText(mContext.getString(R.string.registered));
			}
			if (promotionTrip.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.ACCEPT)) {
				tvStatus.setText(mContext.getString(R.string.accept));
			}
			register.setVisibility(View.GONE);
		} else {

			tvStatus.setVisibility(View.GONE);
			register.setVisibility(View.VISIBLE);
		}

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String params[] = { promotionTrip.getId(),
						promotionTrip.getFromCity(),
						promotionTrip.getFromAddress(),
						promotionTrip.getToCity(),
						promotionTrip.getToAddress(),
						String.valueOf(promotionTrip.getCapacity()),
						promotionTrip.getTime() };

				new RegisterPromotionTripBO(RegisterdPromotionTripDetails.this)
						.execute(params);
			}
		});

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
	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKING)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.picking_request_title),
							getString(R.string.picking_request_message));
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.PICKED)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.picked_request_title),
							getString(R.string.picked_request_message));
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.CANCELLED)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.cancel_request_title),
							getString(R.string.cancel_request_message));
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.COMPLETED)) {
					String message = getString(R.string.distance)
							+ trip.getDistance() + " KM\n"
							+ getString(R.string.fee) + ": "
							+ +(int) trip.getFee() + " VND\n"
							+ getString(R.string.completed_request_message);
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.completed_request_title),
							message);
				}
				if (trip.getStatus().equalsIgnoreCase(TripStatus.REJECTED)) {
					AlertDialogManager.showCustomAlert(mContext,
							getString(R.string.reject_request_title),
							getString(R.string.reject_request_message));
				}
			}
		}
	}

	public class HandleMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.PROMOTION_TRIP) != null) {
				PromotionTrip tripRiders = (PromotionTrip) extras
						.getSerializable(Constants.PROMOTION_TRIP);
				if (tripRiders.getStatus().equalsIgnoreCase(
						PromotionTripRiderStatus.ACCEPT)) {
					AlertDialogManager.showCustomAlert(mContext, mContext
							.getString(
									R.string.accepted_promotion_trip_message,
									tripRiders.getDriver().getFirstName()
											+ " "
											+ tripRiders.getDriver()
													.getLastName()));
					tvStatus.setText(getString(R.string.accepted));
				} else if (tripRiders.getStatus().equalsIgnoreCase(
						PromotionTripRiderStatus.REJECT)) {
					AlertDialogManager.showCustomAlert(mContext, mContext
							.getString(R.string.reject_promotion_trip_message,
									tripRiders.getDriver().getFirstName()
											+ " "
											+ tripRiders.getDriver()
													.getLastName()));
					tvStatus.setText(getString(R.string.reject));
				} else if (tripRiders.getStatus().equalsIgnoreCase(
						PromotionTripRiderStatus.CANCEL)) {
					AlertDialogManager.showCustomAlert(mContext, mContext
							.getString(R.string.cancel_promotion_trip_message,
									tripRiders.getDriver().getFirstName()
											+ " "
											+ tripRiders.getDriver()
													.getLastName()));
					tvStatus.setText(getString(R.string.cancel));
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

}
