package vn.co.taxinet.mobile.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import vn.co.taxinet.mobile.alert.AlertDialogManager;
import vn.co.taxinet.mobile.app.AppController;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.Rider;
import vn.co.taxinet.mobile.newactivity.R;
import vn.co.taxinet.mobile.utils.Constants;
import vn.co.taxinet.mobile.utils.Constants.Message;
import vn.co.taxinet.mobile.utils.Constants.RiderType;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

public class VipRiderAdapter extends BaseSwipeAdapter {

	private Context mContext;
	private DatabaseHandler handler;
	private ArrayList<Rider> listRider;

	public VipRiderAdapter(Context mContext, ArrayList<Rider> listRider) {
		this.mContext = mContext;
		this.listRider = listRider;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.item_vip_rider,
				null);
		SwipeLayout swipeLayout = (SwipeLayout) v
				.findViewById(getSwipeLayoutResourceId(position));
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				YoYo.with(Techniques.Tada).duration(500).delay(100)
						.playOn(layout.findViewById(R.id.bt_delete));

			}
		});
		swipeLayout
				.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
					@Override
					public void onDoubleClick(SwipeLayout layout,
							boolean surface) {
						Toast.makeText(mContext, "DoubleClick",
								Toast.LENGTH_SHORT).show();
					}
				});
		return v;
	}

	@Override
	public void fillValues(final int position, final View convertView) {
		TextView num = (TextView) convertView.findViewById(R.id.tv_numbering);
		TextView name = (TextView) convertView.findViewById(R.id.tv_name);
		TextView phone = (TextView) convertView.findViewById(R.id.tv_phone);
		num.setText((position + 1) + ".");
		name.setText(listRider.get(position).getFirstName() + " "
				+ listRider.get(position).getLastName());
		phone.setText(listRider.get(position).getPhone());

		Button delete = (Button) convertView.findViewById(R.id.bt_delete);
		handler = new DatabaseHandler(convertView.getContext());
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new DeleteVIPRiderBO(convertView.getContext(), position)
						.execute(listRider.get(position).getId());
			}
		});
		Button call = (Button) convertView.findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"
						+ listRider.get(position).getPhone()));
				mContext.startActivity(callIntent);
			}
		});

	}

	public void updateRiderList(List<Rider> newlist) {
		listRider.clear();
		listRider.addAll(newlist);
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listRider.size();
	}

	@Override
	public Object getItem(int position) {
		return listRider.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class DeleteVIPRiderBO extends AsyncTask<String, Void, String> {

		private Context context;
		private ProgressDialog pd;
		private int position;

		public DeleteVIPRiderBO(Context activity, int position) {
			this.context = activity;
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(context);
			pd.setTitle(context.getString(R.string.delete));
			pd.setMessage(context.getString(R.string.delete_vip_rider));
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return postData(params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				parseJson(result);
			} else {
				AlertDialogManager.showCustomAlert(mContext,
						mContext.getString(R.string.cannot_connect_to_server));
			}
		}

		public String postData(String[] params) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					Constants.URL.DELETE_FAVORITE_RIDER);
			try {
				System.out.println(AppController.getDriverId());

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("driverId",
						AppController.getDriverId()));
				nameValuePairs
						.add(new BasicNameValuePair("riderId", params[0]));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));
				HttpResponse response = httpclient.execute(httppost);
				int respnseCode = response.getStatusLine().getStatusCode();
				if (respnseCode == 200) {
					HttpEntity entity = response.getEntity();
					return EntityUtils.toString(entity);
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			return null;
		}

		public void parseJson(String response) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				String message = jsonObject.getString("message");
				if (message != null
						&& message.equalsIgnoreCase(Message.SUCCESS)) {
					Rider rider = listRider.get(position);
					rider.setType(RiderType.StrangerRider);
					handler.updateRider(rider);
					listRider.remove(position);
					notifyDataSetChanged();
				}
				if (message.equalsIgnoreCase(Message.DATA_NOT_FOUND)) {
					Toast.makeText(context, Message.FAIL, Toast.LENGTH_LONG)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
