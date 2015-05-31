package mobile.taxinet.co.vn.mdnew.bo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mobile.taxinet.co.vn.mdnew.R;
import mobile.taxinet.co.vn.mdnew.alert.AlertDialogManager;
import mobile.taxinet.co.vn.mdnew.dto.DriverDTO;
import mobile.taxinet.co.vn.mdnew.utils.Constants;

/**
 * Created by User on 5/28/2015.
 */
public class LoginBO {
    private Activity activity;
    private String username, password;
    private ProgressDialog pd;

    public void checkLoginInfo(Activity activity, String username, String password) {
        this.activity = activity;
        this.username = username;
        this.password = password;

        if (TextUtils.isEmpty(username)) {
            // return Const.EMPTY_ERROR;
            AlertDialogManager.showCustomAlert(activity,
                    this.activity.getString(R.string.error),
                    this.activity.getString(R.string.empty_email));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            // return Const.EMPTY_ERROR;
            AlertDialogManager.showCustomAlert(activity,
                    this.activity.getString(R.string.error),
                    this.activity.getString(R.string.empty_password));
            return;
        }
        new LoginAsyncTask().execute();
    }

    public class LoginAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pd = new ProgressDialog(activity);
//            pd.setTitle(activity.getString(R.string.login));
//            pd.setMessage(activity.getString(R.string.wait_message));
//            pd.setCancelable(true);
//            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.LOGIN_AUTHEN);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs
                        .add(new BasicNameValuePair("password", password));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
                        "UTF-8"));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                int respnseCode = response.getStatusLine().getStatusCode();
                if (respnseCode == 200) {
                    HttpEntity entity = response.getEntity();
                    return parseJson(EntityUtils.toString(entity));
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return null;
        }

    }

    public String parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String status = jsonObject.getString("status");
            if (status.equalsIgnoreCase(Constants.UserStatus.ACTIVE)) {
                DriverDTO driverDTO = new DriverDTO();
                driverDTO.setId(jsonObject.getString("id"));
                driverDTO.setFirstName(jsonObject.getString("firstName"));
                driverDTO.setLastName(jsonObject.getString("lastName"));
                driverDTO.setEmail(jsonObject.getString("email"));
                driverDTO.setPassword(jsonObject.getString("password"));
                driverDTO.setPhoneNumber(jsonObject.getString("phoneNumber"));
                driverDTO.setImage(jsonObject.getString("image"));
                driverDTO.setBalance(Double.parseDouble(jsonObject
                        .getString("balance")));
                driverDTO.setHomeAddress(jsonObject.getString("homeAddress"));
            }
            if (status.equalsIgnoreCase(Constants.DriverStatus.NEW)) {
                return Constants.LOGIN_ERROR.ACCOUNT_NOT_ACTIVE;
            }
            if (status.equalsIgnoreCase(Constants.Message.FAIL)) {
                return Constants.LOGIN_ERROR.WRONG_EMAIL_OR_PASSWORD;
            }
            return status;

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error",e.toString());
        }
        return null;

    }
}
