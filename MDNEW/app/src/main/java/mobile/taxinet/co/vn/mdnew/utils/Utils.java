package mobile.taxinet.co.vn.mdnew.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SimpleDateFormat")
public class Utils {

	private static Pattern pattern;
	private static Matcher matcher;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_PATTERN = "^[0-9]{10,11}$";

	/**
	 * @author Hieu-Gie
	 * 
	 *         Notifies UI to display a message.
	 *         <p>
	 *         This method is defined in the common helper because it's used
	 *         both by the UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(Constants.BroadcastAction.DISPLAY_MESSAGE);
		intent.putExtra(Constants.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	/**
	 * @author Hieu-Gie
	 * 
	 *         Notifies UI to display a REQUEST .
	 *         <p>
	 *         This method is defined in the common helper because it's used
	 *         both by the UI and the background service.
	 * 
	 * @param context
	 * @param riderImage
	 * @param longitude
	 * @param latitude
	 * @param price
	 */
	public static void displayRequest(Context context, String riderImage,
			String riderName, String longitude, String latitude) {
		Intent intent = new Intent(Constants.BroadcastAction.TRIP_REQUEST);
		intent.putExtra(Constants.DRIVER_IMAGE, riderImage);
		intent.putExtra(Constants.NAME, riderName);
		intent.putExtra(Constants.LONGITUDE, longitude);
		intent.putExtra(Constants.LATITUDE, latitude);
		context.sendBroadcast(intent);
	}

	/**
	 * @author Hieu-Gie
	 * 
	 *         Checking for all possible internet providers
	 * 
	 * @param _context
	 * @return
	 */
	public static boolean isConnectingToInternet(Context _context) {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	/**
	 * Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validateEmail(final String hex) {
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(hex);
		return matcher.matches();

	}

	/**
	 * @author Hieu-Gie
	 * 
	 *         Validate hex with regular expression
	 * 
	 * @param hex
	 *            hex for validation
	 * @returntrue valid hex, false invalid hex
	 */
	public static boolean validatePhoneNumber(final String hex) {
		pattern = Pattern.compile(PHONE_PATTERN);
		matcher = pattern.matcher(hex);
		return matcher.matches();

	}

	// -------------------common methods---------------------------------//
	public static String getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String getYesterdayDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return dateFormat.format(cal.getTime());
	}

	public static HashMap<String, String> getAddress(Context context,
			double lat, double lon) {
		if (isConnectingToInternet(context)) {
			// Geocoder geocoder = new Geocoder(context, context.getResources()
			// .getConfiguration().locale);
			Geocoder geocoder = new Geocoder(context, new Locale("en"));
			HashMap<String, String> address = null;
			try {
				List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
				if (addresses != null && addresses.size() > 0) {
					System.out.println(addresses);
					address = new HashMap<String, String>(3);
					Address returnedAddress = addresses.get(0);

					StringBuilder strReturnedAddress = new StringBuilder();
					address.put(
							"country",
							returnedAddress.getAddressLine(
									returnedAddress.getMaxAddressLineIndex())
									.toString());
					address.put(
							"city",
							returnedAddress
									.getAddressLine(
											returnedAddress
													.getMaxAddressLineIndex() - 1)
									.toString());
					for (int i = 0; i < returnedAddress
							.getMaxAddressLineIndex() - 1; i++) {
						if (!returnedAddress.getAddressLine(i)
								.equalsIgnoreCase("Unnamed Rd")) {
							strReturnedAddress.append(
									returnedAddress.getAddressLine(i)).append(
									" ");
						}

					}
					address.put("address", strReturnedAddress.toString());

					return address;
				} else {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;

	}
	
	public static String convertDateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dates = format.format(date);
		return dates;
	}

}
