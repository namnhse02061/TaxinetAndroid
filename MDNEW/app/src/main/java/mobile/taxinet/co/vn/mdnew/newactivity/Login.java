package mobile.taxinet.co.vn.mdnew.newactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

import mobile.taxinet.co.vn.mdnew.R;
import mobile.taxinet.co.vn.mdnew.alert.AlertDialogManager;
import mobile.taxinet.co.vn.mdnew.bo.LoginBO;
import mobile.taxinet.co.vn.mdnew.utils.Utils;

public class Login extends ActionBarActivity {
    private EditText mEmail, mPassword;
    private LoginBO loginBO;
    private SharedPreferences prefs = null;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_login);
        mEmail = (EditText) findViewById(R.id.etEmail);
        mPassword = (EditText) findViewById(R.id.etPassword);
        loginBO = new LoginBO();
        prefs = getSharedPreferences("vn.co.taxinet.mobile", MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void login(View v) {
        Log.v("Internet", Utils.isConnectingToInternet(context)+"");
        if (Utils.isConnectingToInternet(context)) {
            loginBO.checkLoginInfo(Login.this, mEmail.getText()
                    .toString(), mPassword.getText().toString());
        } else {
            AlertDialogManager.showInternetConnectionErrorAlert(context);
        }
    }
    public void getPassword(View v) {
        Intent it = new Intent(Login.this, Login.class);
        startActivityForResult(it, 2);
    }
}
