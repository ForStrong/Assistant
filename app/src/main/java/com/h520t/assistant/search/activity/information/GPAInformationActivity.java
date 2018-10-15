package com.h520t.assistant.search.activity.information;

import android.content.Intent;
import android.os.Bundle;
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

import java.math.BigDecimal;
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

     /*   BigDecimal multiply = new BigDecimal(0);
        BigDecimal gpaCount = new BigDecimal(0);
        TextView gradePoint = findViewById(R.id.gradePoint);
        for (GPABean gpaBean  : Constant.sGPABeans) {
            BigDecimal credit = new BigDecimal(gpaBean.getCredit());
            BigDecimal gpa = new BigDecimal(gpaBean.getGpa());
            multiply.add(credit.multiply(gpa));
            gpaCount.add(gpa);
        }
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        double result = multiply.divide(gpaCount, BigDecimal.ROUND_HALF_UP).doubleValue();
        String format = decimalFormat.format(result);
        gradePoint.setText(format);*/


        RecyclerView recyclerView = findViewById(R.id.gpa_score_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GPAAdapter adapter = new GPAAdapter(Constant.sGPABeans);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        Constant.sGPABeans = new ArrayList<>();
        super.onStop();
    }
}
