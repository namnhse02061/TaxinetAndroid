package vn.co.taxinet.mobile.gcm;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HandleMessageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context mContext, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras.getSerializable(Constants.PROMOTION_TRIP) != null) {
			PromotionTrip tripRiders = (PromotionTrip) extras
					.getSerializable(Constants.PROMOTION_TRIP);
			if (tripRiders.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.ACCEPT)) {
				AlertDialogManager
						.showCustomAlert(mContext, mContext.getString(
								R.string.accepted_promotion_trip_message,
								tripRiders.getDriver().getFirstName() + " "
										+ tripRiders.getDriver().getLastName()));
			} else if (tripRiders.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.REJECT)) {
				AlertDialogManager
						.showCustomAlert(mContext, mContext.getString(
								R.string.reject_promotion_trip_message,
								tripRiders.getDriver().getFirstName() + " "
										+ tripRiders.getDriver().getLastName()));
			} else if (tripRiders.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.CANCEL)) {
				AlertDialogManager
						.showCustomAlert(mContext, mContext.getString(
								R.string.cancel_promotion_trip_message,
								tripRiders.getDriver().getFirstName() + " "
										+ tripRiders.getDriver().getLastName()));
			}

		}
	}
}
