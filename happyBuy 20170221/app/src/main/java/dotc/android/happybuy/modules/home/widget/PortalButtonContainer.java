package dotc.android.happybuy.modules.home.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.bean.HomeActive;
import dotc.android.happybuy.modules.schema.SchemeProcessor;

/**
 * Created by wangjun on 16/12/13.
 */

public class PortalButtonContainer extends LinearLayout {

    private List<HomeActive.PortalButton> mButtonList;
    private LayoutInflater mInflater;


    public PortalButtonContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mInflater = LayoutInflater.from(context);
        setOrientation(HORIZONTAL);
    }

    public void setItem(List<HomeActive.PortalButton> buttonList){
        this.mButtonList = buttonList;
        removeAllViews();
        for(int i=0;i<buttonList.size();i++){
            View view = createItemView();
            bindView(view,buttonList.get(i),i);
            addView(view,getDefaultLP());
        }
    }

    private View createItemView(){
        View view = mInflater.inflate(R.layout.layout_portal_button ,this,false);

        return view;
    }

    private void bindView(View view, final HomeActive.PortalButton button,final int i){
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        TextView textView = (TextView) view.findViewById(R.id.textview);

        Glide.with(getContext())
                .load(button.pic_url)
                .placeholder(R.drawable.ic_main_buttom_default)
                .into(imageView);
        textView.setText(button.name.getText());
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Analytics.sendUIEvent(AnalyticsEvents.HomeFragment.Click_Home_Button,i+1+"",null);
                SchemeProcessor.handle(getContext(),button.click_url);
            }
        });
    }

    private LayoutParams getDefaultLP(){
        return new LayoutParams(0, LayoutParams.MATCH_PARENT,1);
    }

}
