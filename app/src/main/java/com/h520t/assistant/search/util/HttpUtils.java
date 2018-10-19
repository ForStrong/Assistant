package com.h520t.assistant.search.util;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtils {
    
    private HttpUtils(){
        
    }

    public static void doGet(String url, Callback callback , Map<String ,String> headers){
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        if (headers!=null&&headers.size()>0) {
            for(Map.Entry<String,String>  header: headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        Request request = builder.build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }
    
    public static void doPost(String url, Callback callback , Map<String ,String> headers ,Map<String,String> params){
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (headers!=null&&headers.size()>0) {
            for(Map.Entry<String,String>  header: headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        if (params!=null&&params.size()>0) {
            for (Map.Entry<String,String> param  : params.entrySet()) {
                if (param.getKey()!=null&&param.getValue()!=null) {
                    bodyBuilder.add(param.getKey(),param.getValue());
                }else {
                    return;
                }
            }
        }
        RequestBody body = bodyBuilder.build();
        Request request = builder.post(body).build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }
    
    private static OkHttpClient getInstance(){
        return HttpUtilsHolder.CLIENT;
    }
    
    
    private static class  HttpUtilsHolder {
        private static final OkHttpClient CLIENT = new OkHttpClient.Builder().cookieJar(new MyCookieJar()).connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(6,TimeUnit.SECONDS).writeTimeout(6,TimeUnit.SECONDS).build();
    }
}
