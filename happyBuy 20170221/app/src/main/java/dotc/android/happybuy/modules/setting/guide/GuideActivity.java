package dotc.android.happybuy.modules.setting.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.modules.setting.guide.widget.GuideViewPager;

/**
 * Created by huangli on 16/4/11.
 */
public class GuideActivity extends Activity{
    private final String KEY_FRIST_RUN = "frist_run_key";
    //翻页控件
    private GuideViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        mViewPager = (GuideViewPager)findViewById(R.id.whatsnew_viewpager);
        mViewPager.setLastPageLeftMoveListener(new GuideViewPager.LastPageLeftMoveListener() {

            @Override
            public void lastpageleftmoving() {
                // TODO Auto-generated method stub
                setGuided();
                Intent intent = new Intent(GuideActivity.this, MainTabActivity.class);
                startActivity(intent);
                GuideActivity.this.finish();
            }
        });
        /*
        * 这里是每一页要显示的布局，根据应用需要和特点自由设计显示的内容
        * 以及需要显示多少页等
        */
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.layout_guide_page1, null);
        View view2 = mLi.inflate(R.layout.layout_guide_page2, null);
        View view3 = mLi.inflate(R.layout.layout_guide_page3, null);
        final ArrayList<View> views = new ArrayList<>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        //填充ViewPager的数据适配器
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(views);
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void setGuided() {
        PrefUtils.putBoolean(this, KEY_FRIST_RUN, false);
    }

    public class MyPagerAdapter extends PagerAdapter {

        private ArrayList<View> views;


        public MyPagerAdapter(ArrayList<View> views){

            this.views = views;
        }

        @Override
        public int getCount() {
            return this.views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(views.get(position));
        }

        //页面view
        public Object instantiateItem(View container, int position) {

            ((ViewPager)container).addView(views.get(position));
            return views.get(position);
        }
    }

}
