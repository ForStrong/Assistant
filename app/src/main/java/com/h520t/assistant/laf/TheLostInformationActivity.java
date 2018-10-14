package com.h520t.assistant.laf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetDataCallback;
import com.bumptech.glide.Glide;
import com.h520t.assistant.R;

import java.util.Date;


public class TheLostInformationActivity extends AppCompatActivity {

    Toolbar mToolbar;
    EditText lostGoods,lostPlace,phone;
    ImageView lostImg;
    private static final String TAG = "TheLostInformationActivity";
    public static final String BITMAP_BYTES = "BYTE";
    public static final String AV_OBJECT = "avObject";
    private byte[] mBytes;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_the_lost_information);
        initView();

    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        lostGoods = findViewById(R.id.lostGoods);
        lostPlace = findViewById(R.id.lostPlace);
        phone = findViewById(R.id.phone);
        lostImg = findViewById(R.id.lostImg);
        Intent intent = getIntent();
        mBytes = intent.getByteArrayExtra(TheLostInformationActivity.BITMAP_BYTES);
        if (mBytes!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length);
            lostImg.setImageBitmap(bitmap);
        }
        AVObject object = intent.getParcelableExtra(TheLostInformationActivity.AV_OBJECT);
        if (object!=null){
            lostGoods.setText((String)object.get("lostGoods"));
            lostPlace.setText((String)object.get("lostPlace"));
            phone.setText((String)object.get("phone"));
            AVFile file = (AVFile) object.get("lostImg");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    mBytes = bytes;
                }
            });
            Glide.with(this).load(file.getUrl()).into(lostImg);
        }

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(v->finish());
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.publish:
                    String goods = lostGoods.getText().toString();
                    String place = lostPlace.getText().toString();
                    if (!(TextUtils.isEmpty(goods)&&TextUtils.isEmpty(place))) {
                        Toast.makeText(this, "put", Toast.LENGTH_SHORT).show();
                        putData();
                    }else{
                        Toast.makeText(this, "失物物品和失物地点是必填项", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            return true;
        });


    }

    private void putData() {
        AVObject avObject = new AVObject("lostProperty");
        avObject.put("deviceID", Build.SERIAL);
        Date date = new Date(System.currentTimeMillis());
        avObject.put("data",date);
        avObject.put("lostGoods",lostGoods.getText().toString());
        avObject.put("lostPlace",lostPlace.getText().toString());
        avObject.put("phone",phone.getText().toString());
        AVFile img = new AVFile("img",mBytes);
        avObject.put("lostImg",img);
        avObject.saveInBackground();
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

/*    public static boolean isMobileNO(String tel) {
        String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        Pattern p = Pattern.compile(PHONE_NUMBER_REG);
        Matcher m = p.matcher(tel);
        return m.matches();
    }*/


}
