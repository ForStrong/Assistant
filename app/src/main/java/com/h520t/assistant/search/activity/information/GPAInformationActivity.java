package com.h520t.assistant.search.activity.information;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.h520t.assistant.R;
import com.h520t.assistant.search.adapter.GPAAdapter;
import com.h520t.assistant.search.bean.GPABean;
import com.h520t.assistant.search.util.Constant;
import com.h520t.assistant.util.CalcUtils;
import com.jeremyliao.livedatabus.LiveDataBus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class GPAInformationActivity extends AppCompatActivity {
    public static final String TOOLBAR_TITLE = "toolbarTitle";
    private static final String TAG = "GPAInformationActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa_information);
        Log.i(TAG, "11111: "+Constant.sGPABeans);
        Toolbar toolbar = findViewById(R.id.gpa_score_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(v->finish());
        Intent intent = getIntent();
        String title = intent.getStringExtra(GPAInformationActivity.TOOLBAR_TITLE);
        toolbar.setTitle(title);

        //平均学分绩点计算
//        Log.i(TAG, "onCreate: "+creditCount+"    "+count+"    "+divide);
        TextView gradePoint = findViewById(R.id.gradePoint);
        calculateGPA(gradePoint);

        RecyclerView recyclerView = findViewById(R.id.gpa_score_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GPAAdapter adapter = new GPAAdapter(Constant.sGPABeans);
        recyclerView.setAdapter(adapter);

        LiveDataBus.get().with("changeChoice",Boolean.class).observe(this,aBoolean -> calculateGPA(gradePoint));
    }

    private void calculateGPA(TextView gradePoint) {
        Double creditCount = Double.valueOf("0");
        Double count = Double.valueOf("0");
        for (GPABean gpaBean  : Constant.sGPABeans) {
            if (gpaBean.isChoice()) {
                double credit = Double.parseDouble(gpaBean.getCredit());
                double gpa = Double.parseDouble(gpaBean.getGpa());
                creditCount = CalcUtils.add(credit,creditCount);
                Double multiply = CalcUtils.multiply(credit, gpa, 3, RoundingMode.HALF_UP);
                count = CalcUtils.add(multiply,count);
            }
        }
        Double divide = CalcUtils.divide(count, creditCount, 2, RoundingMode.HALF_UP);
        gradePoint.setText(String.format("%s", divide));
    }

    @Override
    protected void onStop() {
        Constant.sGPABeans = new ArrayList<>();
        super.onStop();
    }
}
