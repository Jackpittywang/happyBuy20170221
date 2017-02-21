package dotc.android.happybuy.modules.setting.language.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import dotc.android.happybuy.R;
import dotc.android.happybuy.http.result.PojoParticpateHistory;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;


/**
 * Created by wangjun on 16/2/1.
 */
public class LanguageAdapter extends BaseAdapter {

    private final String TAG = this.getClass().getSimpleName();
    private String[] mLanguages;
    private Context mContext;
    private LayoutInflater mInflater;
//    private AppIconLoader mAppIconLoader;

    public LanguageAdapter(Context context, String[] languages){
        mLanguages = languages;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return mLanguages.length;
    }

    @Override
    public String getItem(int i) {
        return mLanguages[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_language, viewGroup, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final String language = getItem(position);
        holder.textView.setText(language);

        String item = Languages.getInstance(mContext).getUserChoiceLanguage();
        if (Languages.LANGUAGE_DAFAULT.equals(item) && position == 0) {
            holder.imageView.setImageResource(R.drawable.ic_checkbox_check);
        } else {
            if(item.equals(language)){
                holder.imageView.setImageResource(R.drawable.ic_checkbox_check);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_checkbox_uncheck);
            }
        }
        return convertView;
    }

    static class Holder {
        ImageView imageView;
        TextView textView;

        public Holder(View view){
            imageView = (ImageView) view.findViewById(R.id.imageview_language);
            textView = (TextView) view.findViewById(R.id.textview_language);
        }
    }

}
