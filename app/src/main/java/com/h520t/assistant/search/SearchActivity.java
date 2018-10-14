package com.h520t.assistant.search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.h520t.assistant.R;
import com.h520t.assistant.search.util.Constant;
import com.h520t.assistant.search.util.ScoreBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SearchActivity extends AppCompatActivity {
    public static final String IS_GRADE = "isGrade";
    private static final String TAG = "SearchActivity";
    byte[] verificationCode;
    private ImageView verifyImage;
    private OkHttpClient mCookieClient;
    private CookieJar mCookieJar;
    private String studentID;
    private EditText mStudentIDEt;
    private EditText mPasswordEt;
    private EditText mSecurityCode;
    private String year="2015-2016"
            ,semester="1";
    public SearchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }
    private void initView() {
        boolean isGrade = getIntent().getBooleanExtra(IS_GRADE, true);
        Button btn_login = findViewById(R.id.login_bt);

        //是查成绩还是查绩点
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
        if (isGrade){
            toolbar.setTitle("成绩查询");
            btn_login.setText("查看成绩");
        }else{
            toolbar.setTitle("绩点查询");
            btn_login.setText("查看绩点");
        }

        //选择学年和学期
        String[] yearSelect = getResources().getStringArray(R.array.years_select);
        String[] semesterSelect = getResources().getStringArray(R.array.semester_select);
        Spinner yearsSpinner = findViewById(R.id.years_select);
        yearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = yearSelect[i];
                Log.i(TAG, "onItemSelected: "+year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        Spinner semesterSpinner = findViewById(R.id.semester_select);
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                semester = semesterSelect[i];
                Log.i(TAG, "onItemSelected: "+semester );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        verifyImage = findViewById(R.id.verification_icon);
        Button verifyCodeBt = findViewById(R.id.verification_bt);
        mCookieClient = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                        Constant.sCookieList = cookies;
                        Constant.sCookie = Constant.sCookieList.toString();
                        Log.e(TAG, "saveFromResponse: " + Constant.sCookie + "," + Constant.sCookie.substring(19, 43));
                    }
                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                }).build();
        getCookie();
        //-----------------------------------------获取cookie和验证码图片------------------------------------------------------//
        //换一张验证码重新获取cookie和图片
        verifyCodeBt.setOnClickListener(view ->  getCookie());
        mStudentIDEt  = findViewById(R.id.studentID);
        mPasswordEt = findViewById(R.id.password);
        mSecurityCode = findViewById(R.id.security_code);
        mCookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {}
            @Override
            public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                return Constant.sCookieList;
            }
        };
        btn_login.setOnClickListener(view -> loginPost());
    }

    private void getCookie() {
        Request request = new Request.Builder().url(Constant.CHECK_CODE_URL).build();
        mCookieClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                verificationCode = Objects.requireNonNull(response.body()).bytes();
                if (verificationCode != null && verificationCode.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(verificationCode, 0, verificationCode.length);
                    final Bitmap resizeBitmap = changeBitmapSize(bitmap);
                    runOnUiThread(() -> {
                                verifyImage.setImageBitmap(resizeBitmap);
                                verificationCode = null;
                            }
                    );
                }
            }
        });
    }
