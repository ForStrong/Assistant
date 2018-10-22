package com.h520t.assistant.laf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
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
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.h520t.assistant.R;
import com.h520t.assistant.fragment.LAFFragment;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TheLostInformationActivity extends AppCompatActivity {

    Toolbar mToolbar;
    EditText lostGoods,lostPlace,phone;
    ImageView lostImg;
    public static final String BITMAP_BYTES = "BYTE";
    public static final String AV_OBJECT = "avObject";
    private byte[] mBytes;
    private AVObject mObject;

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


        mObject = intent.getParcelableExtra(TheLostInformationActivity.AV_OBJECT);
        if (mObject!=null){
            lostGoods.setText((String)mObject.get("lostGoods"));
            lostPlace.setText((String)mObject.get("lostPlace"));
            phone.setText((String)mObject.get("phone"));
            AVFile file = (AVFile) mObject.get("lostImg");
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    mBytes = bytes;
                }});
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
                    if (TextUtils.isEmpty(goods)||TextUtils.isEmpty(place)) {
                        Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                    }else if ((phone.getText().toString()).length()>0&&!(isPhone(phone.getText().toString()))){
                        Toast.makeText(this, "请填写正确的手机号", Toast.LENGTH_SHORT).show();
                        phone.setText("");
                    }else{
                        putData();
                    }
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    private void putData() {
        AVObject avObject;
        if (mObject==null) {
           avObject = new AVObject("lostProperty");
        }else{
            String objectId = mObject.getObjectId();
            avObject = AVObject.createWithoutData("lostProperty",objectId);
        }
        avObject.put("deviceID", Build.SERIAL);
        Date date = new Date(System.currentTimeMillis());
        avObject.put("data",date);
        avObject.put("lostGoods",lostGoods.getText().toString());
        avObject.put("lostPlace",lostPlace.getText().toString());
        avObject.put("phone",phone.getText().toString());
        AVFile img = new AVFile("img",mBytes);
        avObject.put("lostImg",img);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e==null){
                    runOnUiThread(() -> LAFFragment.getInstance().getData(true));
                }
            }
        });
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }
    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            return m.matches();
        }
    }


}
