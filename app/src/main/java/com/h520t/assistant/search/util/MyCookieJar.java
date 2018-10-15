package com.h520t.assistant.search.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 自动化管理cookie
 */
public class MyCookieJar implements CookieJar {
    @Override
    public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> cookies) {
        Constant.sCookieList =  cookies;
        Constant.sCookie = Constant.sCookieList.toString();
    }

    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
       return Constant.sCookieList != null?Constant.sCookieList:new ArrayList<>();
    }

}
