package com.h520t.assistant.search.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.h520t.assistant.search.bean.ScoreBean;
import com.h520t.assistant.search.call_back_impl.IScoreCallback;

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

public class SearchScoreUtils {
    private String mStudentID;
    private IScoreCallback mScoreCallBack;
    private String mYear,mSemester;
    private static final String TAG = "SearchScoreUtils";

    public SearchScoreUtils(String studentID, IScoreCallback scoreCallBack, String year, String semester) {
        mStudentID = studentID;
        mScoreCallBack = scoreCallBack;
        mYear = year;
        mSemester = semester;
    }

    public void loginScore(String xm){
        final String loginScoreUrl = Constant.SCORE_SEARCH_URL+mStudentID+"&xm=" + xm + Constant.SCORE_SEARCH_GNMKDM;
        Callback callback = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String html = Objects.requireNonNull(response.body()).string();
                String viewstate = Jsoup.parse(html).select("input[name=__VIEWSTATE]").val();
                scoreSearch(xm, loginScoreUrl, viewstate);
            }
        };

        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie", Constant.sCookie);
        headers.put("Referer", Constant.GET_LOGIN_URL+mStudentID);
        headers.put("Host", Constant.HOST);
        HttpUtils.doGet(loginScoreUrl,callback,headers);
    }

    //-------------------------------------------post成绩查询--------------------------------------------------------------------//
    private void scoreSearch(String xm, String loginScoreUrl, String viewState) {
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
                for (int i = 16; i < arrayList.size(); i=i+16) {
                    List<String> strings = arrayList.subList(i, i+16);
                    Constant.sScoreBeans.add(new ScoreBean(strings.get(3),strings.get(4),strings.get(6),strings.get(7)
                            ,strings.get(8),strings.get(9),strings.get(10),strings.get(11)));
                }
                Log.i(TAG, "onResponse: "+Constant.sScoreBeans.toString()+"size is :  "+Constant.sScoreBeans.size());
                if (!(Constant.sScoreBeans.size()>0)){
                    mScoreCallBack.failedSemester();
                }else{
                    mScoreCallBack.successGetGrade();
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
        params.put("gnmkdm", "N121605");
        params.put("ASP.NET_SessionId", Constant.sCookie);
        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", viewState);
        params.put("ddlxn", mYear);
        params.put("ddlxq", mSemester);
        params.put("btnCx", "查询");
        HttpUtils.doPost(loginScoreUrl,callback,headers,params);
    }

}
