package dotc.android.happybuy.modules.setting.language;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.R;
import dotc.android.happybuy.config.ConfigManager;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.modules.main.MainTabActivity;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.modules.setting.language.adapter.LanguageAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.ToastUtils;

/**
 * Created by wangjun on 16/3/29.
 * 语言
 *
 */
public class LanguageActivity extends BaseActivity {

    private HBToolbar mToolbar;
    private ListView mListView;
    private LanguageAdapter mLanguageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        initActionbar();
        initUI();
    }

    private void initActionbar() {
        mToolbar = (HBToolbar) findViewById(R.id.id_toolbar);
        mToolbar.setTitle(R.string.activity_language);
        mToolbar.setDisplayHomeAsUpEnabled(true);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUI() {
        mListView = (ListView) findViewById(R.id.listview);

        mLanguageAdapter = new LanguageAdapter(this, Languages.getInstance(this).getLanguages());
        mListView.setAdapter(mLanguageAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String language = mLanguageAdapter.getItem(position);
                HBLog.d(TAG+" onItemClick language:"+language);
                if(!language.equals(Languages.getInstance().getUserChoiceLanguage())){
                    if(Languages.getInstance().updateLanguageWithLanguage(language)){
                        Languages.getInstance().userChoiceChanged(language);
                        mLanguageAdapter.notifyDataSetChanged();
                        ConfigManager.get(LanguageActivity.this).asyncFetch(true);//重新拉取一次配置
                        backToHome();
                    } else {
                        ToastUtils.showLongToast(GlobalContext.get(),R.string.show_save_pic_result_fail);
                    }
                }
            }
        });
    }

    private void backToHome(){
        getMyApplication().finishAllActivity();
        Intent intent = new Intent(this, MainTabActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
