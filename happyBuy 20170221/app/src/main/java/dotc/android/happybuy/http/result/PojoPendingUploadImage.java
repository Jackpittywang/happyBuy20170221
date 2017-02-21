package dotc.android.happybuy.http.result;

import java.io.File;
import java.io.Serializable;

/**
 * Created by LiShen
 * on 2016/12/19.
 */

public class PojoPendingUploadImage implements Serializable {
    public File file;
    public PojoShowImage image = new PojoShowImage();
}
