package com.h520t.assistant.search.activity.search;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.h520t.assistant.R;
import com.h520t.assistant.search.activity.information.GPAInformationActivity;
import com.h520t.assistant.search.call_back_impl.IGPACallBack;
import com.h520t.assistant.search.call_back_impl.ILoginCallBack;
import com.h520t.assistant.search.util.Constant;
import com.h520t.assistant.search.util.GPAUtils;
import com.h520t.assistant.search.util.LoginUtils;

public class GPAActivity extends AppCompatActivity {

    private static final String TAG = "GPAActivity";
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
    private ProgressDialog mDialog;
    private CheckBox rememberPass;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    IGPACallBack mGPACallBack = new IGPACallBack() {
        @Override
        public void failedSemester() {
            runOnUiThread(() -> {
                mDialog.cancel();
                mBtn_login.setText("查询绩点");
                Toast.makeText(GPAActivity.this, "还无法查询该学期的成绩", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mVerifyEt.setText("");
            });
        }

        @Override
        public void successOfSemester() {
            Intent intent = new Intent(GPAActivity.this, GPAInformationActivity.class);
            Log.i(TAG, "failedSemester: "+Constant.sGPABeans);
            intent.putExtra(GPAInformationActivity.TOOLBAR_TITLE, year + " " + semester + " 绩点");
            startActivity(intent);
        }
    };

    private ILoginCallBack mLoginCallBack = new ILoginCallBack() {
        @Override
        public void failedGet() {
            runOnUiThread(() -> {
                if (mDialog.isShowing())
                    mDialog.cancel();
                mBtn_login.setText("查询绩点");
                Toast.makeText(GPAActivity.this, "检查网络状态,教务网登陆可能需要内网", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void setVerifyImg(Bitmap bitmap) {
            runOnUiThread(() -> {
                mBtn_login.setText("查询绩点");
                if (bitmap!=null) {
                    verifyImage.setImageBitmap(bitmap);
                }
            });
        }
        @Override
        public void failedStudentID() {
            runOnUiThread(() -> {
                mDialog.cancel();
                mBtn_login.setText("查询绩点");
                Toast.makeText(GPAActivity.this, "用户名不存在或未按照要求参加教学活动", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mStudentIDEt.setText("");
                mVerifyEt.setText("");
            });
        }
        @Override
        public void failedPassword() {
            runOnUiThread(() -> {
                mDialog.cancel();
                mBtn_login.setText("查询绩点");
                Toast.makeText(GPAActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mPasswordEt.setText("");
                mVerifyEt.setText("");
            });
        }
        @Override
        public void failedVerifyCode() {
            runOnUiThread(() -> {
                mDialog.cancel();
                mBtn_login.setText("查询绩点");
                Toast.makeText(GPAActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
                mVerifyEt.setText("");
            });
        }
        @Override
        public void failedMessage() {
            runOnUiThread(() -> {
                mDialog.cancel();
                mBtn_login.setText("查询绩点");
                Toast.makeText(GPAActivity.this, "填写完整信息", Toast.LENGTH_SHORT).show();
                mLoginUtils.getCookie();
            });
        }

        @Override
        public void successLogin(String xm) {
            runOnUiThread(()->{
                mEditor = mPreferences.edit();
                if (rememberPass.isChecked()){
                    mEditor.putBoolean("remember_password",true);
                    mEditor.putString("account",studentID);
                    mEditor.putString("password",password);
                }else {
                    mEditor.clear();
                }
                mEditor.apply();
            });
            GPAUtils gpaUtils = new GPAUtils(studentID,mGPACallBack,year,semester);
            gpaUtils.loginGPA(xm);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa);
        initView();
    }

    private void initView() {
        mBtn_login = findViewById(R.id.gpa_login_bt);
        verifyImage = findViewById(R.id.gpa_verification_icon);
        mVerifyCodeBt = findViewById(R.id.gpa_verification_bt);
        mStudentIDEt  = findViewById(R.id.gpa_studentID);
        mPasswordEt = findViewById(R.id.gpa_password);
        mVerifyEt = findViewById(R.id.gpa_security_code);
        mToolbar = findViewById(R.id.gpa_search_toolbar);
        rememberPass = findViewById(R.id.gpa_remember_password);
        mLoginUtils = new LoginUtils();
        mLoginUtils.setLoginCallBack(mLoginCallBack);
        mLoginUtils.getCookie();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = mPreferences.getBoolean("remember_password",false);
        if (isRemember){
            String account = mPreferences.getString("account","");
            String password = mPreferences.getString("password","");
            mStudentIDEt.setText(account);
            mPasswordEt.setText(password);
            rememberPass.setChecked(true);
        }

        initViewClick();
    }

    public void initViewClick() {
        mToolbar.setNavigationOnClickListener(view -> finish());
        //选择学年和学期
        String[] yearSelect = getResources().getStringArray(R.array.years_select);
        String[] semesterSelect = getResources().getStringArray(R.array.semester_select);
        Spinner yearsSpinner = findViewById(R.id.gpa_years_select);
        yearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                year = yearSelect[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        Spinner semesterSpinner = findViewById(R.id.gpa_semester_select);
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
            Button loginBt = view.findViewById(R.id.gpa_login_bt);
            loginBt.setText("查询中...");
            mDialog = new ProgressDialog(this);
            mDialog.setTitle("提示");
            mDialog.setMessage("正在查询");
            mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", (dialogInterface, i) ->finish());
            mDialog.show();

            studentID = mStudentIDEt.getText().toString();
            password = mPasswordEt.getText().toString();
            verifyCode = mVerifyEt.getText().toString();
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
        if (mDialog!=null&&mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
