package vn.co.taxinet.mobile.newactivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import vn.co.taxinet.mobile.R;
import vn.co.taxinet.mobile.googleapi.PlaceProvider;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

public class SearchPlace extends Activity implements LoaderCallbacks<Cursor> {

	public LatLng poisition;

	public SearchPlace() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SearchPlace(LatLng poisition) {
		super();
		this.poisition = poisition;
	}

	public LatLng getPoisition() {
		return poisition;
	}

	public void setPoisition(LatLng poisition) {
		this.poisition = poisition;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		Toast.makeText(getApplicationContext(), "Fuckkkkkkkkk", 5).show();
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		CursorLoader cLoader = null;
		cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI,
				null, null, new String[] { query }, null);

	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		CursorLoader cLoader = null;
		cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI,
				null, null, new String[] { query }, null);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
		Toast.makeText(getApplicationContext(), "Cursor", 3).show();
		CursorLoader cLoader = null;
		if (arg0 == 0)
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.SEARCH_URI, null, null,
					new String[] { query.getString("query") }, null);
		else if (arg0 == 1)
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.DETAILS_URI, null, null,
					new String[] { query.getString("query") }, null);
		return cLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		this.setPoisition(getPosition(c));

	}

	public void search() {
		onSearchRequested();
	}

	private LatLng getPosition(Cursor c) {
		LatLng position2 = null;
		while (c.moveToNext()) {
			position2 = new LatLng(Double.parseDouble(c.getString(1)),
					Double.parseDouble(c.getString(2)));
		}
		return position2;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
