package dotc.android.happybuy.uibase.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import dotc.android.happybuy.R;


public class NetworkErrorLayout extends LinearLayout implements View.OnClickListener {

    private final String TAG = this.getClass().getSimpleName();

    private View mRetryView;
    private View.OnClickListener mRetryListener;

    public NetworkErrorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkErrorLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.layout_network_error, this);
        mRetryView = findViewById(R.id.btn_retry);
        setOnClickListener(this);
    }

    public void setOnRetryListener(View.OnClickListener listener){
        mRetryListener = listener;

    }

    @Override
    public void onClick(View v) {
        if(mRetryListener!=null){
            mRetryListener.onClick(v);
        }
    }
}
