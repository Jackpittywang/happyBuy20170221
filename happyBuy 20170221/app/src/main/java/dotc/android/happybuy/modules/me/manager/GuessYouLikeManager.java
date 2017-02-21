package dotc.android.happybuy.modules.me.manager;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.analytics.Analytics;
import dotc.android.happybuy.analytics.AnalyticsEvents;
import dotc.android.happybuy.config.abtest.AbConfigManager;
import dotc.android.happybuy.config.abtest.bean.YouLikeItem;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.modules.me.adapter.GuessAdapter;

/**
 * Created by 陈尤岁 on 2016/12/14.
 */
public class GuessYouLikeManager {

    private View mViewGroup;

    private GridView mGridView;

    private TextView mTvReminder;

    private List<YouLikeItem> mGuessData;

    private String mLabel;

    public GuessYouLikeManager(String label) {
        mLabel = label;
    }


    private List<YouLikeItem> getGuessData(Context ctx) {
        if (mGuessData == null) {
            mGuessData = AbConfigManager.getInstance(ctx).getConfig().guess_you_like;
        }
        return mGuessData;
    }

    public void addGuessLayout(final ViewGroup view, int stringID) {
        mViewGroup = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_guess_you_like, null);
        mGridView = (GridView) mViewGroup.findViewById(R.id.gridView);
        mViewGroup.findViewById(R.id.tv_buy_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if((Activity)view.getContext() instanceof MainTabActivity){
                    ((MainTabActivity)view.getContext()).switchTabLayout(0);
                    Analytics.sendUIEvent(AnalyticsEvents.UserCenter.ClickBuyIt,mLabel, null);
                }
            }
        });
        initGridView(view.getContext());
        mGridView.setAdapter(new GuessAdapter(mLabel,getGuessData(view.getContext())));
        mTvReminder = (TextView) mViewGroup.findViewById(R.id.tv_reminder);
        mTvReminder.setText(stringID);
        view.removeAllViews();
        view.addView(mViewGroup);
    }

    public void initGridView(Context ctx) {
//        WindowManager wm = (WindowManager) ctx
//                .getSystemService(Context.WINDOW_SERVICE);
//        int width = wm.getDefaultDisplay().getWidth();
        int size = getGuessData(ctx).size();
//        int itemWidth = width / 3;
        int absoluteItemWidth = ctx.getResources().getDimensionPixelOffset(R.dimen.guess_item_width);

        int allWidth = absoluteItemWidth * size;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                allWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        mGridView.setLayoutParams(params);
        mGridView.setColumnWidth(absoluteItemWidth);
//        mGridView.setHorizontalSpacing(10);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setNumColumns(size);
    }
}
