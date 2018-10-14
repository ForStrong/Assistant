package com.h520t.assistant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.h520t.assistant.fragment.LAFFragment;
import com.h520t.assistant.fragment.SearchFragment;
import com.h520t.assistant.fragment.WebsiteFragment;
import com.h520t.assistant.util.BottomNavigationViewHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
            return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    private FragmentManager fm;
    private Fragment currentF;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    fragment = SearchFragment.getInstance();
                    break;

                case R.id.navigation_lostGoods:
                    fragment = LAFFragment.getInstance();
                    break;

                case R.id.navigation_website:
                    fragment = WebsiteFragment.getInstance();
                    break;
            }
            replaceFragment(fragment);
            return true;
        });
        navigation.setSelectedItemId(R.id.navigation_search);
    }


    public void replaceFragment(Fragment fragment){
        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (currentF == null){
            transaction.add(R.id.frame_layout,fragment);
        }else if(!fragment.isAdded()){
            transaction.hide(currentF).add(R.id.frame_layout,fragment);
        }else{
            transaction.hide(currentF).show(fragment);
        }
        currentF = fragment;
        transaction.commit();
    }


}
