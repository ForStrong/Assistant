package com.h520t.assistant.fragment;


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

import com.h520t.assistant.R;
import com.h520t.assistant.util.RecyclerItemClickListener;
import com.h520t.assistant.util.RecyclerViewDivider;
import com.h520t.assistant.website.WebsiteAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebsiteFragment extends Fragment {
    RecyclerView mRecyclerView;
    ArrayList<String> url_name;
    String[] url = {
             "http://jwc.njit.edu.cn"
            ,"http://cwxt.njit.edu.cn/WFManager/login.jsp"
            ,"http://210.29.166.212/getSystemDepartmentPortal.action"
            ,"http://opac.lib.njit.edu.cn/opac/search.php"
            ,"http://jwjx.njit.edu.cn"
            ,"http://my.njit.edu.cn/"
            ,"http://sjjx.njit.edu.cn/"
            ,"https://english.njit.casbs.cn/"
            ,"http://sjjx.njit.edu.cn:8080/"
            ,"https://client.njit.casbs.cn/client/#/login"
            };


    public WebsiteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_website, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        url_name = new ArrayList<>();
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
                Uri uri = Uri.parse(url[position]);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }
    public static WebsiteFragment getInstance(){
        return WebsiteFragmentViewHolder.WEBSITE_FRAGMENT;
    }

    static class WebsiteFragmentViewHolder{
        static final WebsiteFragment WEBSITE_FRAGMENT = new WebsiteFragment();
    }

}
