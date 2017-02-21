package dotc.android.happybuy.http.result;

import java.io.Serializable;

/**
 *
 */
public class PojoBilling implements Serializable{
    public String id;
    public String name;
    public int coin_num;
    public double price;
    public String pic_url;
    public String cp_product_id;

    @Override
    public String toString() {
        return "PojoBilling{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", coin_num='" + coin_num + '\'' +
                ", price='" + price + '\'' +
                ", pic_url='" + pic_url + '\'' +
                ", cp_product_id='" + cp_product_id + '\'' +
                '}';
    }
}
