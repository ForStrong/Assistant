package com.h520t.assistant.search.util;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.h520t.assistant.search.bean.ScoreBean;
import com.h520t.assistant.search.call_back_impl.ScoreCallbackImpl;
import com.jeremyliao.livedatabus.LiveDataBus;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchScoreUtil  {
    private String mStudentID;
    private ScoreCallbackImpl mScoreCallBack;
    private CookieJar mCookieJar;
    private String mYear,mSemester;

    public SearchScoreUtil(String year,String semester) {
        getMessage();
        mYear = year;
        mSemester = semester;
    }

    public void setStudentID(String studentID) {
        mStudentID = studentID;
    }

    public void setUIImpl(ScoreCallbackImpl UIImpl) {
        mScoreCallBack = UIImpl;
    }

    private void getMessage(){
        LiveDataBus.get().with("isSuccess",String.class).observeForever(isSuccess -> {
            assert isSuccess != null;
            switch (isSuccess) {
                case "failedSemester":
                    mScoreCallBack.failedSemester();
                    break;
                case "successGetGrade":
                    mScoreCallBack.successGetGrade();
                    break;
                default:
                    break;
            }
        });
    }

    public void loginScore(String name, String studentID, CookieJar cookieJar) throws UnsupportedEncodingException {
        mStudentID = studentID;
        mCookieJar = cookieJar;
        final String xm = URLEncoder.encode(name, "GB2312");
        final String loginScoreUrl = Constant.SCORE_SEARCH_URL+mStudentID+"&xm=" + xm + Constant.SCORE_SEARCH_GNMKDM;
        Request request = new Request.Builder().url(loginScoreUrl)
                .addHeader("Host", Constant.HOST)
                .addHeader("Referer", Constant.GET_LOGIN_URL+mStudentID)
                .addHeader("Cookie", Constant.sCookie).build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().cookieJar(mCookieJar).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
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
        });

    }

    //-------------------------------------------post成绩查询--------------------------------------------------------------------//
    private void scoreSearch(String xm, String loginScoreUrl, String viewState) {
        RequestBody requestBody = new FormBody.Builder()
                .add("xh", mStudentID)
                .add("xm", xm)
                .add("gnmkdm", "N121605")
                .add("ASP.NET_SessionId", Constant.sCookie)
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", viewState)
                .add("ddlxn", mYear)
                .add("ddlxq", mSemester)
                .add("btnCx", "查询").build();

        Request request = new Request.Builder().url(loginScoreUrl)
                .addHeader("Host", Constant.HOST)
                .addHeader("Referer", loginScoreUrl)
                .addHeader("Cookie", Constant.sCookie)
                .post(requestBody).build();

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().cookieJar(mCookieJar).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
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
                if (!(Constant.sScoreBeans.size()>0)){
                    LiveDataBus.get().with("isSuccess").postValue("failedSemester");
                }else{
                    mScoreCallBack.successGetGrade();
                }
            }
        });
    }

}
