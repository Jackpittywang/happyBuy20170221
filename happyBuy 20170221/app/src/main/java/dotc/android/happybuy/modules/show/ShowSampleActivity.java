package dotc.android.happybuy.modules.show;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.adapter.ShowSamplesPagerAdapter;

/**
 * Created by LiShen on 16/12/29.
 * Show images view
 */
public class ShowSampleActivity extends Activity {
    private ViewPager vpShowViewImages;

    private String[] images;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        initData();
        setListener();
        initView();
    }

    private void setLayout() {
        setContentView(R.layout.activity_show_pic);
    }

    private void findView() {
        vpShowViewImages = (ViewPager) findViewById(R.id.vpShowViewImages);
    }

    private void initData() {
        Intent intent = getIntent();
        images = intent.getExtras().getStringArray("pics");
        position = intent.getExtras().getInt("position", 0);
        if (images == null || images.length == 0) {
            finish();
        }
    }

    private void setListener() {

    }

    private void initView() {
        vpShowViewImages.setAdapter(new ShowSamplesPagerAdapter(this, images));
        vpShowViewImages.setCurrentItem(position, false);
    }
}
