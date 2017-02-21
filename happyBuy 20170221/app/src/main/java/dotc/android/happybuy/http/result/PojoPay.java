package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class PojoPay implements Serializable{
    public String id;
    public String name;
    public List<PojoPayItems> items;
    public String desc;
    public String app_url;

}
