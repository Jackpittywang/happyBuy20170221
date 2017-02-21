package dotc.android.happybuy.modules.show;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.adapter.PhotoFolderChooseAdapter;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public class PhotoFolderChooseActivity extends BaseActivity {
    private HBToolbar tbPhotoFolderChoose;
    private ListView lvPhotoFolderChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        setListener();
        initView();
    }

    private void setLayout() {
        setContentView(R.layout.activity_photo_folder_choose);
    }

    private void findView() {
        tbPhotoFolderChoose = (HBToolbar) findViewById(R.id.tbPhotoFolderChoose);
        lvPhotoFolderChoose = (ListView) findViewById(R.id.lvPhotoFolderChoose);
    }

    private void setListener() {
        lvPhotoFolderChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(PhotoChooseActivity.INTENT_FOLDER_CHOOSE, position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initView() {
        tbPhotoFolderChoose.setTitle(R.string.choose_photo_folder);
        tbPhotoFolderChoose.setDisplayHomeAsUpEnabled(true);

        lvPhotoFolderChoose.setAdapter(new PhotoFolderChooseAdapter(this));
    }
}

