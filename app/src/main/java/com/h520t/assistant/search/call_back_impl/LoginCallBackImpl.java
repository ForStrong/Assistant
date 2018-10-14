package com.h520t.assistant.search.call_back_impl;

/**
 * @author Administrator
 * @des ${TODO}
 * @Version $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public interface LoginCallBackImpl {
    void setVerifyImg();
    void failedStudentID();
    void failedPassword();
    void failedVerifyCode();
    void failedMessage();
}
