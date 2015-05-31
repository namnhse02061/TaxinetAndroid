package mobile.taxinet.co.vn.mdnew.newactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import mobile.taxinet.co.vn.mdnew.R;
import mobile.taxinet.co.vn.mdnew.adapter.SlideAdapter;
import mobile.taxinet.co.vn.mdnew.animation.ZoomOutPageTransformer;


public class StartApp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        SlideAdapter adapter = new SlideAdapter();
        ViewPager myPager = (ViewPager) findViewById(R.id.pageview);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
        myPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    public void nextView(View v) {
        Intent intent = new Intent(StartApp.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void upLoad(View v) {
        Intent intent = new Intent(StartApp.this, UpLoadImage.class);
        startActivity(intent);
        finish();
    }
}
