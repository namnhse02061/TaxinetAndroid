package vn.co.taxinet.mobile.newactivity;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.bo.ForgotPasswordBO;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ForgotPasswordActivity extends Activity {

	private EditText mInfo;
	private String info;
	private ForgotPasswordBO mForgotPasswordBO;
	private Context mContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		mInfo = (EditText) findViewById(R.id.et_info);
		mForgotPasswordBO = new ForgotPasswordBO(ForgotPasswordActivity.this);
	}

	public void getPassword(View v) {
		info = mInfo.getText().toString();
		mForgotPasswordBO.checkInfo(info);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