//---------------------------------------------post登陆操作--------------------------------------------------------------------//
    private void loginPost(){
        studentID = mStudentIDEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        String s = mSecurityCode.getText().toString();
        if (!(TextUtils.isEmpty(s)||TextUtils.isEmpty(studentID)||TextUtils.isEmpty(password))) {
            RequestBody requestBody = new FormBody.Builder()
                    .add("ASP.NET_SessionId", Constant.sCookie)
                    .add("__VIEWSTATE", Constant.LOGIN_VIEWSTATE)
                    .add("txtUserName", studentID)
                    .add("Textbox1", "")
                    .add("TextBox2", password)
                    .add("txtSecretCode", s)
                    .add("RadioButtonList1", "学生")
                    .add("Button1", "")
                    .add("lbLanguage", "")
                    .add("hidPdrs", "")
                    .add("hidsc", "").build();
            Request request = new Request.Builder()
                    .url(Constant.POST_LOGIN_URL)
                    .post(requestBody).build();
            OkHttpClient loginPostClient = new OkHttpClient().newBuilder().
                    cookieJar(mCookieJar).build();
            loginPostClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i(TAG, "onFailure: " + e.toString());
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String name = "";
                    if (response.isSuccessful()) {
                        String html = Objects.requireNonNull(response.body()).string();
                        Document parse = Jsoup.parse(html);
                        if (!isSuccessLogin(parse, mSecurityCode, mStudentIDEt, mPasswordEt)) {
                            getCookie();
                            return;
                        }
                        //获取名字
                        Elements info = parse.getElementsByClass("info");
                        if (info.size()>0) {
                            Element li = info.get(0);
                            Elements select = li.select("span[id=xhxm]");
                            String text = select.text();
                            name = text.substring(0, text.length() - 2);
                            Log.i(TAG, "onResponse: " + name);
                        }
                        //                            Log.i(TAG, "onResponse: html  "+html);
                    }
                    loginGet(name);
                }
            });
        }else{
            Toast.makeText(SearchActivity.this, "填写完整信息", Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------------------------------判断验证码，用户名，和密码是否正确----------------------------------------------//
    private boolean isSuccessLogin(Document parse, final EditText securityCode
            , final EditText studentIDEt, final EditText passwordEt) {
        Elements scripts = parse.getElementsByTag("script");
        for (Element script : scripts ) {
            final String text = script.html();
            if (text.contains("验证码不正确")){

                runOnUiThread(() -> {
                    Toast.makeText(SearchActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    securityCode.setText("");
                });
                return false;
            }
            if (text.contains("用户名不存在或未按照要求参加教学活动")){
                runOnUiThread(() -> {
                    Toast.makeText(SearchActivity.this, "用户名不存在或未按照要求参加教学活动", Toast.LENGTH_SHORT).show();
                    studentIDEt.setText("");
                    securityCode.setText("");
                });
                return false;
            }
            if (text.contains("密码错误")){
                runOnUiThread(() -> {
                    Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
                    passwordEt.setText("");
                    securityCode.setText("");
                });
                return false;
            }
        }
        return true;
    }

    //-------------------------------------------get登陆操作--------------------------------------------------------------------//
    private void loginGet(final String name){

        Request request = new Request.Builder()
                .url(Constant.GET_LOGIN_URL+studentID)
                .addHeader("Cookie", Constant.sCookie)
                .addHeader("Referer", Constant.GET_LOGIN_URL+studentID)
                .addHeader("Host", Constant.HOST)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().cookieJar(mCookieJar).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
/*                Log.i(TAG, "onResponse: "+ Objects.requireNonNull(response.body()).string());
                Log.i(TAG, "onResponse: "+response.request().url().toString());*/
                    loginScore(name);
            }
        });
    }

    //---------------------------------------get获取成绩查询页面--------------------------------------------------------------------//
    private void loginScore(String name) throws UnsupportedEncodingException {
        final String xm = URLEncoder.encode(name, "GB2312");
        Log.i(TAG, "loginScore: xm  " + xm);
        final String loginScoreUrl = Constant.SCORE_SEARCH_URL+studentID+"&xm=" + xm + Constant.SCORE_SEARCH_GNMKDM;
        Request request = new Request.Builder().url(loginScoreUrl)
                .addHeader("Host", Constant.HOST)
                .addHeader("Referer", Constant.GET_LOGIN_URL+studentID)
                .addHeader("Cookie", Constant.sCookie).build();
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().cookieJar(mCookieJar).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { }
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
                .add("xh", studentID)
                .add("xm", xm)
                .add("gnmkdm", "N121605")
                .add("ASP.NET_SessionId", Constant.sCookie)
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", viewState)
                .add("ddlxn", year)
                .add("ddlxq", semester)
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
                Log.i(TAG, "onResponse: "+arrayList);
                for (int i = 16; i < arrayList.size(); i=i+16) {
                    List<String> strings = arrayList.subList(i, i+16);
                    Log.i(TAG, "onResponse: "+strings);
                    Constant.sScoreBeans.add(new ScoreBean(strings.get(3),strings.get(4),strings.get(6),strings.get(7)
                            ,strings.get(8),strings.get(9),strings.get(10),strings.get(11)));
                }

                Log.i(TAG, "onResponse: "+Constant.sScoreBeans);
                if (!(Constant.sScoreBeans.size()>0)){
                    getCookie();
                    runOnUiThread(()-> {
                        Toast.makeText(SearchActivity.this, "还无法查询该学期的成绩", Toast.LENGTH_SHORT).show();
                        mSecurityCode.setText("");
                    });
                    return;
                }else {
                    Intent intent = new Intent(SearchActivity.this,ScoreActivity.class);
                    intent.putExtra(ScoreActivity.TOOLBAR_TITLE,year+"学年 "+semester+"学期成绩");
                    startActivity(intent);
                    runOnUiThread(()->mSecurityCode.setText(""));
                    getCookie();
                }

            }
        });
    }

    private Bitmap changeBitmapSize(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleX = (float) 80 / w;
        float scaleY = (float) 30 / h;
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }
}
