package com.h520t.assistant.search.call_back_impl;

import android.graphics.Bitmap;

public interface ILoginCallBack {
    void failedGet();
    void setVerifyImg(Bitmap bitmap);
    void failedStudentID();
    void failedPassword();
    void failedVerifyCode();
    void failedMessage();
    void successLogin(String xm);
}
