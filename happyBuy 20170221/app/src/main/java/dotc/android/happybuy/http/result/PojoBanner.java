package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

import dotc.android.happybuy.config.abtest.core.IConfigBean;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoBanner implements IConfigBean {

    public List<PojoAd> adInfos;

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
