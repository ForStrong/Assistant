package com.h520t.assistant.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.h520t.assistant.R;
import com.h520t.assistant.util.RecyclerItemClickListener;
import com.h520t.assistant.util.RecyclerViewDivider;
import com.h520t.assistant.website.GlideImageLoader;
import com.h520t.assistant.website.WebsiteAdapter;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebsiteFragment extends Fragment {
    RecyclerView mRecyclerView;
    ArrayList<String> url_name;
    Banner mBanner;
    private ArrayList<Integer> images;
    String[] url = {"http://jwjx.njit.edu.cn","http://jwc.njit.edu.cn","http://english.njit.edu.cn",
            "http://opac.lib.njit.edu.cn/opac/search.php"
            ,"http://cwxt.njit.edu.cn/WFManager/login.jsp"
            ,"http://sjjx.njit.edu.cn:8080/"};

    @SuppressLint("ValidFragment")
    private WebsiteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_website, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mBanner = view.findViewById(R.id.banner);
        url_name = new ArrayList<>();
        images = new ArrayList<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] array = getResources().getStringArray(R.array.website_url);
        Collections.addAll(this.url_name, array);
        WebsiteAdapter websiteAdapter = new WebsiteAdapter(url_name);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new RecyclerViewDivider(getActivity(), LinearLayout.VERTICAL));
        mRecyclerView.setAdapter(websiteAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url[position]);
                intent.setData(uri);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        images.add(R.drawable.college1);
        images.add(R.drawable.college2);
        images.add(R.drawable.library);
        mBanner.setImages(images).setImageLoader(new GlideImageLoader()).start();
    }
    public static WebsiteFragment getInstance(){
        return WebsiteFragmentViewHolder.WEBSITE_FRAGMENT;
    }

    static class WebsiteFragmentViewHolder{
        static final WebsiteFragment WEBSITE_FRAGMENT = new WebsiteFragment();
    }

}
