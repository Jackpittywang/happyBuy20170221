package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 * Created by huangli on 16/3/29.
 */
public class PojoPayItems implements Serializable {

    public String id;
    public String name;
    public int coin_num;
    public int price;
    public String cp_product_id;

    public int give_coin;
    public int status;
    public String desc;
}
