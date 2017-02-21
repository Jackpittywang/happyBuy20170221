package dotc.android.happybuy.social;

/**
 * Created by huangli on 16/5/10.
 */
@Deprecated
public class ProfileInfo {
    public String platform;
    public String account;
    public String token;
    public String name;
    public String headUrl;
    public ProfileInfo(String platform,String account, String token, String name, String headUrl){
        this.platform = platform;
        this.account = account;
        this.token = token;
        this.name = name;
        this.headUrl = headUrl;
    }
}
