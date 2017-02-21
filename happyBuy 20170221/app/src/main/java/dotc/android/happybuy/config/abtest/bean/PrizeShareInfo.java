package dotc.android.happybuy.config.abtest.bean;

import dotc.android.happybuy.config.abtest.core.MultiLang;

/**
 * Created by LiShen
 * on 2016/12/15.
 */

public class PrizeShareInfo {
    public int user_input_text_max;
    public int user_photos_max;

    @Override
    public String toString() {
        return "PrizeShareInfo{" +
                "user_input_text_max=" + user_input_text_max +
                ", user_photos_max=" + user_photos_max +
                '}';
    }
}
