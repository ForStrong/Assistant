package com.h520t.assistant.search.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.h520t.assistant.R;
import com.h520t.assistant.search.call_back_impl.ILoginCallBack;
import com.h520t.assistant.search.call_back_impl.IScoreCallback;
import com.h520t.assistant.search.util.Constant;
import com.h520t.assistant.search.util.LoginUtils;
import com.h520t.assistant.search.util.SearchScoreUtils;


public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private ImageView verifyImage;
    private String studentID,password,verifyCode;
    private EditText mStudentIDEt;
    private EditText mPasswordEt;
    private EditText mVerifyEt;
    private String year,semester;
    private Button mVerifyCodeBt;
    private Button mBtn_login;
    private Toolbar mToolbar;

    private LoginUtils mLoginUtils;
    private IScoreCallback mScoreCallBack =  new IScoreCallback() {
        @Override
        public void failedSemester() {
            runOnUiThread(() -> {
                Toast.makeText(SearchActivity.this, "还无法查询该学期的成绩", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mVerifyEt.setText("");
            });
        }

        @Override
        public void successGetGrade() {
            runOnUiThread(() -> {
                Intent intent = new Intent(SearchActivity.this, ScoreActivity.class);
                intent.putExtra(ScoreActivity.TOOLBAR_TITLE, year + "学年 " + semester + "学期成绩");
                startActivity(intent);
            });
            Log.i(TAG, "successGetGrade: " + Constant.sScoreBeans);
        }
    };

    private ILoginCallBack mLoginCallBack = new ILoginCallBack() {
        @Override
        public void setVerifyImg() {
            runOnUiThread(() -> {
                verifyImage.setImageBitmap(Constant.verifyBitmap);
                Constant.verifyBitmap = null;
            });
        }
        @Override
        public void failedStudentID() {
            runOnUiThread(() -> {
                Toast.makeText(SearchActivity.this, "用户名不存在或未按照要求参加教学活动", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mStudentIDEt.setText("");
                mVerifyEt.setText("");
            });
        }
        @Override
        public void failedPassword() {
            runOnUiThread(() -> {
                Toast.makeText(SearchActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mPasswordEt.setText("");
                mVerifyEt.setText("");
            });
        }
        @Override
        public void failedVerifyCode() {
            runOnUiThread(() -> {
                Toast.makeText(SearchActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mVerifyEt.setText("");
            });
        }
        @Override
        public void failedMessage() {
            runOnUiThread(() -> {
                Toast.makeText(SearchActivity.this, "填写完整信息", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        mBtn_login = findViewById(R.id.login_bt);
        verifyImage = findViewById(R.id.verification_icon);
        mVerifyCodeBt = findViewById(R.id.verification_bt);
        mStudentIDEt  = findViewById(R.id.studentID);
        mPasswordEt = findViewById(R.id.password);
        mVerifyEt = findViewById(R.id.security_code);
        mToolbar = findViewById(R.id.search_toolbar);

        mLoginUtils = new LoginUtils();
        mLoginUtils.setLoginCallBack(mLoginCallBack);
        mLoginUtils.getCookie();

        initViewClick();
    }
    public void initViewClick() {
        mToolbar.setNavigationOnClickListener(view -> finish());
        //选择学年和学期
        String[] yearSelect = getResources().getStringArray(R.array.years_select);
        String[] semesterSelect = getResources().getStringArray(R.array.semester_select);
        Spinner yearsSpinner = findViewById(R.id.years_select);
        yearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = yearSelect[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        Spinner semesterSpinner = findViewById(R.id.semester_select);
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                semester = semesterSelect[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        //刷新图片
        mVerifyCodeBt.setOnClickListener(view -> mLoginUtils.getCookie());

        //获取成绩
        mBtn_login.setOnClickListener(view -> {
            studentID = mStudentIDEt.getText().toString();
            password = mPasswordEt.getText().toString();
            verifyCode = mVerifyEt.getText().toString();
            SearchScoreUtils searchScoreUtils = new SearchScoreUtils(year,semester);
            searchScoreUtils.setUIImpl(mScoreCallBack);
            mLoginUtils.setScoreUtil(searchScoreUtils);
            mLoginUtils.setStudentID(studentID);
            mLoginUtils.setPassword(password);
            mLoginUtils.setVerifyCode(verifyCode);
            mLoginUtils.loginPost();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLoginUtils.getCookie();
        mVerifyEt.setText("");
    }
}
