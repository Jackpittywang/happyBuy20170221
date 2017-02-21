package dotc.android.happybuy.http;

/**
 * Created by wangjun on 16/3/28.
 */
public class HttpError extends Exception {

    public int code;
    public String msg;
    public Throwable throwable;

    public HttpError(int code, String msg, Throwable throwable) {
        this.code = code;
        this.msg = msg;
        this.throwable = throwable;
    }
}
