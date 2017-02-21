package dotc.android.happybuy.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dotc.android.happybuy.GlobalContext;
import dotc.android.happybuy.http.mock.MockMaker;
import dotc.android.happybuy.http.result.PojoFileUpload;
import dotc.android.happybuy.language.Languages;
import dotc.android.happybuy.persist.pref.PrefConstants;
import dotc.android.happybuy.persist.pref.PrefUtils;
import dotc.android.happybuy.util.AesUtil;
import dotc.android.happybuy.util.AppUtil;
import dotc.android.happybuy.util.Md5Util;
import dotc.android.happybuy.util.RandomStringUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wangjun on 16/3/28.
 */
public class Network {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private OkHttpClient mOkHttpClient;
    private MockMaker mMockMaker;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mCountryCode;
    private static Network mInstance;

    private Network(Context context) {
        mContext = context;
        mOkHttpClient = new OkHttpClient();
        mMockMaker = new MockMaker(context);
        mOkHttpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS);
        mCountryCode = AppUtil.getMetaData(context,"country");
    }

    public static Network get(Context context) {
        if (mInstance == null) {
            synchronized (Network.class) {
                if (mInstance == null) {
                    mInstance = new Network(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    public OkHttpClient getHttpClient(){
        return mOkHttpClient;
    }

    public PojoFileUpload syncUploadFile(File file){
        final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(HttpProtocol.Header.TOKEN, PrefUtils.getString(PrefConstants.Token.TOKEN, ""))
                .addFormDataPart(HttpProtocol.Header.UID, PrefUtils.getString(PrefConstants.UserInfo.UID, ""))
                .addFormDataPart(HttpProtocol.Header.APP_ID, HttpProtocol.AppId.APP_ID)
                .addFormDataPart(HttpProtocol.Header.PRD_ID,HttpProtocol.ProducteId.PRD_ID)
//                .addFormDataPart("file_type", "jpg")
                .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();
        Request request = new Request.Builder()
                .url(HttpProtocol.URLS.FILE_UPLOAD)//HttpProtocol.URLS.FILE_UPLOAD
                .headers(buildFileUploadHeader())
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        try {
            Response response = call.execute();
//            String responseString = response.body().string();
            String responseString = parseResponse(response.body().string());
            HttpLog.d(TAG , " syncUploadFile response:" + responseString);
            JSONObject jsonObject = new JSONObject(responseString);
            if(HttpProtocol.CODE.OK == jsonObject.getInt("code")){
                String data = jsonObject.getString("data");
                PojoFileUpload pojoFileUpload = parseResponse(data, PojoFileUpload.class);
                if(HttpProtocol.FILE_ESCAPE){
                    pojoFileUpload.file_url = decodeFileUrl(pojoFileUpload.file_url);
                }
                return pojoFileUpload;
            }
        } catch (Exception e){
            HttpLog.w(TAG," syncUploadFile "+e);
        }
        return null;
    }

    public <T> T syncPost(String url,Map<String,Object> params,Class<T> className) throws HttpError{
        if (HttpProtocol.MOCK_ENABLE&&false) {
            return null;
        } else {
            return syncHttpPost(url,params,className);
        }
    }

    public <T> void asyncPostOnThread(String url, Map<String, Object> params, final JsonCallBack<T> jsonCallBack) {
        if (HttpProtocol.MOCK_ENABLE) {
            if (asyncMockPost(false,url, params, jsonCallBack) == 0) {
                asyncHttpPost(false,url, params, jsonCallBack);
            }
        } else {
            asyncHttpPost(false,url, params, jsonCallBack);
        }
    }

    public <T> void asyncPost(String url, Map<String, Object> params, final JsonCallBack<T> jsonCallBack) {
        if (HttpProtocol.MOCK_ENABLE) {
            if (asyncMockPost(true,url, params, jsonCallBack) == 0) {
                asyncHttpPost(true,url, params, jsonCallBack);
            }
        } else {
            asyncHttpPost(true,url, params, jsonCallBack);
        }
    }

    private <T> void asyncHttpPost(final boolean callOnUi,String url, Map<String, Object> params, final JsonCallBack<T> jsonCallBack) {
        Call call = buildHttpCall(url, params);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                notifyReponseFail(callOnUi,HttpProtocol.CODE.NET_ERROR, "", e, jsonCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    handleResponse(callOnUi,response, jsonCallBack);
                } catch (IOException e) {
                    notifyReponseFail(callOnUi,HttpProtocol.CODE.NET_ERROR, "io error",e,jsonCallBack);
                }
            }
        });
    }

    //callback at thread
    public <T> void asyncCPlusConfig(String url,final JsonCallBack<T> jsonCallBack){
        HttpLog.d(TAG , "asyncCPlusConfig url:" + url);
        Request.Builder builder = new Request.Builder().url(url);
//        builder.headers(buildDefaultHeader());
//        builder.post(toRequestBody(url, params));
        Request request = builder.build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                notifyReponseFail(false,HttpProtocol.CODE.NET_ERROR, "", e, jsonCallBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    handleResponse(false,false,response, jsonCallBack);
                } catch (IOException e) {
                    notifyReponseFail(false,HttpProtocol.CODE.NET_ERROR, "io error",e,jsonCallBack);
                }
            }
        });
    }

    private Call buildHttpCall(String url, Map<String, Object> params){
        String fixedUrl = buildUrl(url);
        HttpLog.d(TAG , " buildHttpCall2:" + fixedUrl);
        Request.Builder builder = new Request.Builder().url(fixedUrl);
        builder.headers(buildDefaultHeader());
        builder.post(toRequestBody(url, params));
        Request request = builder.build();
        return mOkHttpClient.newCall(request);
    }

    private RequestBody toRequestBody(String url,Map<String, Object> params) {
        if (HttpProtocol.ENCRY_ENABLE) {
            String data = toJson(url, params);
            String randomKey = RandomStringUtils.getRandomString(8);
            String realKey = Md5Util.md5Hex(randomKey + HttpProtocol.Secure.AES_KEY);
            byte[] binaryKey = AesUtil.hex2byte(realKey);
            String encryptContent = AesUtil.encrypt(data, binaryKey);
            String wrapEncryContent = randomKey + encryptContent;

            //        Integer.toBinaryString(a)
            HttpLog.d(TAG," data:" + data);
//            HBLog.d(TAG + " randomKey:" + randomKey);
//            HBLog.d(TAG + " realKey:" + realKey);
//            HBLog.d(TAG + " encryptStr:" + encryptContent);
//            HBLog.d(TAG + " 加密的postbody参数:" + wrapEncryContent);
            return RequestBody.create(MediaType.parse("text/plain"), wrapEncryContent);
        } else {
            String data = toJson(url, params);
            HttpLog.d(TAG, " data:" + data);
            return RequestBody.create(MediaType.parse("text/plain"), data);
            /*
            FormEncodingBuilder builder = new FormEncodingBuilder();
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
            return builder.build(); */
        }
    }

    private String toJson(String url,Map<String, Object> params) {
        try {
            JSONObject jsonObject = new JSONObject();
            if(params!=null){
                for (String key : params.keySet()) {
                    jsonObject.put(key, params.get(key));
                }
            }
            jsonObject.put(HttpProtocol.Header.TOKEN, PrefUtils.getString(PrefConstants.Token.TOKEN, ""));
            jsonObject.put(HttpProtocol.Header.UID, PrefUtils.getString(PrefConstants.UserInfo.UID, ""));
            jsonObject.put(HttpProtocol.Header.LANGUAGE, Languages.getInstance().getLanguage());
            jsonObject.put(HttpProtocol.Header.COUNTRY, mCountryCode);
            jsonObject.put(HttpProtocol.Header.DEVICE_ID, AppUtil.getDeviceId(mContext));
            jsonObject.put(HttpProtocol.Header.PRD_ID,HttpProtocol.ProducteId.PRD_ID);
            jsonObject.put(HttpProtocol.Header.APP_ID,HttpProtocol.AppId.APP_ID);
            String returnJson = jsonObject.toString();
//            HBLog.i(TAG+" returnJson "+returnJson);
            return returnJson;
        } catch (JSONException e) {
            //ignore
        }
        return null;
    }

    private <T> int asyncMockPost(final boolean callOnUi,String url, Map<String, Object> params, final JsonCallBack<T> jsonCallBack) {
        final String responseString = mMockMaker.createMock(url, params);
        if(TextUtils.isEmpty(responseString)){
            return 0;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) (0.5*1000));
                        org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
                        int code = jsonObject.getInt("code");
                        String msg = jsonObject.getString("msg");
                        if (code == HttpProtocol.CODE.OK) {
                            String object = jsonObject.getString("data");
                            T t = parseResponse(object, jsonCallBack.getObjectClass());
                            notifyResponeSuccess(callOnUi,t, jsonCallBack);
                        } else {
                            notifyReponseFail(callOnUi,code, msg, null, jsonCallBack);
                        }
                    } catch (Exception e) {
                        HttpLog.w(TAG," asyncMockPost "+e);
                        notifyReponseFail(callOnUi,HttpProtocol.CODE.NET_ERROR, "", e, jsonCallBack);
                    }
                }
            }).start();
            return 1;
        }
    }

    private <T> T syncHttpPost(String url,Map<String, Object> params,Class<T> className) throws HttpError{
        HttpLog.d(TAG, " syncHttpPost url:" + url);
        Call call = buildHttpCall(url, params);
        try {
            Response response = call.execute();
            String responseString = response.body().string();
            HttpLog.d(TAG, " handleResponse step:" + responseString);
            if (HttpProtocol.ENCRY_ENABLE) {
                responseString = decryptString(responseString);
            }
            HttpLog.d(TAG, " handleResponse " + responseString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            if (code == HttpProtocol.CODE.OK) {
                String object = jsonObject.getString("data");
                return parseResponse(object, className);
            } else {
                throw new HttpError(code,msg,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpLog.w(TAG, " handleResponse "+ e);
            throw new HttpError(-1,"",e);
        }
    }

    public void asyncPostPics(String url,Map<String, Object> params,List<File> files){
        List<RequestBody> fileBodys = new ArrayList<>();
        for (File file : files) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            fileBodys.add(fileBody);
        }
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBuilder.addPart(buildDefaultHeader(), toRequestBody(url,params));

        for (RequestBody fileBody : fileBodys) {
            multipartBuilder.addPart(buildDefaultHeader(), fileBody);
        }
        RequestBody requestBody = multipartBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                HttpLog.w(TAG, "error "+ e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                HttpLog.w(TAG, response.body().string());
            }
        });
    }

    private <T> void notifyReponseFail(boolean callOnUi,final int code, final String message, final Exception e, final JsonCallBack<T> jsonCallBack) {
        if(callOnUi){
            mHandler.post(new Runnable() {
                public void run() {
                    jsonCallBack.onFailed(code, message, e);
                }
            });
        } else {
            jsonCallBack.onFailed(code, message, e);
        }
    }

    private <T> void notifyResponeSuccess(boolean callOnUi,final T t, final JsonCallBack<T> jsonCallBack) {
        if(callOnUi){
            mHandler.post(new Runnable() {
                public void run() {
                    jsonCallBack.onSuccess(t);
                }
            });
        } else {
            jsonCallBack.onSuccess(t);
        }
    }

    private <T> void handleResponse(final boolean callOnUi,Response response, JsonCallBack<T> jsonCallBack) throws IOException {
        handleResponse(callOnUi,true,response,jsonCallBack);
    }

    private <T> void handleResponse(final boolean callOnUi,boolean encryEnable,Response response, JsonCallBack<T> jsonCallBack) throws IOException {
        try {
            String responseString = response.body().string();
            HttpLog.d(TAG, " handleResponse step:" + responseString);
            if (encryEnable) {
                responseString = decryptString(responseString);
            }
            HttpLog.d(TAG, " handleResponse " + responseString);
            org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            if (code == HttpProtocol.CODE.OK) {
                String object = jsonObject.getString("data");
                T t = parseResponse(object, jsonCallBack.getObjectClass());
                notifyResponeSuccess(callOnUi,t, jsonCallBack);
            } else {
                notifyReponseFail(callOnUi,code, msg, null, jsonCallBack);
            }
        } catch (Exception e) {
            notifyReponseFail(callOnUi,HttpProtocol.CODE.NET_ERROR, "json error", e, jsonCallBack);
            HttpLog.w(TAG, " handleResponse "+ e);
        }
    }

    public  <T> T parseResponse(String response, Class<T> className) {
        final Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, className);
    }

    private String buildUrl(String url){
        StringBuilder sb = new StringBuilder(url);
        if(url.contains("?")){
            sb.append("&");
        } else {
            sb.append("?");
        }
        sb.append(HttpProtocol.Header.COUNTRY);
        sb.append("=");
        sb.append(mCountryCode);
        sb.append("&");
        sb.append(HttpProtocol.Header.LANGUAGE);
        sb.append("=");
        sb.append(Languages.getInstance().getLanguage());
        sb.append("&");
        sb.append(HttpProtocol.Header.APP_ID);
        sb.append("=");
        sb.append(HttpProtocol.AppId.APP_ID);

        return sb.toString();
    }

    private Headers buildDefaultHeader() {
        Headers.Builder builder = new Headers.Builder();
        builder.add(HttpProtocol.Header.CIPHER_SPEC, "1");
        builder.add(HttpProtocol.Header.PACKAGE_NAME,GlobalContext.get().getPackageName());
        builder.add(HttpProtocol.Header.PACKAGE_VER, String.valueOf(AppUtil.getVersionCode(GlobalContext.get())));
//        builder.add(HttpProtocol.Header.TOKEN, PrefUtils.getString(PrefConstants.Network.TOKEN,""));
        return builder.build();
    }

    public Headers buildFileUploadHeader(){
        Headers.Builder builder = new Headers.Builder();
        builder.add(HttpProtocol.Header.CIPHER_SPEC, "1");
        builder.add(HttpProtocol.Header.PACKAGE_NAME,GlobalContext.get().getPackageName());
        builder.add(HttpProtocol.Header.PACKAGE_VER, String.valueOf(AppUtil.getVersionCode(GlobalContext.get())));
        builder.add(HttpProtocol.Header.TOKEN, PrefUtils.getString(PrefConstants.Token.TOKEN, ""));
        builder.add(HttpProtocol.Header.UID, PrefUtils.getString(PrefConstants.UserInfo.UID, ""));
        return builder.build();
    }

    public String parseResponse(String responseString){
        if (HttpProtocol.ENCRY_ENABLE) {
            return decryptString(responseString);
        }
        return responseString;
    }

    private String decryptString(String response) {
        String prefix = response.substring(0, 8);
        String fixResponse = response.substring(8);
        String realKey = Md5Util.md5Hex(prefix + HttpProtocol.Secure.AES_KEY);
        byte[] binaryKey = AesUtil.hex2byte(realKey);
        return AesUtil.decrypt(fixResponse, binaryKey);
    }

    private String decodeFileUrl(String url){
        if(url.contains("cdn.avazu.net")){
            return url.replace("cdn.avazu.net","192.168.5.254");
        }
        return url;
    }

    public interface JsonCallBack<T>  {
        void onSuccess(T t);
        void onFailed(int code, String message, Exception e);
        Class<T> getObjectClass();
    }

}
