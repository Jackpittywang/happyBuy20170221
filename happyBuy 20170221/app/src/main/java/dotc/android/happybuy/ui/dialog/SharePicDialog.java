package dotc.android.happybuy.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import dotc.android.happybuy.R;
import dotc.android.happybuy.util.DisplayUtils;

/**
 * Created by huangli on 16/4/6.
 */
public class SharePicDialog extends RelativeLayout {
    private int POP_OFFSET ;       //弹出偏移量
    public interface OnSharePicDialogButtonClickListener{
        void onbtnAlbumClick();
        void onbtnCameraClick();
    }
    private RelativeLayout btnAlbum,btnCamera,btnCancel,layoutDialog;
    private OnSharePicDialogButtonClickListener onSharePicDialogButtonClickListener;



    public SharePicDialog(Context context,AttributeSet attrs) {
        super(context,attrs);
        POP_OFFSET = DisplayUtils.dp2Px(context,40);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dialog_share_pic, this);
        findViews();
        setListeners();
    }

    public void show(){
        layoutDialog.setVisibility(VISIBLE);
        anim();
    }

    private void anim(){
        btnCancel.setVisibility(INVISIBLE);
        btnCamera.setVisibility(INVISIBLE);
        btnAlbum.setVisibility(INVISIBLE);
        btnCancel.setVisibility(VISIBLE);
        final ObjectAnimator objectAnimatorCancelBtn = ObjectAnimator.ofFloat(btnCancel, "translationY", POP_OFFSET, 0).setDuration(50);
        final ObjectAnimator objectAnimatorCameraBtn = ObjectAnimator.ofFloat(btnCamera, "translationY", POP_OFFSET, 0).setDuration(15);
        final ObjectAnimator objectAnimatorAlbumBtn = ObjectAnimator.ofFloat(btnAlbum, "translationY", POP_OFFSET, 0).setDuration(15);
        objectAnimatorCancelBtn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnCamera.setVisibility(VISIBLE);
                objectAnimatorCameraBtn.start();
            }
        });
        objectAnimatorCameraBtn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnAlbum.setVisibility(VISIBLE);
                objectAnimatorAlbumBtn.start();
            }
        });
        objectAnimatorAlbumBtn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        objectAnimatorCancelBtn.start();
    }

    public void dismiss(){
        layoutDialog.setVisibility(GONE);
    }

    public boolean isshowing(){
        return layoutDialog.getVisibility() == VISIBLE;
    }

    private void findViews() {
        btnAlbum = (RelativeLayout)findViewById(R.id.layout_album);
        btnCamera = (RelativeLayout)findViewById(R.id.layout_camera);
        btnCancel = (RelativeLayout)findViewById(R.id.layout_cancel);
        layoutDialog = (RelativeLayout)findViewById(R.id.layout_dialog);
    }

    public void setOnSharePicDialogButtonClickListener(OnSharePicDialogButtonClickListener onSharePicDialogButtonClickListener){
        this.onSharePicDialogButtonClickListener = onSharePicDialogButtonClickListener;
    }


    private void setListeners() {
        layoutDialog.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layoutDialog.setVisibility(GONE);
                return false;
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSharePicDialogButtonClickListener != null){
                    onSharePicDialogButtonClickListener.onbtnAlbumClick();
                }
                layoutDialog.setVisibility(GONE);
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSharePicDialogButtonClickListener != null){
                    onSharePicDialogButtonClickListener.onbtnCameraClick();
                }
                layoutDialog.setVisibility(GONE);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutDialog.setVisibility(GONE);
            }
        });
    }


}
