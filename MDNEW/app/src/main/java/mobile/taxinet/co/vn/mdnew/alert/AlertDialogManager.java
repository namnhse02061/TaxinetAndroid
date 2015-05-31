package mobile.taxinet.co.vn.mdnew.alert;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import mobile.taxinet.co.vn.mdnew.R;


public class AlertDialogManager {

	private Dialog dialog;
	public static void showCustomAlert(Context context, String title,
									   String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(context.getString(R.string.close),
				new DialogInterface.OnClickListener() {

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
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				});
		builder.setCancelable(false);
		builder.show();
	}
}
