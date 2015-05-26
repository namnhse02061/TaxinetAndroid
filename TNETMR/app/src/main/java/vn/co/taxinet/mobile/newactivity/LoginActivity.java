package vn.co.taxinet.mobile.newactivity;

import java.util.Locale;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.bo.LoginBO;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private EditText mEmail, mPassword;
	private Button register;
	private LoginBO loginBO;
	private SharedPreferences prefs = null;
	private DatabaseHandler handler;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loadLocale();
		mEmail = (EditText) findViewById(R.id.et_email);
		mPassword = (EditText) findViewById(R.id.et_password);
		loginBO = new LoginBO();
		prefs = getSharedPreferences("vn.co.taxinet.mobile", MODE_PRIVATE);
		handler = new DatabaseHandler(this);

		// check database

		Rider rider = handler.findRider();
		if (rider != null) {

			AppController.setRiderId(rider.getId());

			Intent it = new Intent(LoginActivity.this, MapActivity.class);
			startActivity(it);
			finish();
		}

		// Register Button
		register = (Button) findViewById(R.id.bt_register);
		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(it);
				finish();
			}
		});

	}

	public void login(View v) {

		// check internet
		if (Utils.isConnectingToInternet(this)) {
			loginBO.checkLoginInfo(LoginActivity.this, mEmail.getText()
					.toString(), mPassword.getText().toString());
		} else {
			AlertDialogManager.showInternetConnectionErrorAlert(mContext);
		}

	}

	public void getPassword(View v) {
		Intent it = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
		startActivityForResult(it, 2);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (prefs.getBoolean("firstrun", true)) {
			Intent it = new Intent(LoginActivity.this, StartActivity.class);
			startActivity(it);
			finish();
			prefs.edit().putBoolean("firstrun", false).commit();
		}
	}

	public void loadLocale() {
		String langPref = "Language";
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		String language = prefs.getString(langPref, "");
		changeLang(language);
	}
	
	public void changeLang(String lang) {
		Locale myLocale = new Locale(lang);
		Locale.setDefault(myLocale);
		android.content.res.Configuration config = new android.content.res.Configuration();
		config.locale = myLocale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}
}
