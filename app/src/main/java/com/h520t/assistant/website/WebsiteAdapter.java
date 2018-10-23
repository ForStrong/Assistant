package com.h520t.assistant.website;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.h520t.assistant.R;

import java.util.ArrayList;

/**
 * @author Administrator
 * @des ${TODO}
 * @Version $Rev$
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.ViewHolder> {
    private ArrayList<String> urls;

    public WebsiteAdapter(ArrayList<String> urls) {
        this.urls = urls;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.website_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (null != urls.get(position)) {
            holder.httpUrl.setText(urls.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView httpUrl;
        ViewHolder(View itemView) {
            super(itemView);
            httpUrl = itemView.findViewById(R.id.http_url);
        }
    }
}
