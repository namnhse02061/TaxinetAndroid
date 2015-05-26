/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.co.taxinet.mobile.gps;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.Price;
import vn.co.taxinet.mobile.model.PromotionTrip;
import vn.co.taxinet.mobile.model.PromotionTripRiders;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.MapActivity;
import vn.co.taxinet.mobile.newactivity.PromotionTripRiderItem;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.NotificationType;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripRiderStatus;
import vn.co.taxinet.mobile.utils.Constants.PromotionTripStatus;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	private int numMessagesOne = 0;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				System.out.println(extras.toString());
				DatabaseHandler handler = new DatabaseHandler(
						getApplicationContext());
				String notificationType = intent
						.getStringExtra("notificationType");
				if (notificationType.equalsIgnoreCase(NotificationType.TRIP)) {
					tripNotification(intent, handler);
				}
				if (notificationType
						.equalsIgnoreCase(NotificationType.PROMOTION_TRIP)) {
					promotionTripNotification(intent, handler);
				}
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void promotionTripNotification(Intent intent,
			DatabaseHandler handler) {
		PromotionTripRiders tripRiders = new PromotionTripRiders();
		tripRiders.setStatus(intent.getStringExtra("status"));
		if (tripRiders.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.NEWREQUEST)) {
			Rider rider = new Rider();
			rider.setFirstName(intent.getStringExtra("firstName"));
			rider.setLastName(intent.getStringExtra("lastName"));
			rider.setPhone(intent.getStringExtra("phone"));
			rider.setType(intent.getStringExtra("type"));
			rider.setId(intent.getStringExtra("riderId"));

			PromotionTrip promotionTrip = new PromotionTrip();
			promotionTrip.setId(intent.getStringExtra("promotionTripId"));

			tripRiders.setFromAddress(intent.getStringExtra("fromAddress"));
			tripRiders.setFromCity(intent.getStringExtra("fromCity"));
			tripRiders.setNumberOfSeats(Integer.parseInt(intent
					.getStringExtra("numberOfSeats")));
			tripRiders.setStartTime(intent.getStringExtra("time"));
			tripRiders.setToAddress(intent.getStringExtra("toAddress"));
			tripRiders.setToCity(intent.getStringExtra("toCity"));
			tripRiders.setStatus(PromotionTripRiderStatus.NEWREQUEST);

			tripRiders.setPromotionTrip(promotionTrip);
			tripRiders.setRider(rider);
			handler.addPromotrionTripRiders(tripRiders);

		}
		if (tripRiders.getStatus().equalsIgnoreCase(PromotionTripStatus.CANCEL)) {
			Rider rider = new Rider();
			rider.setId(intent.getStringExtra("riderId"));
			rider.setFirstName(intent.getStringExtra("firstName"));
			rider.setLastName(intent.getStringExtra("lastName"));

			PromotionTrip promotionTrip = new PromotionTrip();
			promotionTrip.setId(intent.getStringExtra("promotionTripId"));

			tripRiders.setPromotionTrip(promotionTrip);
			tripRiders.setRider(rider);

			handler.updatePromotionTripDetails(rider.getId(),
					promotionTrip.getId(), PromotionTripRiderStatus.CANCEL);
		}
		promotionTripNotification(tripRiders);
		Intent intent2 = new Intent(
				Constants.BroadcastAction.PROMOTION_TRIP_REQUEST);
		intent2.putExtra(Constants.PROMOTION_TRIP, tripRiders);
		sendBroadcast(intent2);
	}

	public void tripNotification(Intent intent, DatabaseHandler handler) {
		Trip trip = new Trip();
		trip.setTripId(intent.getStringExtra("id"));
		trip.setStatus(intent.getStringExtra("status"));
		if (trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {

			trip.setFromLongitude(Double.parseDouble(intent
					.getStringExtra("startLongitude")));
			trip.setFromLatitude(Double.parseDouble(intent
					.getStringExtra("startLatitude")));
			trip.setToLongitude(Double.parseDouble(intent
					.getStringExtra("stopLongitude")));
			trip.setToLatitude(Double.parseDouble(intent
					.getStringExtra("stopLatitude")));
			trip.setPaymenMethod(intent.getStringExtra("paymentMethod"));
			trip.setFromAddress(intent.getStringExtra("fromAddress"));
			trip.setToAddress(intent.getStringExtra("toAddress"));
			trip.setStartTime(Utils.getDateTime());

			Rider rider = new Rider();
			rider.setId(intent.getStringExtra("riderId"));
			rider.setFirstName(intent.getStringExtra("firstName"));
			rider.setLastName(intent.getStringExtra("lastName"));
			rider.setPhone(intent.getStringExtra("phone"));
			rider.setImage(intent.getStringExtra("image"));
			rider.setType(intent.getStringExtra("riderType"));
			trip.setRider(rider);

			Price price = new Price();
			price.setOpenKM(Double.parseDouble(intent.getStringExtra("openKM")));
			price.setOpenKMPrice(Double.parseDouble(intent
					.getStringExtra("openKMPrice")));
			price.setFirstKM(Double.parseDouble(intent
					.getStringExtra("firstKM")));
			price.setFirstPrice(Double.parseDouble(intent
					.getStringExtra("firstKMPrice")));
			price.setNextKM(Double.parseDouble(intent.getStringExtra("nextKM")));
			price.setNextKMPrice(Double.parseDouble(intent
					.getStringExtra("nextKMPrice")));
			price.setWaitingPrice(Double.parseDouble(intent
					.getStringExtra("watingPrice")));
			savePrice(price);
			trip.setPrice(price);
			handler.addTrip(trip);
			handler.addRider(rider);
			tripNotification(trip);
		}

		if (!trip.getStatus().equalsIgnoreCase(TripStatus.NEW_TRIP)) {
			Trip trip2 = handler.findTripById(trip.getTripId());
			if (trip2 != null) {
				trip2.setStatus(TripStatus.CANCELLED);
				handler.updateTrip(trip2);
			}
		}

		Intent intent2 = new Intent(Constants.BroadcastAction.TRIP_REQUEST);
		intent2.putExtra(Constants.TRIP, trip);
		sendBroadcast(intent2);
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void tripNotification(Trip trip) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this);
		NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle();
		mBuilder.setNumber(++numMessagesOne);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setContentTitle(trip.getRider().getFirstName() + " "
				+ trip.getRider().getLastName());
		mBuilder.setContentText(trip.getRider().getPhone());
		mBuilder.setLargeIcon(getBitmapFromURL(trip.getRider().getImage()));
		mBuilder.setStyle(bigPicStyle);
		mBuilder.setVibrate(new long[] { 0, 500, 500, 500, 0 });
		Uri uri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(uri);

		Intent resultIntent = new Intent(this, MapActivity.class);
		resultIntent.putExtra(Constants.TRIP, trip);

		// This ensures that navigating backward from the Activity leads out of
		// the app to Home page
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent
		stackBuilder.addParentStack(MapActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_ONE_SHOT // can only be used once
				);
		// start the activity when the user clicks the notification text
		mBuilder.setContentIntent(resultPendingIntent);

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	private void promotionTripNotification(PromotionTripRiders tripRiders) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this);
		mBuilder.setNumber(++numMessagesOne);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		mBuilder.setContentTitle(tripRiders.getRider().getFirstName() + " "
				+ tripRiders.getRider().getLastName());

		if (tripRiders.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.ACCEPT)) {
			mBuilder.setContentText(getString(R.string.response_register_promotrion));
		}

		if (tripRiders.getStatus().equalsIgnoreCase(
				PromotionTripRiderStatus.REJECT)) {
			mBuilder.setContentText(getString(R.string.response_cancel_promotrion));
		}
		mBuilder.setVibrate(new long[] { 0, 500, 500, 500, 0 });
		Uri uri = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(uri);

		Intent resultIntent = new Intent(this, PromotionTripRiderItem.class);
		resultIntent.putExtra(Constants.PROMOTION_TRIP, tripRiders);

		// This ensures that navigating backward from the Activity leads out of
		// the app to Home page
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Adds the back stack for the Intent
		stackBuilder.addParentStack(PromotionTripRiderItem.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_ONE_SHOT // can only be used once
				);
		// start the activity when the user clicks the notification text
		mBuilder.setContentIntent(resultPendingIntent);

		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	public Bitmap getBitmapFromURL(String strURL) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void savePrice(Price price) {
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("OpenKM", String.valueOf(price.getOpenKM()));
		editor.putLong("OpenKMPrice", (long) price.getOpenKMPrice());
		editor.putLong("FirstKM", (long) price.getFirstKM());
		editor.putLong("FirstKMPrice", (long) price.getFirstPrice());
		editor.putLong("NextKM", (long) price.getNextKM());
		editor.putLong("NextKMPrice", (long) price.getNextKMPrice());
		editor.commit();
	}
}
