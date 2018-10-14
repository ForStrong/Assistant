package com.h520t.assistant.search.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.h520t.assistant.search.call_back_impl.LoginCallBackImpl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressLint("Registered")
public class LoginUtil  {
    private String mStudentID,mPassword,mVerifyCode;
    private LoginCallBackImpl mLoginCallBack;
    private SearchScoreUtil mScoreUtil;


    private CookieJar mCookieJar = new CookieJar() {
        @Override
        public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {}
        @Override
        public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
            return Constant.sCookieList;
        }
    };

    public LoginUtil() {
    }

    public void setScoreUtil(SearchScoreUtil scoreUtil) {
        mScoreUtil = scoreUtil;
    }

    public void setStudentID(String studentID) {
        mStudentID = studentID;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setVerifyCode(String verifyCode) {
        mVerifyCode = verifyCode;
    }

    public void setLoginCallBack(LoginCallBackImpl loginCallBack) {
        mLoginCallBack = loginCallBack;
    }


    public  void getCookie() {
        Constant.sCookie = null;
        Constant.sCookieList = null;
        OkHttpClient mCookieClient = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                        Constant.sCookieList = cookies;
                        Constant.sCookie = Constant.sCookieList.toString();
                    }
                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                }).build();
        Request request = new Request.Builder().url(Constant.CHECK_CODE_URL).build();
        mCookieClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                byte[] verificationCode = Objects.requireNonNull(response.body()).bytes();
                if (verificationCode != null && verificationCode.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode.length);
                    Constant.verifyBitmap = changeBitmapSize(bitmap);
                    mLoginCallBack.setVerifyImg();
                }
            }
        });
    }

    public void loginPost(){
        if (!(TextUtils.isEmpty(mVerifyCode)||TextUtils.isEmpty(mStudentID)||TextUtils.isEmpty(mPassword))) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("ASP.NET_SessionId", Constant.sCookie)
                    .add("__VIEWSTATE", Constant.LOGIN_VIEWSTATE)
                    .add("txtUserName", mStudentID)
                    .add("Textbox1", "")
                    .add("TextBox2", mPassword)
                    .add("txtSecretCode", mVerifyCode)
                    .add("RadioButtonList1", "学生")
                    .add("Button1", "")
                    .add("lbLanguage", "")
                    .add("hidPdrs", "")
                    .add("hidsc", "").build();
            Request request = new Request.Builder()
                    .url(Constant.POST_LOGIN_URL)
                    .post(requestBody).build();
            OkHttpClient loginPostClient = new OkHttpClient().newBuilder().
                    cookieJar(mCookieJar).build();
            loginPostClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) { }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String name = "";
                    if (response.isSuccessful()) {
                        String html = Objects.requireNonNull(response.body()).string();
                        Document parse = Jsoup.parse(html);
                        if (!isSuccessLogin(parse))
                            return;
                        //获取名字
                        Elements info = parse.getElementsByClass("info");
                        if (info.size()>0) {
                            Element li = info.get(0);
                            Elements select = li.select("span[id=xhxm]");
                            String text = select.text();
                            name = text.substring(0, text.length() - 2);
                        }else{
                            mLoginCallBack.setVerifyImg();
                        }
                        //                            Log.i(TAG, "onResponse: html  "+html);
                    }
                    loginGet(name);
                }
            });
        }else{
            mLoginCallBack.failedMessage();

        }
    }
    private boolean isSuccessLogin(Document parse) {
        Elements scripts = parse.getElementsByTag("script");
        for (Element script : scripts ) {
            final String text = script.html();
            if (text.contains("验证码不正确")){
                mLoginCallBack.failedVerifyCode();
                return false;
            }
            if (text.contains("用户名不存在或未按照要求参加教学活动")){
                mLoginCallBack.failedStudentID();
                return false;
            }
            if (text.contains("密码错误")){
                mLoginCallBack.failedPassword();
                return false;
            }
        }
        return true;
    }

    private void loginGet(final String name){
        Request request = new Request.Builder()
                .url(Constant.GET_LOGIN_URL+mStudentID)
                .addHeader("Cookie", Constant.sCookie)
                .addHeader("Referer", Constant.GET_LOGIN_URL+mStudentID)
                .addHeader("Host", Constant.HOST)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().cookieJar(mCookieJar).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        mScoreUtil.loginScore(name,mStudentID,mCookieJar);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static Bitmap changeBitmapSize(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleX = (float) 140 / w;
        float scaleY = (float) 60 / h;
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }
}
