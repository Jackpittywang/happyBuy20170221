package dotc.android.happybuy.http.result;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoBillingConfig implements Serializable{
    public List<PojoPay> list;
    public String default_product_id;

}
