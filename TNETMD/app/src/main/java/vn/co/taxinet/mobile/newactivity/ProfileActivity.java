package vn.co.taxinet.mobile.newactivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.LogoutBO;
import vn.co.taxinet.mobile.bo.ProfileBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.gps.HandleMessageReceiver;
import vn.co.taxinet.mobile.model.Driver;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.LanguageCode;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class ProfileActivity extends Activity {

	private EditText mEmail, mPhone, mFirstName, mLastName, mBalance;
	private MenuItem mSaveMenu, mEditMenu, mCancelMenu;
	private String email, phone, firstName, lastName;
	private ProfileBO bo;
	private ActionBar actionBar;
	private DatabaseHandler handler;
	private Button mPassword, mHomeAddress;
	private InputMethodManager imm;
	private Driver driver;
	private boolean editting = false;
	private Context mContext = this;
	private ImageView profileImage;
	private Spinner spLanguage;
	private int iCurrentSelection;
	private BroadcastReceiver receiverPromotionTrip, receiverTrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		receiverPromotionTrip = new HandleMessageReceiver();
		registerReceiver(receiverPromotionTrip, filter);

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
		mBalance = (EditText) findViewById(R.id.et_balance);
		spLanguage = (Spinner) findViewById(R.id.sp_language);
		mHomeAddress = (Button) findViewById(R.id.bt_home_address);
		String locate = loadLocale();
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
					if (spLanguage.getSelectedItem().toString()
							.equalsIgnoreCase("Tiếng việt")) {
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

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		bo = new ProfileBO(mContext);
		handler = new DatabaseHandler(this);

		driver = handler.findDriver();
		setText();
		profileImage = (ImageView) findViewById(R.id.image);

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

	public class TripReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context mContext, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras.getSerializable(Constants.TRIP) != null) {
				Trip trip = (Trip) extras.getSerializable(Constants.TRIP);
				AlertDialogManager manager = new AlertDialogManager();
				manager.showNewTripRequestAlert(ProfileActivity.this, trip);
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiverPromotionTrip);
		unregisterReceiver(receiverTrip);
		super.onDestroy();
	}

	public void setText() {
		mEmail.setText(driver.getEmail());
		mPhone.setText(driver.getPhoneNumber());
		mFirstName.setText(driver.getFirstName());
		mLastName.setText(driver.getLastName());
		mBalance.setText(String.valueOf(driver.getBalance()));
		System.out.println(String.valueOf(driver.getBalance()));
		mHomeAddress.setText(driver.getHomeAddress());
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
			disableEdit();
			disableEdittext();
			setText();
			editting = false;
			return true;
		}
		if (id == android.R.id.home) {
			Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public void updateProfile() {
		hideKeyboard();
		if (Utils.isConnectingToInternet(mContext)) {
			String result = bo.checkProfile(firstName, lastName, email, phone);
			if (result.equalsIgnoreCase(Constants.SUCCESS)) {
				disableEdittext();
				disableEdit();
			}
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(mContext);
		}
	}

	public void hideKeyboard() {
		imm.hideSoftInputFromWindow(mEmail.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mPhone.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mFirstName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mLastName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
	}

	public void disableEdittext() {
		mEmail.setFocusable(false);
		mPhone.setFocusable(false);
		mPassword.setFocusable(false);
		mFirstName.setFocusable(false);
		mLastName.setFocusable(false);
		mBalance.setFocusable(false);
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
		hideKeyboard();
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
		hideKeyboard();
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
							Intent it = new Intent(ProfileActivity.this,
									ChangePasswordActivity.class);
							startActivityForResult(it, 1);
						}
					});
			builder.setCancelable(false);
			builder.show();
		} else {
			Intent it = new Intent(ProfileActivity.this,
					ChangePasswordActivity.class);
			startActivityForResult(it, 1);
		}
	}

	public void pickHomeAddress(View v) {
		hideKeyboard();
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
							Intent it = new Intent(ProfileActivity.this,
									HomeAddressActivity.class);
							it.putExtra("homeAddress", driver.getHomeAddress());
							startActivityForResult(it, 1);
						}
					});
			builder.setCancelable(false);
			builder.show();
		} else {
			Intent it = new Intent(ProfileActivity.this,
					HomeAddressActivity.class);
			it.putExtra("homeAddress", driver.getHomeAddress());
			startActivityForResult(it, 2);
		}
	}

	public void logout(View v) {
		hideKeyboard();
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
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.change_password_title),
						getString(R.string.change_password_fail_message));
			}
		}

		if (requestCode == 2) {
			if (resultCode == RESULT_OK) {
				if (data.getStringExtra("action") == null) {
					AlertDialogManager
							.showCustomAlert(
									this,
									getString(R.string.change_home_address_title),
									getString(R.string.change_home_address_success_message));
					mHomeAddress.setText(data.getStringExtra("address"));
				}
			}
			if (resultCode == RESULT_CANCELED) {
				AlertDialogManager.showCustomAlert(this,
						getString(R.string.change_password_title),
						getString(R.string.change_home_address_fail_message));
			}
		}
	}

	public void companyInfo(View v) {
		hideKeyboard();
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
							Intent it = new Intent(ProfileActivity.this,
									CompanyActivity.class);
							startActivity(it);
							finish();
						}
					});
			builder.setCancelable(false);
			builder.show();
		} else {
			Intent it = new Intent(ProfileActivity.this, CompanyActivity.class);
			startActivity(it);
			finish();
		}

	}

	public void saveLocale(String lang) {
		String langPref = "Language";
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
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
}
