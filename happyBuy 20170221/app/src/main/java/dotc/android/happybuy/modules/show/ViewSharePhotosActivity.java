package dotc.android.happybuy.modules.show;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import dotc.android.happybuy.R;
import dotc.android.happybuy.modules.show.adapter.ViewChoosePhotosAdapter;
import dotc.android.happybuy.modules.show.func.PhotoChooser;
import dotc.android.happybuy.uibase.app.BaseActivity;
import dotc.android.happybuy.uibase.component.HBToolbar;

/**
 * Created by LiShen
 * on 16/12/19.
 */
public class ViewSharePhotosActivity extends BaseActivity {
    public static final String EXTRA_FIRST_LOOK_POSITION = "extra_first_look_position";
    private HBToolbar tbViewSharePhotos;
    private ViewPager vpViewSharePhotos;

    private ViewChoosePhotosAdapter viewChoosePhotosAdapter;

    private int firstViewPosition;
    private int viewPhotoSelected = 0;

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
        setContentView(R.layout.activity_view_share_photos);
    }

    private void findView() {
        tbViewSharePhotos = (HBToolbar) findViewById(R.id.tbViewSharePhotos);
        vpViewSharePhotos = (ViewPager) findViewById(R.id.vpViewSharePhotos);
    }

    private void initData() {
        firstViewPosition = getIntent().getIntExtra(EXTRA_FIRST_LOOK_POSITION, 0);
    }

    private void setListener() {
        vpViewSharePhotos.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPhotoSelected = position;
                tbViewSharePhotos.setTitle(viewChoosePhotosAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tbViewSharePhotos.setRightTextItem(R.string.address_delete, new HBToolbar.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                PhotoChooser.Singleton.removeSharePhoto(viewPhotoSelected);
                viewChoosePhotosAdapter.refresh();
                vpViewSharePhotos.setCurrentItem(0, true);
                tbViewSharePhotos.setTitle(viewChoosePhotosAdapter.getPageTitle(0));
                if(PhotoChooser.Singleton.getSharePhotosChoose().size()==0){
                    finish();
                }
            }
        });
    }

    private void initView() {
        viewChoosePhotosAdapter = new ViewChoosePhotosAdapter(this);
        vpViewSharePhotos.setAdapter(viewChoosePhotosAdapter);
        vpViewSharePhotos.setCurrentItem(firstViewPosition, false);

        tbViewSharePhotos.setDisplayHomeAsUpEnabled(true);
        tbViewSharePhotos.setTitle(viewChoosePhotosAdapter.getPageTitle(firstViewPosition));
    }
}
