package dotc.android.happybuy.modules.schema;

import android.os.Bundle;
import android.text.TextUtils;

import dotc.android.happybuy.log.HBLog;
import dotc.android.happybuy.uibase.app.BaseActivity;

/**
 * Created by wangjun on 16/3/29.
 *
 */
public class SchemaActivity extends BaseActivity {

    public static final String EXTRA_DATA_STRING = "extra_data_string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HBLog.e(TAG, " onCreate "+getIntent());
        if(getIntent()!= null){
            if(TextUtils.isEmpty(getIntent().getScheme())){
                String dataString = getIntent().getStringExtra(EXTRA_DATA_STRING);
                SchemeProcessor.handle(this,dataString);
            } else {
                String scheme = getIntent().getScheme();
                String dataString = getIntent().getDataString();
                SchemeProcessor.handle(this,scheme,dataString);
            }
        }
        finish();
    }

}
