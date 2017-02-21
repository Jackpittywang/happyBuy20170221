package dotc.android.happybuy.http.result;

/**
 * Created by huangli on 16/3/31.
 */
public class PojoComment {
    public String userName;
    public String ptime;
    public String comment;
    public PojoComment(String userName,String ptime,String comment){
        this.userName = userName;
        this.ptime = ptime;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "PojoComment{" +
                "userName='" + userName + '\'' +
                ", ptime='" + ptime + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
