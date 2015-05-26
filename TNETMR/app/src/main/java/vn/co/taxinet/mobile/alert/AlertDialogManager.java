package vn.co.taxinet.mobile.alert;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.bo.LogoutBO;
import vn.co.taxinet.mobile.newactivity.LoginActivity;
import vn.co.taxinet.mobile.newactivity.MapActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;

public class AlertDialogManager {

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
	
	public static void showCompleteResetPass(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.reset_password));
		builder.setMessage(activity.getString(R.string.reset_password_successfully_message));
		builder.setPositiveButton(activity.getString(R.string.close),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent i = new Intent(activity,LoginActivity.class);
						activity.startActivity(i);
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

	public static void showRegisterSuccess(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getString(R.string.success));
		builder.setMessage(activity.getString(R.string.register_success));
		builder.setPositiveButton(activity.getString(R.string.login),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent it = new Intent(activity, LoginActivity.class);
						activity.startActivity(it);
						activity.finish();
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
}
