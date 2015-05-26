package vn.co.taxinet.mobile.newactivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.LogoutBO;
import vn.co.taxinet.mobile.bo.ProfileBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gcm.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.newactivity.ListRegisteredPromotionTripActivity.TripReceiver;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Utils;
import vn.co.taxinet.mobile.utils.Constants.LanguageCode;
import vn.co.taxinet.mobile.utils.Constants.TripStatus;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ProfileActivity extends Activity {

	private EditText mEmail, mPhone, mFirstName, mLastName;
	private MenuItem mSaveMenu, mEditMenu, mCancelMenu;
	private String email, phone, firstName, lastName;
	private ProfileBO bo;
	private ActionBar actionBar;
	private DatabaseHandler handler;
	private boolean editting = false;
	private Button mPassword;
	private TextView addHomeAdd, addWorkAdd;
	private Rider rider;
	private Context mContext = this;
	private ImageView profileImage;
	private Spinner spLanguage;
	private int iCurrentSelection;
	private BroadcastReceiver receiver, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		initialize();
		disableEdittext();
		actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_profile, menu);

		mSaveMenu = menu.findItem(R.id.save);
		mEditMenu = menu.findItem(R.id.edit);
		mCancelMenu = menu.findItem(R.id.cancel);
		disableEdit();
		super.onCreateOptionsMenu(menu);
		return true;
	}

	public void initialize() {
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

		mEmail = (EditText) findViewById(R.id.et_email);
		mPhone = (EditText) findViewById(R.id.et_phone);
		mFirstName = (EditText) findViewById(R.id.et_first_name);
		mLastName = (EditText) findViewById(R.id.et_last_name);
		mPassword = (Button) findViewById(R.id.bt_password);
		addHomeAdd = (TextView) findViewById(R.id.home_address);
		addWorkAdd = (TextView) findViewById(R.id.work_address);
		bo = new ProfileBO();
		handler = new DatabaseHandler(this);

		rider = handler.findRider();
		mEmail.setText(rider.getEmail());
		mPhone.setText(rider.getPhone());
		mFirstName.setText(rider.getFirstname());
		mLastName.setText(rider.getLastname());
		addHomeAdd.setText(rider.getHome_detail());
		addWorkAdd.setText(rider.getWork_detail());
		spLanguage = (Spinner) findViewById(R.id.sp_language);
		profileImage = (ImageView) findViewById(R.id.image);
		String locate = loadLocale();
		System.out.println("lco :" + locate);
		if (locate.equalsIgnoreCase(LanguageCode.VI)) {
			spLanguage.setSelection(0);
		} else {
			spLanguage.setSelection(1);
		}
		iCurrentSelection = spLanguage.getSelectedItemPosition();
		spLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View v,
					int position, long duration) {
				if (iCurrentSelection != position) {
					if (!spLanguage.getSelectedItem().toString()
							.equalsIgnoreCase("English")) {
						changeLang(LanguageCode.VI);
						saveLocale(LanguageCode.VI);
					} else {
						changeLang(LanguageCode.EN);
						saveLocale(LanguageCode.EN);
					}
					Intent intent = new Intent(ProfileActivity.this,
							ProfileActivity.class);
					startActivity(intent);
					finish();
					iCurrentSelection = position;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		try {
			ContextWrapper cw = new ContextWrapper(mContext);
			File directory = cw.getDir("images", Context.MODE_PRIVATE);
			File f = new File(directory, "profile.jpg");
			Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
			profileImage.setImageBitmap(b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.edit) {
			enableEdit();
			enableEdittext();
			editting = true;
			return true;
		}
		if (id == R.id.save) {
			getInfo();
			updateProfile();
			editting = false;
			return true;
		}

		if (id == R.id.cancel) {
			mEmail.setText(rider.getEmail());
			mPhone.setText(rider.getPhone());
			mFirstName.setText(rider.getFirstname());
			mLastName.setText(rider.getLastname());
			editting = false;
			disableEdit();
			disableEdittext();
			return true;
		}

		if (id == android.R.id.home) {
			Intent it = new Intent(ProfileActivity.this, MapActivity.class);
			startActivity(it);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public void updateProfile() {
		if (Utils.isConnectingToInternet(mContext)) {
			Rider rider = new Rider();
			rider.setFirstname(firstName);
			rider.setLastname(lastName);
			rider.setEmail(email);
			rider.setPhone(phone);
			String result = bo.checkProfile(ProfileActivity.this, rider);
			if (result.equalsIgnoreCase(Constants.SUCCESS)) {
				disableEdittext();
				disableEdit();

			}
		} else {
			AlertDialogManager.showCustomAlert(mContext,
					getString(R.string.no_internet_connection_title),
					getString(R.string.no_internet_connection_message));
		}
	}

	public void disableEdittext() {
		mEmail.setFocusable(false);
		mPhone.setFocusable(false);
		mPassword.setFocusable(false);
		mFirstName.setFocusable(false);
		mLastName.setFocusable(false);
	}

	public void enableEdittext() {
		mEmail.setFocusable(true);
		mEmail.setFocusableInTouchMode(true);
		mPhone.setFocusable(true);
		mPhone.setFocusableInTouchMode(true);
		mPassword.setFocusable(true);
		mPassword.setFocusableInTouchMode(true);
		mFirstName.setFocusable(true);
		mFirstName.setFocusableInTouchMode(true);
		mLastName.setFocusable(true);
		mLastName.setFocusableInTouchMode(true);
	}

	public void disableEdit() {
		mEditMenu.setVisible(true);
		mSaveMenu.setVisible(false);
		mCancelMenu.setVisible(false);
	}

	public void enableEdit() {
		mEditMenu.setVisible(false);
		mSaveMenu.setVisible(true);
		mCancelMenu.setVisible(true);
	}

	public void getInfo() {
		email = mEmail.getText().toString();
		phone = mPhone.getText().toString();
		firstName = mFirstName.getText().toString();
		lastName = mLastName.getText().toString();
	}

	public void changePassword(View v) {
		Intent it = new Intent(ProfileActivity.this,
				ChangPasswordActivity.class);
		startActivityForResult(it, 1);
	}

	public void addHomeAddress(View v) {
		Intent it = new Intent(ProfileActivity.this, PickAddressActivity.class);
		it.putExtra("type", "home");
		it.putExtra("id", rider.getId());
		startActivityForResult(it, 1);
	}

	public void addWorkAddress(View v) {
		Intent it = new Intent(ProfileActivity.this, PickAddressActivity.class);
		it.putExtra("type", "work");
		it.putExtra("id", rider.getId());
		startActivityForResult(it, 1);
	}

	public void logout(View v) {
		if (editting) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.update_warning_title));
			builder.setMessage(getString(R.string.update_warning_message));
			builder.setPositiveButton(getString(R.string.cancel),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {

						}
					});
			builder.setNegativeButton(getString(R.string.move),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							new LogoutBO(getParent()).execute();
						}
					});
			builder.setCancelable(false);
			builder.show();
		} else {
			if (Utils.isConnectingToInternet(this)) {
				AlertDialogManager.showLogoutAlert(this);
			} else {
				AlertDialogManager
						.showInternetConnectionErrorAlert(getApplicationContext());
			}
		}

	}

	public void deleteAndLogout() {
		// delete database
		handler.deleteRiderById();
		// clear shared preferences
		clearRegistrationId();
		// move to login screen
		Intent it = new Intent(ProfileActivity.this, LoginActivity.class);
		startActivity(it);
		finish();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				if (data.getStringExtra("action") == null) {
					AlertDialogManager
							.showCustomAlert(
									this,
									getString(R.string.change_password_title),
									getString(R.string.change_password_success_message));
				}
			}
			if (resultCode == RESULT_CANCELED) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getString(R.string.error));
				alertDialog
						.setMessage(getString(R.string.change_password_fail_message));
				alertDialog.setPositiveButton(getString(R.string.accept),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								deleteAndLogout();
							}
						});
			}
		}
	}

	private void clearRegistrationId() {
		final SharedPreferences prefs = getSharedPreferences(
				MapActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}

	public String loadLocale() {
		String langPref = "Language";
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		String language = prefs.getString(langPref, "");
		changeLang(language);
		return language;
	}

	public void changeLang(String lang) {
		Locale myLocale = new Locale(lang);
		saveLocale(lang);
		Locale.setDefault(myLocale);
		android.content.res.Configuration config = new android.content.res.Configuration();
		config.locale = myLocale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	@Override
	public void onBackPressed() {
		Intent it = new Intent(ProfileActivity.this, MapActivity.class);
		startActivity(it);
		finish();
	}

	public void saveLocale(String lang) {
		String langPref = "Language";
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiverTrip);
		unregisterReceiver(receiver);
		super.onDestroy();
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
}
