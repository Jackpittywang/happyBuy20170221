package dotc.android.happybuy.modules.show;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.adapter.PhotoChooseAdapter;
import dotc.android.happybuy.modules.show.func.PhotoChooser;
import dotc.android.happybuy.modules.show.widget.PrizeShareAlertDialog;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public class PhotoChooseActivity extends BaseActivity {
    public static final String INTENT_FOLDER_CHOOSE = "intent_folder_choose";
    public static final int FOR_CHOOSE_PHOTO_FOLDER = 0x02;

    private HBToolbar tbPhotoChoose;
    private GridView gvPhotoChoose;
    private PhotoChooseAdapter photoChooseAdapter;
    private TextView tvPhotoChooseComplete;
    private TextView tvPhotoChooseNum;
    private ProgressBar pbPhotoChoose;
    private PrizeShareAlertDialog adPhotoChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
        findView();
        setListener();
        initView();

        new InitPhotosInfoTask().execute();
    }

    private void setLayout() {
        setContentView(R.layout.activity_photo_choose);
    }

    private void findView() {
        tbPhotoChoose = (HBToolbar) findViewById(R.id.tbPhotoChoose);
        gvPhotoChoose = (GridView) findViewById(R.id.gvPhotoChoose);
        tvPhotoChooseComplete = (TextView) findViewById(R.id.tvPhotoChooseComplete);
        tvPhotoChooseNum = (TextView) findViewById(R.id.tvPhotoChooseNum);
        pbPhotoChoose = (ProgressBar) findViewById(R.id.pbPhotoChoose);
    }

    private void setListener() {
        gvPhotoChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoChooseAdapter.setCheck(position);
            }
        });
        tbPhotoChoose.setRightTextItem(R.string.cancel, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        tbPhotoChoose.setLeftItem(new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                startActivityForResult(new Intent(PhotoChooseActivity.this,
                        PhotoFolderChooseActivity.class), FOR_CHOOSE_PHOTO_FOLDER);
            }
        });
        tvPhotoChooseComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoChooseAdapter.completeSelection();
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void initView() {
        tbPhotoChoose.setTitle(getString(R.string.choose_photo));

        photoChooseAdapter = new PhotoChooseAdapter(this);
        gvPhotoChoose.setAdapter(photoChooseAdapter);
        gvPhotoChoose.setSelector(R.color.transparent);

        setChooseNum(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FOR_CHOOSE_PHOTO_FOLDER && resultCode == RESULT_OK && data != null) {
            photoChooseAdapter.setPhotoFolder(data.getIntExtra(INTENT_FOLDER_CHOOSE, 0));
            setChooseNum(0);
        }
    }

    public void setChooseNum(int num) {
        if (num >= PhotoChooser.Singleton.getSharePhotoMaxNum()) {
            num = PhotoChooser.Singleton.getSharePhotoMaxNum();
        }
        tvPhotoChooseNum.setText(String.valueOf(num));
    }

    public void showAlertDialog(String content) {
        if (adPhotoChoose == null) {
            adPhotoChoose = new PrizeShareAlertDialog(this, R.style.prize_share_dialog);
        }
        adPhotoChoose.show(content);
    }

    private class InitPhotosInfoTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbPhotoChoose.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (PhotoChooser.Singleton.isReady()) {
                return RESULT_OK;
            } else {
                PhotoChooser.Singleton.searchPhotos();
                return RESULT_OK;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            pbPhotoChoose.setVisibility(View.GONE);
            // choose the all photos folder
            photoChooseAdapter.setPhotoFolder(0);
        }
    }
}
