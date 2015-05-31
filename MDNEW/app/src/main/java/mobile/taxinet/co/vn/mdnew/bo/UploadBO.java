package mobile.taxinet.co.vn.mdnew.bo;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mobile.taxinet.co.vn.mdnew.R;
import mobile.taxinet.co.vn.mdnew.alert.AlertDialogManager;
import mobile.taxinet.co.vn.mdnew.utils.Constants;

/**
 * Created by User on 5/30/2015.
 */
public class UploadBO {

    private int serverResponseCode = 0;

    private Activity activity;
    private String filePath;
    private InputStream in;

    public void upLoadFile(Activity activity, InputStream in, String filePath) {
        this.activity = activity;
        this.in = in;
        this.filePath = filePath;
        if (TextUtils.isEmpty(filePath)) {
            // return Const.EMPTY_ERROR;
            AlertDialogManager.showCustomAlert(activity,
                    this.activity.getString(R.string.error),
                    this.activity.getString(R.string.empty_email));
            return;
        }
        if (in == null) {
                // return Const.EMPTY_ERROR;
                AlertDialogManager.showCustomAlert(activity,
                        this.activity.getString(R.string.error),
                        this.activity.getString(R.string.empty_email));
                return;
            }
            new UploadAsyncTask().execute();
        }

        public class UploadAsyncTask extends AsyncTask<Void, Void, String> {
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
//            HttpClient httpclient = new DefaultHttpClient();
//
//            HttpPost httppost = new HttpPost(Constants.URL.UPLOAD_AVA);
                InputStream is = null;
                int len = 500;
                try {
                    // Add your data
                    URL url = new URL(Constants.URL.UPLOAD_AVA);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
    /* Define InputStreams to read from the URLConnection. */
                    int response = conn.getResponseCode();
                    Log.d("lamsao", "The response is: " + response);
                    is = conn.getInputStream();

                    // Convert the InputStream into a string

                    return "123";

//                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//                File file = new File(filePath);
//                FileBody bin = new FileBody(file, "multipart/form-data");
//
//                reqEntity.addPart("file", bin);
//
//                httppost.setEntity(reqEntity);
//                // Execute HTTP Post Request
//                HttpResponse response = httpclient.execute(httppost);
//                int respnseCode = response.getStatusLine().getStatusCode();
//                if (respnseCode == 200) {
//                    HttpEntity entity = response.getEntity();
//                    Log.i("test123", "Success");
//                    return "OK";
//                }
//            } catch (ClientProtocolException e) {
//                Log.e("test123", e.toString());
//            } catch (IOException e) {
//                Log.e("test123", e.toString());
//            }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("lamsao", e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("lamsao", e.toString());
            }
            return null;
        }
    }
}