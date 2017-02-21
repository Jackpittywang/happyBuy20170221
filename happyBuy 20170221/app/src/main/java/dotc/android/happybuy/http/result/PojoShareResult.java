package dotc.android.happybuy.http.result;

/**
 * Created by huangli on 16/4/20.
 */
public class PojoShareResult {
    public PojoShareResultInfo info;
    public String id;

    @Override
    public String toString() {
        return "PojoShareResult{" +
                "info=" + info +
                ", id='" + id + '\'' +
                '}';
    }
}
