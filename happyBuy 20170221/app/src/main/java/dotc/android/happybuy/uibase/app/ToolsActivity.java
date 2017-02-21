package dotc.android.happybuy.uibase.app;

import android.os.Bundle;

/**
 * Created by wangjun on 17/1/12.
 */

public class ToolsActivity extends BaseActivity {

    public static int mInstanceCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstanceCount++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstanceCount--;
    }
}
