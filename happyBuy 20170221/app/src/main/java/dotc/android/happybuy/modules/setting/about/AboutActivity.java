package dotc.android.happybuy.modules.setting.about;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;

/**
 * Created by wangjun on 16/3/29.
 * 关于
 *
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private HBToolbar mToolbar;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initActionbar();
        initUI();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_about);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mTextView = (TextView) findViewById(R.id.textview_app_version);
        mTextView.setText(AppUtil.getAppVersionName(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_pass_mgr:
                break;
        }
    }


}
