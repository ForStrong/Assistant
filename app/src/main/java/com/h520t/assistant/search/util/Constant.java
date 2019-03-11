package com.h520t.assistant.search.util;

import com.h520t.assistant.search.bean.GPABean;
import com.h520t.assistant.search.bean.ScoreBean;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;

public class Constant {

    public static List<Cookie> sCookieList;
    public static String sCookie;
    //登陆部分
    public static final String CHECK_CODE_URL = "http://202.119.160.5/CheckCode.aspx";
    public static final String POST_LOGIN_URL = "http://202.119.160.5/default2.aspx";
    public static final String HOST = "202.119.160.5";
    public static final String GET_LOGIN_URL = " http://202.119.160.5/xs_main.aspx?xh=";
    public static  String URL = "http://202.119.160.5" ;//请求_VIEWSTATE

    //成绩查询参数
    //实验期中成绩查询
    public static ArrayList<ScoreBean> sScoreBeans = new ArrayList<>();
    public static final String SCORE_SEARCH_URL = "http://202.119.160.5/xscjcx_dq.aspx?xh=";
    public static final String SCORE_SEARCH_GNMKDM = "&gnmkdm=N121605";

    //绩点成绩查询
    public static ArrayList<GPABean> sGPABeans = new ArrayList<>();
    public static final String GPA_SEARCH_URL = "http://202.119.160.5/xscjcx.aspx?xh=";
    public static final String GPA_GNMKDM = "&gnmkdm=N121617";

}
