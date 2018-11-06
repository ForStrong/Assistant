package com.h520t.assistant.search.util;


import android.support.annotation.NonNull;
import android.util.Log;

import com.h520t.assistant.search.bean.GPABean;
import com.h520t.assistant.search.call_back_impl.IGPACallBack;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GPAUtils {
    private String mStudentID;
    private IGPACallBack mGPACallBack;
    private String mYear,mSemester;
    private static final String TAG = "GPAUtils";
    public GPAUtils(String studentID, IGPACallBack GPACallBack, String year, String semester) {
        mYear = year;
        mSemester = semester;
        mStudentID = studentID;
        mGPACallBack = GPACallBack;
    }

    public void loginGPA(String xm){
        final String loginScoreUrl = Constant.GPA_SEARCH_URL+mStudentID+"&xm=" + xm + Constant.GPA_GNMKDM;
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String html = Objects.requireNonNull(response.body()).string();
                String viewstate = Jsoup.parse(html).select("input[name=__VIEWSTATE]").val();
                GPASearch(xm, loginScoreUrl, viewstate);
            }
        };

        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie", Constant.sCookie);
        headers.put("Referer", Constant.GPA_SEARCH_URL+mStudentID);
        headers.put("Host", Constant.HOST);
        HttpUtils.doGet(loginScoreUrl,callback,headers);
    }

    private void GPASearch(String xm, String loginScoreUrl, String viewState) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ArrayList<String> arrayList = new ArrayList<>();
                String s = Objects.requireNonNull(response.body()).string();
                Document parse = Jsoup.parse(s);
                Elements alt = parse.getElementsByClass("datelist");
                Elements trs = alt.tagName("tr");
                for (Element tr  : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    for (Element td : tds) {
                        arrayList.add(td.text());
                    }
                }
                for (int i = 15; i < arrayList.size(); i=i+15) {
                    List<String> strings = arrayList.subList(i, i+15);
                    Constant.sGPABeans.add(new GPABean(strings.get(3),strings.get(4),strings.get(6),strings.get(7)
                            ,strings.get(8),strings.get(10),strings.get(11)));
                }
                Log.i(TAG, "onResponse: "+Constant.sGPABeans.toString()+"size is :  "+Constant.sGPABeans.size());
                if (!(Constant.sGPABeans.size()>0)){
                    mGPACallBack.failedSemester();
                }else{
                    mGPACallBack.successOfSemester();
                }
            }
        };
        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie", Constant.sCookie);
        headers.put("Referer", loginScoreUrl);
        headers.put("Host", Constant.HOST);

        Map<String,String> params = new HashMap<>();
        params.put("xh", mStudentID);
        params.put("xm", xm);
        params.put("gnmkdm", "N121617");
        params.put("ASP.NET_SessionId", Constant.sCookie);
        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", viewState);
        params.put("hidLanguage", "");
        params.put("ddlXN", mYear);
        params.put("ddlXQ", mSemester);
        params.put("ddl_kcxz","");
        params.put("btn_xq", "学期成绩");
        HttpUtils.doPost(loginScoreUrl,callback,headers,params);
    }


}
