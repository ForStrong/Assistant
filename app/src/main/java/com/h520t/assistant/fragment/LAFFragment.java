package com.h520t.assistant.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.h520t.assistant.R;
import com.h520t.assistant.laf.LAFAdapter;
import com.h520t.assistant.laf.TheLostInformationActivity;
import com.jeremyliao.livedatabus.LiveDataBus;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class LAFFragment extends Fragment {
    FloatingActionMenu fabMenu;
    FloatingActionButton fabCamera,fabAlbum;
    Uri imageUri;
    final int TAKE_PHOTO = 111;
    private final int GET_PHOTO = 222;
    private  Bitmap mBitmap;
    RecyclerView mRecyclerView;
    List<AVObject> mList ;
    SmartRefreshLayout mRefreshLayout;
    int skipCount = 0;
    Boolean mIsRefresh;
    private static final String TAG = "LAFFragment";
    private LAFAdapter mAdapter;
    private byte[] mBitmapBytes;
    Boolean isNoMore = false;
    @SuppressLint("ValidFragment")
    private LAFFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_laf, container, false);
        fabCamera = view.findViewById(R.id.fab_camera);
        fabAlbum = view.findViewById(R.id.fab_album);
        mRecyclerView = view.findViewById(R.id.laf_recycler_view);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mRefreshLayout.setEnableAutoLoadMore(false);
        fabMenu = view.findViewById(R.id.fab_menu);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LiveDataBus.get().with("key",List.class).observe(Objects.requireNonNull(getActivity()), list -> {
            if (mIsRefresh){
                mList = list;
            }else {
                mList.addAll(list);
            }
            if (mAdapter==null||mIsRefresh) {
                mAdapter = new LAFAdapter(mList, getActivity());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                //            mRecyclerView.addItemDecoration(new RecyclerViewDivider(getActivity(), LinearLayout.VERTICAL));
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }else {
                mAdapter.notifyDataSetChanged();
            }
        });
        getData(true);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            getData(true);
            if (isNoMore){
                refreshLayout.finishRefresh();
                refreshLayout.setNoMoreData(false);
            }else {
                refreshLayout.finishRefresh(1000);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            getData(false);
            if (isNoMore){
                refreshLayout.finishLoadMoreWithNoMoreData();
            }else {
                refreshLayout.finishLoadMore(1000);
            }
        });

        fabCamera.setOnClickListener(view -> getPhotoByCamera());
        fabAlbum.setOnClickListener(view -> getPhotoByAlbum());
    }

    private void getData(Boolean isRefresh) {
        mIsRefresh = isRefresh;
        AVQuery<AVObject> avQuery = new AVQuery<>("lostProperty");
        avQuery.addDescendingOrder("data");
        avQuery.setLimit(10);
        if (!isRefresh){
            skipCount += 10;
            avQuery.setSkip(skipCount);
        }
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (mList!=null&&list.size()>0){
                    if (mList.get(0).getObjectId().equals(list.get(0).getObjectId())){
                        isNoMore = true;
                        Log.i(TAG, "done: date一样");
                        return;
                    }
                }else if (list.size()==0){
                    isNoMore = true;
                }

                if (list.size()>0) {
                    LiveDataBus.get().with("key").postValue(list);
                    isNoMore = false;
                }
            }
        });
    }

    public  void getPhotoByCamera(){

        File outputImage = new File(Objects.requireNonNull(getActivity()).getExternalCacheDir(),"output_image.jpg");
        try {
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT>=24){
            imageUri = FileProvider.getUriForFile(getActivity(),
                    "com.h520t.assistant.fileprovider",outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public  void getPhotoByAlbum(){
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity())
                , Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity()
                    , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_PHOTO);
        else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,GET_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(getActivity(), "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK)
                    try {
                        mBitmap = BitmapFactory.decodeStream(Objects.requireNonNull(getActivity()).getContentResolver().openInputStream(imageUri));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        mBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                        mBitmapBytes = baos.toByteArray();
                        Intent intent = new Intent(getActivity(),TheLostInformationActivity.class);
                        intent.putExtra(TheLostInformationActivity.BITMAP_BYTES,mBitmapBytes);
                        startActivity(intent);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                break;
            case GET_PHOTO:
                if (resultCode==RESULT_OK){
                    handleImage(data);
                }
                break;
            default:
                break;
        }
    }

    private void handleImage(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getActivity(),uri)){
            String docID = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docID.split(":")[1];
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath =  getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),Long.valueOf(docID));
                imagePath = getImagePath(contentUri,null);
            }else if ("content".equals(uri.getScheme())){
                imagePath = getImagePath(uri,null);
            }else if ("file".equals(uri.getScheme())){
                imagePath = uri.getPath();
            }
        }
        if (imagePath!=null) {
            mBitmap = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG,10,stream);
            mBitmapBytes = stream.toByteArray();
            Intent intent = new Intent(getActivity(),TheLostInformationActivity.class);
            intent.putExtra(TheLostInformationActivity.BITMAP_BYTES,mBitmapBytes);
            startActivity(intent);
        }else{
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        @SuppressLint("Recycle")
        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onPause() {
        super.onPause();
        fabMenu.close(true);
    }

    public static LAFFragment getInstance(){
        return LAFFragmentHolder.SETTING_FRAGMENT;
    }

    static class LAFFragmentHolder{
        static final LAFFragment SETTING_FRAGMENT = new LAFFragment();
    }

}
