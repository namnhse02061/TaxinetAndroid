package vn.co.taxinet.mobile.adapter;

import java.util.List;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.database.DatabaseHandler;
import vn.co.taxinet.mobile.model.Trip;
import vn.co.taxinet.mobile.utils.Constants.RiderType;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyTripAdatpter extends BaseAdapter {

	private Activity activity;
	private List<Trip> list;
	private TextView tvDay, tvName, tvFrom, tvTo, tvType;

	public MyTripAdatpter(Activity activity, List<Trip> list) {
		this.activity = activity;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int location) {
		return list.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			if (list.get(position).getCompletionTime().length() == 10) {
				convertView = inflater.inflate(R.layout.header_my_trip, null);
				initialize2(convertView, position);
			} else {
				convertView = inflater.inflate(R.layout.item_my_trip, null);
				initialize1(convertView, position);
			}
		}
		return convertView;
	}

	public void initialize1(View v, int position) {

		tvName = (TextView) v.findViewById(R.id.tv_name);
		tvFrom = (TextView) v.findViewById(R.id.tv_from);
		tvTo = (TextView) v.findViewById(R.id.tv_to);
		Trip trip = list.get(position);
		tvName.setText(trip.getDriver().getFirstName()+ " "+ trip.getDriver().getLastName());
		tvFrom.setText(trip.getFromAddress());
		tvTo.setText(trip.getToAddress());
	}

	public void initialize2(View v, int position) {
		tvDay = (TextView) v.findViewById(R.id.tv_day);
		tvDay.setText(list.get(position).getCompletionTime());
	}

	public List<Trip> updateListTrip() {
		DatabaseHandler handler = new DatabaseHandler(activity);
		list = handler.getListTrip();
		notifyDataSetChanged();
		return list;
	}

}
