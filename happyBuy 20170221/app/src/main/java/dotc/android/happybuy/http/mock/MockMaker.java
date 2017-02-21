package dotc.android.happybuy.http.mock;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import dotc.android.happybuy.log.HBLog;

/**
 * Created by wangjun on 16/3/28.
 */
public class MockMaker {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;

    public MockMaker(Context context){
        mContext = context.getApplicationContext();
    }

    public String createMock(String url, Map<String, Object> params){
        String assetFilePath= "mock/"+getFileName(url)+".json";
//        HBLog.d(TAG + " createMock " + assetFilePath);
        try{
            InputStream inputStream = mContext.getAssets().open(assetFilePath);
            String response = inputStream2String(inputStream);
            return response;
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    private String getFileName(String url){
        return url.substring(url.lastIndexOf("/")+1);
    }

    public static String inputStream2String(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i=-1;
        while((i=is.read())!=-1){
            baos.write(i);
        }
        return baos.toString();
    }


}
