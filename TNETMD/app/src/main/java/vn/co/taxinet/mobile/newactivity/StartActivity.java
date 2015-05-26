package vn.co.taxinet.mobile.newactivity;

import vn.co.taxinet.mobile.adapter.SlideAdapter;
import vn.co.taxinet.mobile.animation.ZoomOutPageTransformer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class StartActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_viewpaper);
		SlideAdapter adapter = new SlideAdapter();
		ViewPager myPager = (ViewPager) findViewById(R.id.pageview);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);
		myPager.setPageTransformer(true, new ZoomOutPageTransformer());
	}

	public void skipAndStart(View v) {
		Intent it = new Intent(StartActivity.this, LoginActivity.class);
		startActivity(it);
		finish();
	}

}
