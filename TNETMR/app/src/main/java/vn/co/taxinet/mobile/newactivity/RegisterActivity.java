package vn.co.taxinet.mobile.newactivity;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.bo.RegisterBO;
import vn.co.taxinet.mobile.utils.Utils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	private CheckBox mCbTerm;
	private EditText mEmail, mPassword, mConfirmPassword, mFirstName,
			mLastName, mPhoneNumber;
	public static String email, password, confirmPassword, firstName, lastName,
			phoneNumber;
	private RegisterBO bo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mCbTerm = (CheckBox) findViewById(R.id.cb_term);
		initialize();
		
		
	}

	public void term(View v) {
		Intent it = new Intent(RegisterActivity.this, TermActivity.class);
		startActivityForResult(it, 1);
	}

	public void register(View v) {
		if (mCbTerm.isChecked()) {
			// check internet connection before register
			if (Utils.isConnectingToInternet(this)) {
				getInfo();
				bo.checkRegisterInfo(RegisterActivity.this, email, password,
						confirmPassword, firstName, lastName, phoneNumber);

			} else {
				// show error message
				AlertDialogManager.showCustomAlert(RegisterActivity.this,
						getString(R.string.no_internet_connection_title),
						getString(R.string.no_internet_connection_message));
			}
		} else {
			// show error message
			AlertDialogManager.showCustomAlert(RegisterActivity.this,
					getResources().getString(R.string.alert_term_title),
					getResources().getString(R.string.alert_term_message));
		}

	}

	public void getInfo() {
		email = mEmail.getText().toString();
		password = mPassword.getText().toString();
		confirmPassword = mConfirmPassword.getText().toString();
		firstName = mFirstName.getText().toString();
		lastName = mLastName.getText().toString();
		phoneNumber = mPhoneNumber.getText().toString();
	}

	public void initialize() {
		mEmail = (EditText) findViewById(R.id.et_email);
		mPassword = (EditText) findViewById(R.id.et_password);
		mConfirmPassword = (EditText) findViewById(R.id.et_cfpassword);
		mFirstName = (EditText) findViewById(R.id.et_first_name);
		mLastName = (EditText) findViewById(R.id.et_last_name);
		mPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
		bo = new RegisterBO();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				mCbTerm.setChecked(true);
			}
			if (resultCode == RESULT_CANCELED) {
				mCbTerm.setChecked(false);
			}
		}
	}

}
