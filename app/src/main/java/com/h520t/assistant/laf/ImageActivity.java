package com.h520t.assistant.laf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.h520t.assistant.R;

public class ImageActivity extends AppCompatActivity {
    public static final String sImageUrl = "imageUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Window window = getWindow();
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.black));
        PhotoView photoView = findViewById(R.id.laf_photoView);
        photoView.enable();
        Intent intent = getIntent();
        String url = intent.getStringExtra(sImageUrl);
        Glide.with(this).load(url).into(photoView);

        photoView.setOnClickListener(view -> finish());
    }
}
