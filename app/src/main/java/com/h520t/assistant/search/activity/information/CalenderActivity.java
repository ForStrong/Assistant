package com.h520t.assistant.search.activity.information;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.h520t.assistant.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalenderActivity extends AppCompatActivity {
    private ImageView mImageView;
    private PhotoView mPhotoView;
    private Info mInfo;
    private LinearLayout mLayout;
    private Window mWindow;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        initView();
        initClick();
    }

    private void initClick() {
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(v->finish());

        mPhotoView.setOnClickListener(view -> mPhotoView.animaTo(mInfo, () -> {
            mPhotoView.setVisibility(View.GONE);
            mLayout.setVisibility(View.VISIBLE);
            //设置状态栏颜色
            mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }));

        mImageView.setOnClickListener(view -> {
            mInfo = PhotoView.getImageViewInfo(mImageView);
            mLayout.setVisibility(View.GONE);
            mWindow.setStatusBarColor(getResources().getColor(android.R.color.black));
            mPhotoView.setVisibility(View.VISIBLE);
            mPhotoView.animaFrom(mInfo);
        });
    }

    private void initView() {
        mToolbar = findViewById(R.id.calender_toolbar);
        mWindow = getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        mImageView = findViewById(R.id.calenderIv);
        mPhotoView = findViewById(R.id.calenderPv);
        mLayout = findViewById(R.id.IvLayout);
        TextView dateTv = findViewById(R.id.date);
        mPhotoView.enable();

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateS = dateFormat.format(date);
        dateTv.setText(String.valueOf(dateS+"  南工程助手"));
    }

    @Override
    public void onBackPressed() {
        if (mPhotoView.getVisibility() == View.VISIBLE) {
            mPhotoView.animaTo(mInfo, () -> {
                mPhotoView.setVisibility(View.GONE);
                mLayout.setVisibility(View.VISIBLE);
                mWindow.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            });
        } else {
            super.onBackPressed();
        }
    }
}
