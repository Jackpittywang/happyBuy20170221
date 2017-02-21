package dotc.android.happybuy.http.result;

import java.util.List;

/**
 * Created by wangjun on 16/3/28.
 */
public class PojoProductList {
    public List<PojoProduct> productList;
    public long timestamp;

    @Override
    public String toString() {
        return "ProductList{" +
                "productList=" + productList +
                ", timestamp=" + timestamp +
                '}';
    }
}
