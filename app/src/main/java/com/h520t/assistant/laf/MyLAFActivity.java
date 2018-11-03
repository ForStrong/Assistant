package com.h520t.assistant.laf;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.h520t.assistant.R;

import java.util.ArrayList;
import java.util.List;

public class MyLAFActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_laf);
        RecyclerView recyclerView = findViewById(R.id.my_laf_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        AVQuery<AVObject> avQuery = new AVQuery<>("lostProperty");
        avQuery.addDescendingOrder("data");
        avQuery.whereContains("deviceID", Build.SERIAL);
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
               runOnUiThread(()->{
                   LAFAdapter adapter = new LAFAdapter(list,MyLAFActivity.this);
                   recyclerView.setAdapter(adapter);
               });
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> finish());

    }
}
