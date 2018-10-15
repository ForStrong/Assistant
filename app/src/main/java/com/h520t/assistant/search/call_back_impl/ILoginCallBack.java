package com.h520t.assistant.search.call_back_impl;

public interface ILoginCallBack {
    void setVerifyImg();
    void failedStudentID();
    void failedPassword();
    void failedVerifyCode();
    void failedMessage();
}
