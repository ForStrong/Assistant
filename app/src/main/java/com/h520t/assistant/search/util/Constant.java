package com.h520t.assistant.search.util;

import com.h520t.assistant.search.bean.GPABean;
import com.h520t.assistant.search.bean.ScoreBean;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;

public class Constant {

    public static List<Cookie> sCookieList;
    public static String sCookie;

    public static final String CHECK_CODE_URL = "http://jwjx.njit.edu.cn/CheckCode.aspx";
    public static final String POST_LOGIN_URL = "http://jwjx.njit.edu.cn/default2.aspx";
    public static final String LOGIN_VIEWSTATE = "dDwxNTMxMDk5Mzc0Ozs+nttZfhG6oXx92HiCAr5KIDfqE08=";
    public static final String HOST = "jwjx.njit.edu.cn";
    public static final String GET_LOGIN_URL = " http://jwjx.njit.edu.cn/xs_main.aspx?xh=";

    //成绩查询参数
    //实验期中成绩查询
    public static ArrayList<ScoreBean> sScoreBeans = new ArrayList<>();
    public static final String SCORE_SEARCH_URL = "http://jwjx.njit.edu.cn/xscjcx_dq.aspx?xh=";
    public static final String SCORE_SEARCH_GNMKDM = "&gnmkdm=N121605";
    //绩点成绩查询
    public static ArrayList<GPABean> sGPABeans = new ArrayList<>();
    public static final String GPA_SEARCH_URL = "http://jwjx.njit.edu.cn/xscjcx.aspx?xh=";
    public static final String GPA_GNMKDM = "&gnmkdm=N121617";

}
