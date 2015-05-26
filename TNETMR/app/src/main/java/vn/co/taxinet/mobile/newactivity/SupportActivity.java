package vn.co.taxinet.mobile.newactivity;

import java.util.List;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.SupportBO;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SupportActivity extends Activity {

	private ActionBar actionBar;
	private BroadcastReceiver receiver, receiverTrip;
	private EditText subject, content;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support);

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
		actionBar.setDisplayShowHomeEnabled(true);
		subject = (EditText) findViewById(R.id.subject);
		content = (EditText) findViewById(R.id.content);
	}

	public void call(View v) {

		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:0437571996"));
		PackageManager manager = getPackageManager();
		List<ResolveInfo> infos = manager.queryIntentActivities(callIntent, 0);
		if (infos.size() > 0) {

			startActivity(callIntent);
		} else {
			AlertDialogManager.showCustomAlert(mContext,
					getString(R.string.error),
					getString(R.string.can_not_make_call));
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
		getMenuInflater().inflate(R.menu.menu_support, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.send) {
			sendEmail();
		}

		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void sendEmail() {
		if (TextUtils.isEmpty(subject.getText().toString())) {
			AlertDialogManager
					.showCustomAlert(mContext, getString(R.string.error),
							getString(R.string.null_subject));
		} else if (TextUtils.isEmpty(content.getText().toString())) {
			AlertDialogManager
					.showCustomAlert(mContext, getString(R.string.error),
							getString(R.string.null_content));
		} else {
			new SupportBO(mContext).checkInfo(subject.getText().toString(),
					content.getText().toString());
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			
		}
	}
}
