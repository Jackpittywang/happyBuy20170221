package dotc.android.happybuy.social.facebook;

import java.io.Serializable;

/**
 * Created by wangjun on 16/11/21.
 */

public class FacebookSignInAccount implements Serializable {
    public String token;
    public String uid;
    public String name;
    public String email;
    public String gender;
    public String birthday;
    public String picture;
    public String linkurl;
    public String location;

    public FacebookSignInAccount(){}

    public FacebookSignInAccount(String token, String uid, String name, String email, String gender, String birthday, String picture) {
        this.token = token;
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "FacebookSignInAccount{" +
                "token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", picture='" + picture + '\'' +
                ", linkurl='" + linkurl + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
