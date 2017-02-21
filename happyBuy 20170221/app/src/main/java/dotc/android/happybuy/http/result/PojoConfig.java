package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoConfig implements Serializable{
    public PojoH5 h5;
    public List<PojoCategory> categories;
    public PojoIcons icons;
    public String ab_url_prefix;
}
