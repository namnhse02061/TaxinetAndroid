package vn.co.taxinet.mobile.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.model.NavDrawerItem;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (navDrawerItems.get(position).getType() == 2) {
				convertView = mInflater.inflate(R.layout.item_drawer_header,
						null);
				ImageView profileImage = (ImageView) convertView
						.findViewById(R.id.profile_image);
				TextView profileName = (TextView) convertView
						.findViewById(R.id.profile_name);
				profileName.setText(navDrawerItems.get(position).getTitle());
//				try {
//
//					ContextWrapper cw = new ContextWrapper(context);
//					// path to /data/data/yourapp/app_data/imageDir
//					File directory = cw.getDir("images", Context.MODE_PRIVATE);
//					File f = new File(directory, "profile.jpg");
//					Bitmap b = BitmapFactory
//							.decodeStream(new FileInputStream(f));
//					profileImage.setImageBitmap(b);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
			} else {

				convertView = mInflater.inflate(R.layout.item_drawer, null);
				ImageView imgIcon = (ImageView) convertView
						.findViewById(R.id.icon);
				TextView txtItem = (TextView) convertView
						.findViewById(R.id.list_item);
				TextView txtCount = (TextView) convertView
						.findViewById(R.id.counter);

				imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
				txtItem.setText(navDrawerItems.get(position).getTitle());
				if (navDrawerItems.get(position).isCounterVisible()) {
					txtCount.setText(navDrawerItems.get(position).getCount());
				} else {
					// hide the counter view
					txtCount.setVisibility(View.GONE);
				}
				if (navDrawerItems.get(position).getType() == 3) {
					RelativeLayout logout = (RelativeLayout) convertView
							.findViewById(R.id.list_selector_item);
					logout.setBackgroundColor(context.getResources().getColor(
							R.color.fresh_red));
					txtItem.setTextColor(context.getResources().getColor(
							R.color.white));
				}
			}
		}

		return convertView;
	}
}
