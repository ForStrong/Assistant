package com.h520t.assistant.laf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.h520t.assistant.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class LAFAdapter extends RecyclerView.Adapter<LAFAdapter.ViewHolder> {
    private List<AVObject> mAVObjects;
    private static final String TAG = "LAFAdapter";
    private Activity mActivity;
    private Context mContext;
    public LAFAdapter(List<AVObject> arrayList,Activity activity){
        mAVObjects = arrayList;
        mActivity = activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_laf_item, parent, false);
        Log.i(TAG, "LAFAdapter: "+mAVObjects.size());
        return new ViewHolder(view);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AVObject avObject = mAVObjects.get(position);
        String phoneNumber = (String) avObject.get("phone");
        String deviceID = (String) avObject.get("deviceID");

        holder.lafPhone.setText(phoneNumber);
        holder.lafGoodName.setText((String)avObject.get("lostGoods"));
        holder.lafGoodPlace.setText((String)avObject.get("lostPlace"));
        Date date = (Date) avObject.get("data");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        holder.putTime.setText(simpleDateFormat.format(date));
        AVFile avFile = (AVFile) avObject.get("lostImg");
        String url = avFile.getUrl();
        Glide.with(mContext).load(url).into(holder.lafGoodImg);
        holder.lafGoodImg.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity,ImageActivity.class);
            intent.putExtra(ImageActivity.sImageUrl,url);
            mActivity.startActivity(intent);
        });
        if (Build.SERIAL.equals(deviceID)) {
            holder.lafMenu.setVisibility(View.VISIBLE);
            holder.lafMenu.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.lafMenu,Gravity.END);
                popupMenu.inflate(R.menu.options_menu);
                darkenBackground(0.5f);
                popupMenu.setOnDismissListener(popupMenu1 -> darkenBackground(1.0f));
                popupMenu.setOnMenuItemClickListener(item -> {
                    AVObject object = mAVObjects.get(position);
                    switch (item.getItemId()) {
                        case R.id.delete:
                            object.deleteInBackground();
                            mAVObjects.remove(position);
                            notifyDataSetChanged();
                            break;
                        case R.id.alter:
                            Intent intent = new Intent(mActivity,TheLostInformationActivity.class);
                            intent.putExtra(TheLostInformationActivity.AV_OBJECT,object);
                            mActivity.startActivity(intent);
                            break;
                        default:
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
        }else {
            holder.lafMenu.setVisibility(View.INVISIBLE);
        }
        if (phoneNumber.length()>0) {
            holder.lafPhone.setVisibility(View.VISIBLE);
            holder.lafPhone.setOnClickListener(view -> {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_DIAL);   //android.intent.action.DIAL
                intent.setData(Uri.parse("tel:"+phoneNumber));
                mActivity.startActivity(intent);
            });
        }else {
            holder.lafPhone.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mAVObjects==null?0:mAVObjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lafPhone,lafGoodName,lafGoodPlace,putTime;
        ImageView lafGoodImg,lafMenu;
        ViewHolder(View itemView) {
            super(itemView);
            lafPhone =  itemView.findViewById(R.id.laf_phone);
            lafGoodName = itemView.findViewById(R.id.laf_good_name);
            lafGoodPlace = itemView.findViewById(R.id.laf_good_place);
            lafGoodImg = itemView.findViewById(R.id.laf_good_img);
            lafMenu = itemView.findViewById(R.id.laf_menu);
            putTime = itemView.findViewById(R.id.put_time);
        }
    }

    private void darkenBackground(Float bg_color) {
        WindowManager.LayoutParams lp  = mActivity.getWindow().getAttributes();
        //alpha 0f-1.0f,An alpha value to apply to this entire window. An alpha of 1.0 means fully opaque and 0.0 means fully transparent
        lp.alpha = bg_color;
        //WindowManager.LayoutParams.FLAG_DIM_BEHIND: Window flag: everything behind this window will be dimmed.
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mActivity.getWindow().setAttributes(lp);
    }
}
