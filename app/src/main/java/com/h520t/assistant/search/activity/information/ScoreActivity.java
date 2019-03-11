package com.h520t.assistant.search.activity.information;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.h520t.assistant.R;
import com.h520t.assistant.search.util.Constant;
import com.h520t.assistant.search.adapter.ScoreAdapter;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {
    public static final String TOOLBAR_TITLE = "toolbarTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        Toolbar toolbar = findViewById(R.id.score_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(v->finish());
        Intent intent = getIntent();
        String title = intent.getStringExtra(ScoreActivity.TOOLBAR_TITLE);
        toolbar.setTitle(title);
        RecyclerView recyclerView = findViewById(R.id.score_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ScoreAdapter adapter = new ScoreAdapter(Constant.sScoreBeans);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constant.sScoreBeans = new ArrayList<>();
    }
}
