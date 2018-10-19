package com.h520t.assistant.search.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.h520t.assistant.search.call_back_impl.ILoginCallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginUtils {
    private String mStudentID,mPassword,mVerifyCode;
    private ILoginCallBack mLoginCallBack;
    private static final String TAG = "LoginUtils";
    public LoginUtils() { }

    public void setStudentID(String studentID) {
        mStudentID = studentID;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setVerifyCode(String verifyCode) {
        mVerifyCode = verifyCode;
    }

    public void setLoginCallBack(ILoginCallBack loginCallBack) {
        mLoginCallBack = loginCallBack;
    }


    public  void getCookie() {
        Constant.sCookie = null;
        Constant.sCookieList = null;
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mLoginCallBack.failedGet();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] verificationCode = Objects.requireNonNull(response.body()).bytes();
                    if (verificationCode != null && verificationCode.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode.length);
                        Bitmap resizeBitmap = changeBitmapSize(bitmap);
                        mLoginCallBack.setVerifyImg(resizeBitmap);
                    }
                }else {
                    mLoginCallBack.failedGet();
                }
            }
        };
        HttpUtils.doGet(Constant.CHECK_CODE_URL,callback,null);
    }

    public void loginPost(){
        if (!(TextUtils.isEmpty(mVerifyCode)||TextUtils.isEmpty(mStudentID)||TextUtils.isEmpty(mPassword))) {
            Callback callback = new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mLoginCallBack.failedGet();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String name = "";
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: "+response.isSuccessful());
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
                            mLoginCallBack.failedVerifyCode();
                            return;
                        }
                    }
                    loginGet(name);
                }
            };
            Map<String,String> params = new HashMap<>();
            params.put("ASP.NET_SessionId",Constant.sCookie);
            params.put("__VIEWSTATE", Constant.LOGIN_VIEWSTATE);
            params.put("txtUserName", mStudentID);
            params.put("Textbox1","");
            params.put("TextBox2",mPassword);
            params.put("txtSecretCode", mVerifyCode);
            params.put("RadioButtonList1","学生");
            params.put("Button1","");
            params.put("lbLanguage","");
            params.put("hidPdrs","");
            params.put("hidsc","");
            HttpUtils.doPost(Constant.POST_LOGIN_URL,callback,null,params);
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
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mLoginCallBack.failedGet();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String xm = URLEncoder.encode(name, "GB2312");
                    mLoginCallBack.successLogin(xm);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        };
        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie", Constant.sCookie);
        headers.put("Referer", Constant.GET_LOGIN_URL+mStudentID);
        headers.put("Host", Constant.HOST);
        HttpUtils.doGet(Constant.GET_LOGIN_URL+mStudentID,callback,headers);
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
