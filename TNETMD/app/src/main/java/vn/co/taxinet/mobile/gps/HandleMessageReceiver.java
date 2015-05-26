package vn.co.taxinet.mobile.gps;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.model.PromotionTripRiders;
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
			PromotionTripRiders tripRiders = (PromotionTripRiders) extras
					.getSerializable(Constants.PROMOTION_TRIP);
			if (tripRiders.getStatus().equalsIgnoreCase(
					PromotionTripRiderStatus.NEWREQUEST)) {
				AlertDialogManager.showRegisterPromotionTripAlert(mContext,
						tripRiders);
			} else {
				AlertDialogManager.showCancelPromotionTripAlert(mContext,
						tripRiders);
			}

		}
	}
}
