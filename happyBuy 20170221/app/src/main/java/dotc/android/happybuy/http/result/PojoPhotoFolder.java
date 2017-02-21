package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiShen
 * on 2016/12/13.
 */

public class PojoPhotoFolder implements Serializable {
    public String iconPhoto = "";
    public int photoNum;
    public String folderName = "";
    public String folderPath = "";
    public List<String> photos = new ArrayList<>();
}
