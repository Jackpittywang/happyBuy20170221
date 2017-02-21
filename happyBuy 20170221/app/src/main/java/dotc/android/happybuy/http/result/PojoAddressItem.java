package dotc.android.happybuy.http.result;

/**
 * Created by huangli on 16/4/5.
 */
public class PojoAddressItem {
    public String address;
    public int id;
    public String mobile;
    public String name;
    public String state;
    public String city;
    public String zipcode;
    public String ward;

    @Override
    public String toString() {
        return "PojoAddressItem{" +
                "address='" + address + '\'' +
                ", id=" + id +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
